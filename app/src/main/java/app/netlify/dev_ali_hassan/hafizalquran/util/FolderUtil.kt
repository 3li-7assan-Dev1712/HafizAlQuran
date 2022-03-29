package app.netlify.dev_ali_hassan.hafizalquran.util

import android.app.DownloadManager
import android.app.DownloadManager.Request.VISIBILITY_VISIBLE
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Context.MODE_PRIVATE
import android.database.Cursor
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import app.netlify.dev_ali_hassan.hafizalquran.api.dataclasses.Ayah
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import javax.inject.Inject

private const val MB = 1024000
class FolderUtil @Inject constructor(@ApplicationContext val context: Context) {

    // TAG for logging and debugging and will be removed after finishing the debug version
    // then all


    private val TAG = "FolderUtil"

    fun storeAudioInInternalStorage(fileName: String, data: ByteArray): Boolean {

        return try {
            context.openFileOutput(
                fileName,
                MODE_PRIVATE
            ).use { stream ->
                stream.write(data)
            }
            true
        } catch (exception: IOException) {
            exception.printStackTrace()
            false
        }

    }

    fun loadFilesFromStorage(): List<File> {
        val files = context.filesDir.listFiles()
        return try {
            files?.filter { it.name.endsWith(".mp3") }?.map {
                it
            } ?: listOf()
        } catch (ioe: IOException) {
            ioe.printStackTrace()
            listOf()
        }

    }

    fun provideMediaPlayer(ayah: Ayah): MediaPlayer {

//        val uri = Uri.parse(ayah.audio)

        val player =MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(ayah.audio)
            prepare()

        }

        return player
    }
    suspend fun downloadAyasIntoOnePage(ayahs: List<Ayah>) {


        withContext(Dispatchers.IO) {

            var count: Int = 0

            ayahs.forEach { ayah ->
                val audioUrl = ayah.audio
                val root = context.filesDir
                val file = File(root, "${ayah.page}.mp3")

                val request = DownloadManager.Request(Uri.parse(audioUrl))
                    .setNotificationVisibility(VISIBILITY_VISIBLE)
                    .setTitle(ayah.surah.name)
                    .setDescription("Downloading...")
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)
//                    .setDestinationUri(Uri.fromFile(file))

                val donwloadManager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val downloadId = donwloadManager.enqueue(request)


                var finishDownload = false

                while (!finishDownload) {

                    val cursor =
                        donwloadManager.query(DownloadManager.Query().setFilterById(downloadId))
                    if (cursor.moveToFirst()) {
                        val index = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                        val status =
                            cursor.getInt(index)
                        finishDownload = handleStatus(status, cursor)
                        Log.d(TAG, "downloadAyasIntoOnePage: the result is $finishDownload")

                    }
                }

//                val url = URL(audioUrl)
                /*try {
                    val conexsion = url.openConnection()
                    conexsion.connect()
                    val inputStream = BufferedInputStream(url.openStream())

                    val data = ByteArray(MB * 10)
                    var total = 0
                    while (count != -1) {
                        count = inputStream.read(data)
                        total += count
                        context.openFileOutput("${ayah.page}.mp3", MODE_PRIVATE).use { stream ->
                            stream.write(data, 0, count)
//                            stream.write(count)
                        }

                    }
                } catch (maoformedException: MalformedURLException) {
                    maoformedException.printStackTrace()
                } catch (protocolException: ProtocolException) {
                    protocolException.printStackTrace()
                } catch (ioe: IOException) {
                    ioe.printStackTrace()
                }*/

            }
        }

    }

    private fun handleStatus(status: Int, cursor: Cursor): Boolean {
        var progress = 0
        when (status) {
            DownloadManager.STATUS_FAILED -> {
                return true
            }
            DownloadManager.STATUS_RUNNING -> {
                val totalIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                val downloadIndex =
                    cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                val total = cursor.getLong(totalIndex)
                if (total >= 0) {
                    val downloaded = cursor.getLong(downloadIndex)
                    progress = ((downloaded * 100) / total).toInt()
                    Log.d(TAG, "handleStatus: the progress now is $progress%")
                }

            }
            DownloadManager.STATUS_SUCCESSFUL -> {
                progress = 100

                Log.d(TAG, "handleStatus: download completed successfully")
                return true

            }
        }
        return false
    }
}