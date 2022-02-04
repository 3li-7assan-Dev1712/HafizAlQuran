package app.netlify.dev_ali_hassan.hafizalquran.ui.singlesurah

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import app.netlify.dev_ali_hassan.hafizalquran.R
import app.netlify.dev_ali_hassan.hafizalquran.data.models.Page
import app.netlify.dev_ali_hassan.hafizalquran.data.models.Surah
import app.netlify.dev_ali_hassan.hafizalquran.databinding.SingleSurahFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

/*
* The Fragment will show a single surah pages, it is opened when the user
* select a surah they want to save, revision.
* */
@AndroidEntryPoint
class SingleSurahFragment : Fragment(R.layout.single_surah_fragment),
    SingleSurahPagesAdapter.OnPageIsClick {


    private val pagesViewModel: SingleSurahViewModel by viewModels()

    private lateinit var binding: SingleSurahFragmentBinding



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SingleSurahFragmentBinding.bind(view)
        val currentSelectedSurah: Surah = (arguments?.get("selectedSurah") ?:
        Surah(surahName = "سورة البقرة", surahPagesCount = 48, surahState = 2, isSurahMakia = true )) as Surah

        val pagesAdapter = SingleSurahPagesAdapter(currentSelectedSurah, requireContext(), this)
        binding.apply {
            pagesRecyclerForSingleSurah.adapter = pagesAdapter
            pagesAdapter.submitList(pagesViewModel.provideFakeData())
        }

    }

    override fun onClickPage(clickedPage: Page) {
        Toast.makeText(requireContext(), "Ok you clicked page number ${clickedPage.pageNumber}", Toast.LENGTH_SHORT)
            .show()
    }

}