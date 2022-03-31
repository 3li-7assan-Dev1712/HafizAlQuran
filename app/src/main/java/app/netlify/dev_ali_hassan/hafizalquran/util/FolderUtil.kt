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
import android.os.Environment
import android.util.Log
import app.netlify.dev_ali_hassan.hafizalquran.api.dataclasses.Ayah
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
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

        val player = MediaPlayer().apply {
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

    fun downloadAyasIntoOnePage(ayahs: List<Ayah>): Boolean {
        var counter = ayahs.size
        val root =
            File(
                "${context.applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)}/.hafizalquran",
                "${ayahs[0].page}.mp3"

            )
        // if the file is already exists that means it is already downloaded, so return.
        if (root.exists()) {
            Log.d(TAG, "downloadAyasIntoOnePage: return directly the file is already exists!")
            return true
        }
        val appFolder = File(
            "${context.applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)}"
        )
        val folder = File(appFolder, ".hafizalquran")
        if (!folder.exists()) {
            folder.mkdirs()
            Log.d(TAG, "downloadAyasIntoOnePage: created folder")
        }

        val ayahsList = mutableListOf<File>()
        val finalFile = File(folder, "${ayahs[0].page}.mp3")
        ayahs.forEach { ayah ->
            val audioUrl = ayah.audio

            val subFile = File(folder, "${ayah.surah.englishName}${ayah.number}.mp3")

            val request = DownloadManager.Request(Uri.parse(audioUrl))
                .setNotificationVisibility(VISIBILITY_VISIBLE)
                .setTitle(ayah.surah.name)
                .setDescription("Downloading...")
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .setDestinationUri(Uri.fromFile(subFile))

            val downloadManager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val downloadId = downloadManager.enqueue(request)

            var finishDownload = false
            while (!finishDownload) {

                val cursor =
                    downloadManager.query(DownloadManager.Query().setFilterById(downloadId))
                if (cursor.moveToFirst()) {
                    var index = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    if (index == -1) index = 0
                    val status =
                        cursor.getInt(index)
                    finishDownload = handleStatus(status, cursor)
                    if (finishDownload) {
                        counter--
                        ayahsList.add(subFile)
                    }
                }
                cursor?.close()
            }
            if (counter == 0) {
                Log.d(TAG, "downloadAyasIntoOnePage: all ayahs is downloaded")
            } else {
                Log.d(TAG, "downloadAyasIntoOnePage: counter now is $counter")
            }
        }
        Log.d(TAG, "downloadAyasIntoOnePage: there are ${ayahsList.size} ayahs here")
        // after downloading all the sub files merge them together with this extension function
        finalFile.appendAll(fileList = ayahsList)
        val data = finalFile.readBytes()
        Log.d(TAG, "downloadAyasIntoOnePage: the size of the data is ${data.size}")
        return storeAudioInInternalStorage(finalFile.name, finalFile.readBytes())

        /*  if (re) {

              Log.d(
                  TAG,
                  "downloadAyasIntoOnePage: the download is completed successfully"
              )
          }*/
        /*try {
            Log.d(
                TAG,
                "downloadAyasIntoOnePage: isFile ${finalFile.isFile}, is dir ${finalFile.isDirectory}"
            )

            Log.d(TAG, "everything went as expected the file name is ${finalFile.name}")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(
                TAG,
                "downloadAyasIntoOnePage: there is an exception with message ${e.message}"
            )
        }

*/
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

    fun File.appendAll(bufferSize: Int = 512, fileList: List<File>) {
        /*if (!exists()) {
            throw NoSuchFileException(this, null, "File dosen't exist")
        }*/
        require(!isDirectory) {
            "The file is a directory"
        }
        FileOutputStream(this, true).use { output ->
            for (file in fileList) {
                Log.d(TAG, "appendAll: inside the loop the file name is ${file.name}")
                if (file.isDirectory || !file.exists()) {
                    continue
                }

                file.forEachBlock(bufferSize) { buffer, bytesRead ->
                    output.write(buffer, 0, bytesRead)
                    Log.d(TAG, "appendAll: copying to the final file")
                }

            }

        }


    }
}