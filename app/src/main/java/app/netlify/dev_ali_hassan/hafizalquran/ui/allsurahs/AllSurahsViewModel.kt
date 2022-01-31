package app.netlify.dev_ali_hassan.hafizalquran.ui.allsurahs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import app.netlify.dev_ali_hassan.hafizalquran.data.SurahsDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AllSurahsViewModel @Inject constructor(
    val surahsDao: SurahsDao
) : ViewModel() {


    val surahs = surahsDao.getAllSuar()
    val surahsLiveData = surahs.asLiveData()

}