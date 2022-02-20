package app.netlify.dev_ali_hassan.hafizalquran.ui.memorizepage

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import javax.inject.Inject

const val MAX_SIZE_OF_AUDIO: Long = 1024 * 1024 * 10 //10MB

@HiltViewModel
class MemorizePageViewModel @Inject constructor (
    @ApplicationContext val context: Context
) : ViewModel() {

    private val eventsChannel = Channel<MemorizePageEvents>()
    val eventsFlow = eventsChannel.receiveAsFlow()

    private val storage = FirebaseStorage.getInstance()

    private val TAG = "MemorizePageViewModel"

    fun downloadAudioFileFromStorage(audioName: String) {

        storage.getReference(audioName).getBytes(MAX_SIZE_OF_AUDIO).addOnSuccessListener {
            val result = storeFileInInternalStorage(it, audioName)
            if (result)
                Log.d(TAG, "downloadAudioFileFromStorage: saved file from storage successfully")
            else
                Log.w(TAG, "downloadAudioFileFromStorage: couldn't store file!")
        }.addOnFailureListener {
            it.printStackTrace()
            Log.d(TAG, "downloadAudioFileFromStorage: exception with message ${it.message}")
        }.addOnCompleteListener {
            showAudioDownloadedSuccessfullyMessage()
        }

    }

    private fun showAudioDownloadedSuccessfullyMessage() {
        viewModelScope.launch {
            eventsChannel.send(MemorizePageEvents.AudioDownloadCompleted)
        }
    }


    private fun storeFileInInternalStorage(data: ByteArray, audioFileName: String): Boolean {
        return try {
            context.openFileOutput(
                audioFileName,
                Context.MODE_PRIVATE
            ).use { stream ->
                stream.write(data)
            }
            true
        } catch (exception: IOException) {
            exception.printStackTrace()
            false
        }

    }


    private fun loadFileFromInternalStorage(): List<File> {
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

    fun playMedia(fileName: String) {

        val files = loadFileFromInternalStorage().filter { it.name == fileName }
        if (files.isNotEmpty()) {
            val file = files[0]
            Log.d(
                app.netlify.dev_ali_hassan.hafizalquran.ui.allsurahs.TAG,
                "playMedia: the name of the file is ${file.name}"
            )
            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(file.path)
                prepare()
                start()
            }
        } else {
            Log.d(
                TAG,
                "playMedia: file is empty"
            )
        }

    }

    sealed class MemorizePageEvents {
        object AudioDownloadCompleted: MemorizePageEvents()
    }
}