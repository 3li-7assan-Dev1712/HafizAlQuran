package app.netlify.dev_ali_hassan.hafizalquran.ui.singlesurah

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.netlify.dev_ali_hassan.hafizalquran.data.daos.PageDao
import app.netlify.dev_ali_hassan.hafizalquran.data.models.Page
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/*all the business logic from the SingleSurahFragment will be delegated to this
* ViewModel*/
@HiltViewModel
class SingleSurahViewModel @Inject constructor(
    private val pageDao: PageDao
) : ViewModel() {

    private val TAG = "SingleSurahViewModel"

    private val eventChannel = Channel<SingleSurahEvents>()
    val eventsFlow = eventChannel.receiveAsFlow()

    fun getPagesOfSurahWithId(surahId: Int): Flow<List<Page>> =
        pageDao.getSurahById(surahId)

    fun fragmentHasOpen() {
        viewModelScope.launch {
            Log.d(TAG, "going to order fragment to hide the bottom nav")
            eventChannel.send(SingleSurahEvents.HideBottomNavGraph)
        }
    }

    fun userClickedPage(clickedPage: Page, position: Int) {
        Log.d(TAG, "userClickedPage: giving the fragment the order of navigating to that screen")
        val surahName = getNameOfSurahByIndex(clickedPage.surahIdPageIn)
        viewModelScope.launch {
            eventChannel.send(SingleSurahEvents.UserChoosePage(clickedPage, surahName, position))
        }
    }


    sealed class SingleSurahEvents {
        data class UserChoosePage(val choosedPage: Page, val surahName: String, val position: Int) :
            SingleSurahEvents()
        object HideBottomNavGraph: SingleSurahEvents()
    }



        fun getNameOfSurahByIndex(index: Int): String {

            if (index <= surahsNames.size) {
                return surahsNames[index - 1]
            }
            return surahsNames[0]
        }


        val surahsNames =
            listOf("alfatiah", "albaqarah", "al-imran", "an-nisa", "almaidah", "al-anam")
    }
