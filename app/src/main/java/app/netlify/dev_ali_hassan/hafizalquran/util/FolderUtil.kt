package app.netlify.dev_ali_hassan.hafizalquran.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.IOException
import javax.inject.Inject

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
}