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
import app.netlify.dev_ali_hassan.hafizalquran.api.dataclasses.Ayah
import app.netlify.dev_ali_hassan.hafizalquran.api.dataclasses.QuranApiResponse
import app.netlify.dev_ali_hassan.hafizalquran.data.daos.PageDao
import app.netlify.dev_ali_hassan.hafizalquran.data.models.Page
import app.netlify.dev_ali_hassan.hafizalquran.repository.QuranRepository
import app.netlify.dev_ali_hassan.hafizalquran.util.CountDownTimerUtil
import app.netlify.dev_ali_hassan.hafizalquran.util.FolderUtil
import app.netlify.dev_ali_hassan.hafizalquran.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.File
import javax.inject.Inject


// the tag will be used to make logging for dibugging requirements
const val TAG = "MemorizePageViewModel"

/**
 * Memorize View Model will handle the memorization functionality like (download from server ->
 * save in the internal storage -> play the audio for the user and repeat it as much as the user wants)
 */
@HiltViewModel
class MemorizePageViewModel @Inject constructor(
    private val quranRepository: QuranRepository,
    var folderUtil: FolderUtil,
    val pageDao: PageDao,
    stateHandle: SavedStateHandle
) : ViewModel() {

    // pause indicator
    private var mediaPlayerIsPaused = false

    private var mTimer: CountDownTimerUtil? = null

    private var currentPosition = 0


    // page data
    val pageDataFromServer: MutableLiveData<Resource<QuranApiResponse>> = MutableLiveData()

    // this channel for making connection between the fragment and this view model
    private val eventsChannel = Channel<MemorizePageEvents>()

    // receive the channel as flow to collect it in the fragment
    val eventsFlow = eventsChannel.receiveAsFlow()

    // the seelcted page from the SingleSurahFragment
    private var selectedPage = stateHandle.get<Page>("choosedPage")


    /* the surah name from which it is passed as argument when navigating to memorize page fragment
    and using the SavedStateHandle to get it, this will also help us when the process of our app
    is killed
     */
    private val surahName = stateHandle.get<String>("surahName")

    // the player to be used to control the audio
    private var mPlayer: MediaPlayer? = null

    fun getPageData() = viewModelScope.launch {
        pageDataFromServer.postValue(Resource.Loading())
        if (selectedPage != null) {
            val response = quranRepository.getPageOfNumber(selectedPage!!.pageNumber)
            pageDataFromServer.postValue(handleQuranApiResponse(response))
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


    /**
     * This method will update the state of the page in the database. make the isDownloaded
     * true, then it will show a dialog to let the user know it is successfully downloaded.
     */
    private fun updatePageDownloadStateAndShowSuccessfulMsg() {
        val updatedPage =
            selectedPage?.copy(isDownloaded = true, pageText = folderUtil.ayahsInString)
        viewModelScope.launch {
            eventsChannel.send(MemorizePageEvents.AudioDownloadCompleted)

            updatedPage?.also {
                pageDao.updatePage(it)
            }

        }
        selectedPage = updatedPage
    }


    /**
     * This method will load the file from the internal storage to play for the user
     *
     * @return a list of all files in the application specific location.
     */
    private fun loadFileFromInternalStorage(): List<File> {
        return folderUtil.loadFilesFromStorage()

    }


    private fun initTimer(duration: Long) {
        mTimer = object : CountDownTimerUtil(duration, 0) {
            override fun onTimerTick(millisUntilFinished: Long) {
                Log.d(TAG, "onTimerTick: $millisUntilFinished until finish")
            }

            override fun onTimerFinish() {
                repeatTopTerm()
            }
        }
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
                "userClickedPlayBtn: pageNumber ${selectedPage!!.pageNumber} in surah number ${selectedPage!!.id}"
            )
            if (selectedPage!!.isDownloaded) {
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
//            downloadAudioFileFromStorage(surahName)
            getPageData()
        }

    }

    /**
     * This method will play the downloaded media to the user using the MediaPlayer.
     */
    private fun playMedia() {

        if (mPlayer?.isPlaying == true) {
            mPlayer!!.pause()
            mediaPlayerIsPaused = true
            sendPlayPauseEventBtn(true)
            mTimer?.pause()
            return

        } else if (mediaPlayerIsPaused) {
            mPlayer?.start()
            mTimer?.start()
            mediaPlayerIsPaused = false
            sendPlayPauseEventBtn(isPlay = false)
            return
        }

        val files =
            loadFileFromInternalStorage().filter { it.name == "${selectedPage?.pageNumber}.mp3" }
        if (files.isNotEmpty()) {
            val file = files[0]
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
                sendPlayPauseEventBtn(false)

            }
        } else {
            Log.d(
                TAG,
                "playMedia: file is empty"
            )
        }

    }


    private fun sendPlayPauseEventBtn(isPlay: Boolean) =
        viewModelScope.launch {
            eventsChannel.send(MemorizePageEvents.PlayPauseEvent(isPlay))
        }

    suspend fun receivedAyahsSuccessfully(ayahs: List<Ayah>) {
        storeAyahsIntoOneFile(ayahs)

    }

    private suspend fun storeAyahsIntoOneFile(ayahs: List<Ayah>) {
        Log.d(TAG, "storeAyahsIntoOneFile: going to store the ayahs into one file...")

        val downloadSuccessful = folderUtil.downloadAyasIntoOnePage(ayahs)
        if (downloadSuccessful) {
            updatePageDownloadStateAndShowSuccessfulMsg()
            playMedia()
            cachePageTextInDatabase(folderUtil.ayahsInString)
        } else {
            showErrorMessage()
        }

    }


    private fun cachePageTextInDatabase(pageText: String) {
        viewModelScope.launch {
            val updatedPage = selectedPage?.copy(pageText = pageText, isDownloaded = true)
            if (updatedPage != null) {
                pageDao.updatePage(updatedPage)
            }
        }
    }

    /**
     * This function will be called when the fragment is going to be destroyed
     * to release the player, because the user leave the screen which indicates
     * they don't want to keep listening.
     */
    fun fragmentDestroyed() {
        mPlayer?.release()
    }

    fun repeatTopTerm() {
        /*if (!mediaPlayerIsPaused) return*/
        currentPosition = mPlayer?.currentPosition ?: 0
        val duration = mPlayer?.currentPosition ?: 0
        initTimer(duration.toLong())
        mPlayer?.pause()
        mPlayer?.seekTo(0)
        mPlayer?.start()
        mTimer?.start()
    }


    /**
     * This sealed class is used to express the events of the memorizing page functionality.
     */
    sealed class MemorizePageEvents {
        data class PlayPauseEvent(val isPlay: Boolean) : MemorizePageEvents()
        object AudioDownloadCompleted : MemorizePageEvents()
        object ErrorEvent : MemorizePageEvents()
        object DownloadConfirmationEvent : MemorizePageEvents()
        object AudioIsNotAvailable : MemorizePageEvents()
    }
}