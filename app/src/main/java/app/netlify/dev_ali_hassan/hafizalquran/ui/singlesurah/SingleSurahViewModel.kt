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


    fun provideFakeData() =
        listOf(
            Page(2, 2, true),
            Page(2, 3, false),
            Page(2, 4, false),
            Page(2, 5, false),
            Page(2, 6, false),
            Page(2, 7, false),
            Page(2, 8, false),
            Page(2, 9, false)
        )


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
    }



        fun getNameOfSurahByIndex(index: Int): String =
            surahsNames[index - 1]


        val surahsNames =
            listOf("alfatiah", "albaqarah", "al-imran", "an-nisa", "almaidah", "al-anam")
    }
