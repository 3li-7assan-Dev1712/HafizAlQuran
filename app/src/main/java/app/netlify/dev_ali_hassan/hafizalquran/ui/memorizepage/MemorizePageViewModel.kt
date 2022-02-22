package app.netlify.dev_ali_hassan.hafizalquran.ui.memorizepage

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.netlify.dev_ali_hassan.hafizalquran.data.daos.PageDao
import app.netlify.dev_ali_hassan.hafizalquran.data.models.Page
import com.google.firebase.storage.FirebaseStorage
//import dagger.assisted.Assisted
//import androidx.hilt.Assisted

import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import javax.inject.Inject

const val MAX_SIZE_OF_AUDIO: Long = 1024 * 1024 * 10 //10MB

@HiltViewModel
class MemorizePageViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    val pageDao: PageDao,
    stateHandle: SavedStateHandle
) : ViewModel() {

    private val eventsChannel = Channel<MemorizePageEvents>()
    val eventsFlow = eventsChannel.receiveAsFlow()

    private val storage = FirebaseStorage.getInstance()

    private val TAG = "MemorizePageViewModel"

    private val selectedPage = stateHandle.get<Page>("choosedPage")
    private val surahName = stateHandle.get<String>("surahName")
    private val pagePosition = stateHandle.get<Int>("position")


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
            // show error to the user
            showAudioIsNotAvailableMsg()

        }.addOnCompleteListener {
            showAudioDownloadedSuccessfullyMessage()
        }

    }

    private fun showAudioIsNotAvailableMsg() {
        viewModelScope.launch {
            eventsChannel.send(MemorizePageEvents.AudioIsNotAvailable)
        }
    }

    private fun showAudioDownloadedSuccessfullyMessage() {
        val updatedPage = selectedPage?.copy(isDownloaded = true)
        viewModelScope.launch {
            eventsChannel.send(MemorizePageEvents.AudioDownloadCompleted)

            // update the page in the database
            updatedPage?.also {
                pageDao.updatePage(it)
            }

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

    fun userClickedPlayBtn() {
        if (selectedPage != null) {
            if (selectedPage.isDownloaded) {
                playMedia()
            } else {
                showConfirmationDownloadMessage()
            }
        } else {
            showErrorMessage()
        }
    }

    private fun showErrorMessage() {
        viewModelScope.launch {
            eventsChannel.send(MemorizePageEvents.ErrorEvent)
        }
    }

    private fun showConfirmationDownloadMessage() {
        viewModelScope.launch {
            eventsChannel.send(MemorizePageEvents.DownloadConfirmationEvent)
        }
    }

    fun userConfirmDownloadOperation() {
        if (surahName != null) {
            downloadAudioFileFromStorage(surahName)
        }

    }

    fun playMedia() {

        val files = loadFileFromInternalStorage().filter { it.name == "${surahName}${pagePosition}"}
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
        object AudioDownloadCompleted : MemorizePageEvents()
        object ErrorEvent : MemorizePageEvents()
        object DownloadConfirmationEvent : MemorizePageEvents()
        object AudioIsNotAvailable : MemorizePageEvents()
    }
}