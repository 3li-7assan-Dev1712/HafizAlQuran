package app.netlify.dev_ali_hassan.hafizalquran.ui.memorizepage

//import dagger.assisted.Assisted
//import androidx.hilt.Assisted

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.netlify.dev_ali_hassan.hafizalquran.api.dataclasses.QuranApiResponse
import app.netlify.dev_ali_hassan.hafizalquran.data.daos.PageDao
import app.netlify.dev_ali_hassan.hafizalquran.data.models.Page
import app.netlify.dev_ali_hassan.hafizalquran.repository.QuranRepository
import app.netlify.dev_ali_hassan.hafizalquran.util.FolderUtil
import app.netlify.dev_ali_hassan.hafizalquran.util.Resource
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.File
import javax.inject.Inject

// this constant value indicates the maximum size of audio file which is 10MB
const val MAX_SIZE_OF_AUDIO: Long = 1024 * 1024 * 10 //10MB

// the tag will be used to make logging for dibugging requirements
const val TAG = "MemorizePageViewModel"

/**
 * Memorize View Model will handle the memorization functionality like (download from server ->
 * save in the internal storage -> play the audio for the user and repeat it as much as the user wants)
 */
@HiltViewModel
class MemorizePageViewModel @Inject constructor(
    val quranRepository: QuranRepository,
    var folderUtil: FolderUtil,
    val pageDao: PageDao,
    stateHandle: SavedStateHandle
) : ViewModel() {

    // page data
    val pageData: MutableLiveData<Resource<QuranApiResponse>> = MutableLiveData()

    // this channel for making connection between the fragment and this view model
    private val eventsChannel = Channel<MemorizePageEvents>()

    // receive the channel as flow to collect it in the fragment
    val eventsFlow = eventsChannel.receiveAsFlow()

    // the storage instance of firebase
    private val storage = FirebaseStorage.getInstance()

    // the seelcted page from the SingleSurahFragment
    private val selectedPage = stateHandle.get<Page>("choosedPage")

    /* the surah name from which it is passed as argument when navigating to memorize page fragment
    and using the SavedStateHandle to get it, this will also help us when the process of our app
    is killed
     */
    private val surahName = stateHandle.get<String>("surahName")

    // the position of the selected page passed as arguemnt as we also get it iwth state handle
    private val pagePosition = stateHandle.get<Int>("position")

    // the player to be used to control the audio
    private lateinit var mPlayer: MediaPlayer

    private fun getPageData() = viewModelScope.launch {
        pageData.postValue(Resource.Loading())
        if (selectedPage != null) {
            val response = quranRepository.getPageOfNumber(selectedPage.pageNumber)
            pageData.postValue(handleQuranApiResponse(response))
        } else
            Log.e(TAG, "selected page cannot be null")
    }

    private fun handleQuranApiResponse(response: Response<QuranApiResponse>): Resource<QuranApiResponse> {
        if (response.isSuccessful) {
            response.body()?.let { result ->
                return Resource.Success(result)
            }

        }
        return Resource.Error(message = response.message())
    }

    /** this method is responsible for downloading the audio from the storage in firebase
     *
     * @param audioName: the name of the audio to be used for download the audio.
     */
    private fun downloadAudioFileFromStorage(audioName: String) {

        Log.d(TAG, "downloadAudioFileFromStorage: audio name is $audioName")

        val location = "$audioName$pagePosition.mp3"
        Log.d(TAG, "the location where file is stored is $location")

        storage.getReference(location).getBytes(MAX_SIZE_OF_AUDIO).addOnSuccessListener {
            val result = storeFileInInternalStorage(it, location)
            if (result) {
                Log.d(TAG, "downloadAudioFileFromStorage: saved file from storage successfully")
                updatePageDownloadStateAndShowSuccessfulMsg()
            } else
                Log.w(TAG, "downloadAudioFileFromStorage: couldn't store file!")
        }.addOnFailureListener {
            it.printStackTrace()
            Log.d(TAG, "downloadAudioFileFromStorage: exception with message ${it.message}")
            // show error to the user
            showAudioIsNotAvailableMsg()

        }

    }

    /**
     * when the audio is not in the server show a dialog to the user to infrom them
     */
    private fun showAudioIsNotAvailableMsg() {
        viewModelScope.launch {
            eventsChannel.send(MemorizePageEvents.AudioIsNotAvailable)
        }
    }

    /**
     * This method will update the state of the page in the database. make the isDownloaded
     * true, then it will show a dialog to let the user know it is successfully downloaded.
     */
    private fun updatePageDownloadStateAndShowSuccessfulMsg() {
        val updatedPage = selectedPage?.copy(isDownloaded = true)
        viewModelScope.launch {
            eventsChannel.send(MemorizePageEvents.AudioDownloadCompleted)

            updatedPage?.also {
                pageDao.updatePage(it)
            }

        }
    }

    /**
     * This method will store the audio in the internal storage
     * after download it from the firebase storage
     *
     * @param data: the byte array of the audio to store it in the internal storage
     * @param audioFileName: the name of the file to use it as the same name in the internal
     * storage
     * @return a boolean type true if the audio is downloaded successfully otherwise false.
     */

    private fun storeFileInInternalStorage(data: ByteArray, audioFileName: String): Boolean =
        folderUtil.storeAudioInInternalStorage(audioFileName, data = data)


    /**
     * This method will load the file from the internal storage to play for the user
     *
     * @return a list of all files in the application specific location.
     */
    private fun loadFileFromInternalStorage(): List<File> {
        return folderUtil.loadFilesFromStorage()

    }


    /**
     * This method will be called from the fragment to tell the view model that the user clicked
     * the play button. Then the view model do some business logic. Tell the fragment what to do
     * after executing this business logic.
     */
    fun userClickedPlayBtn() {
        Log.d(TAG, "userClickedPlayBtn: works")
        if (selectedPage != null) {
            Log.d(
                TAG,
                "userClickedPlayBtn: pageNumber ${selectedPage.pageNumber} in surah number ${selectedPage.id}"
            )
            if (selectedPage.isDownloaded) {
                playMedia()
            } else {
                showConfirmationDownloadMessage()
            }
        } else {
            showErrorMessage()
            Log.d(TAG, "userClickedPlayBtn: the selected page cannot be null")
        }
    }

    /**
     * This method show an error message when a problem occurs
     */
    private fun showErrorMessage() {
        viewModelScope.launch {
            eventsChannel.send(MemorizePageEvents.ErrorEvent)
        }
    }

    /**
     * This method show a confirmation message to ask the user if they are sure about
     * a particular action.
     */
    private fun showConfirmationDownloadMessage() {
        Log.d(TAG, "showConfirmationDownloadMessage: works")
        viewModelScope.launch {
            eventsChannel.send(MemorizePageEvents.DownloadConfirmationEvent)
        }
    }

    fun userConfirmDownloadOperation() {
        if (surahName != null) {
            downloadAudioFileFromStorage(surahName)
        }

    }

    /**
     * This method will play the downloaded media to the user using the MediaPlayer.
     */
    private fun playMedia() {

        val files =
            loadFileFromInternalStorage().filter { it.name == "${surahName}${pagePosition}.mp3" }
        if (files.isNotEmpty()) {
            val file = files[0]
            Log.d(
                app.netlify.dev_ali_hassan.hafizalquran.ui.allsurahs.TAG,
                "playMedia: the name of the file is ${file.name}"
            )
            mPlayer = MediaPlayer().apply {
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

    fun userClickedPauseBtn() {
        mPlayer.pause()
    }

    /**
     * This sealed class is used to express the events of the memorizing page functionality.
     */
    sealed class MemorizePageEvents {
        object AudioDownloadCompleted : MemorizePageEvents()
        object ErrorEvent : MemorizePageEvents()
        object DownloadConfirmationEvent : MemorizePageEvents()
        object AudioIsNotAvailable : MemorizePageEvents()
    }
}