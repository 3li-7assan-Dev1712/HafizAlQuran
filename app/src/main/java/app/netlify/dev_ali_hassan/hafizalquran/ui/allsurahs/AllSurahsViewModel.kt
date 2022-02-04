package app.netlify.dev_ali_hassan.hafizalquran.ui.allsurahs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import app.netlify.dev_ali_hassan.hafizalquran.data.models.Surah
import app.netlify.dev_ali_hassan.hafizalquran.data.SurahsDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllSurahsViewModel @Inject constructor(
    surahsDao: SurahsDao
) : ViewModel() {


    private val surahs = surahsDao.getAllSuar()
    val surahsLiveData = surahs.asLiveData()

    /*we will use a channel to communicate between the fragment the and the ViewModel*/
    private val surahEventsChannel = Channel<SurahsEvents>()
    val surahEventsFlow = surahEventsChannel.receiveAsFlow()

    fun surahIsSelected(selectedSurah: Surah) {
        viewModelScope.launch {
            // tell the fragment what to navigate to SingleSurahFragment
            surahEventsChannel.send(SurahsEvents.NavigateToSingleSurahFragment(selectedSurah))
        }
    }
    sealed class SurahsEvents {
        data class NavigateToSingleSurahFragment(val surah: Surah): SurahsEvents()
    }
}