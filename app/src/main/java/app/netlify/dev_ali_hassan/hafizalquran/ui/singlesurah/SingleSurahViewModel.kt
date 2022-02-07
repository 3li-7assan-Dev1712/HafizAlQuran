package app.netlify.dev_ali_hassan.hafizalquran.ui.singlesurah

import androidx.lifecycle.ViewModel
import app.netlify.dev_ali_hassan.hafizalquran.data.daos.PageDao
import app.netlify.dev_ali_hassan.hafizalquran.data.models.Page
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/*all the business logic from the SingleSurahFragment will be delegated to this
* ViewModel*/
@HiltViewModel
class SingleSurahViewModel @Inject constructor(
    private val pageDao: PageDao
): ViewModel() {


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
}