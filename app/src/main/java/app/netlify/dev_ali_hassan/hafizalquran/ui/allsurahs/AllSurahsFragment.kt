package app.netlify.dev_ali_hassan.hafizalquran.ui.allsurahs

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import app.netlify.dev_ali_hassan.hafizalquran.R
import app.netlify.dev_ali_hassan.hafizalquran.data.Surah
import app.netlify.dev_ali_hassan.hafizalquran.data.SurahRoot
import app.netlify.dev_ali_hassan.hafizalquran.databinding.AllSurahsFragmentBinding

/**
 * This fragment will be responsible for displaying all Surahs in Quran.
 * All the work (business logic will be delegated to a dedicated view model).
 */
class AllSurahsFragment : Fragment(R.layout.all_surahs_fragment) {

    private lateinit var binding: AllSurahsFragmentBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = AllSurahsAdapter()
        binding = AllSurahsFragmentBinding.bind(view)
        binding.apply {
            allSurahsRecyclerView.adapter = adapter
        }
        adapter.submitList(provideListSurahForTest())
    }
}

fun provideListSurahForTest(): List<Surah> {
    val list = mutableListOf<Surah>()
    for (i in 0.. 113)  {
        list.add(Surah("al-Maidah", SurahRoot.MAKIA, i +1, 2))
    }
    return list
}