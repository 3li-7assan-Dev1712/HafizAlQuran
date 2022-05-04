package app.netlify.dev_ali_hassan.hafizalquran.ui.singlesurah

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import app.netlify.dev_ali_hassan.hafizalquran.R
import app.netlify.dev_ali_hassan.hafizalquran.data.models.Page
import app.netlify.dev_ali_hassan.hafizalquran.data.models.Surah
import app.netlify.dev_ali_hassan.hafizalquran.databinding.SingleSurahFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/*
* The Fragment will show a single surah pages, it is opened when the user
* selects a surah they want to save, revision.
* */
@AndroidEntryPoint
class SingleSurahFragment : Fragment(R.layout.single_surah_fragment),
    SingleSurahPagesAdapter.OnPageIsClick {

    private val TAG = "SingleSurahFragment"

    private val pagesViewModel: SingleSurahViewModel by viewModels()

    private lateinit var binding: SingleSurahFragmentBinding

    private lateinit var currentSelectedSurah: Surah

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SingleSurahFragmentBinding.bind(view)

        currentSelectedSurah = (arguments?.get("selectedSurah") ?: Surah(
            surahName = "سورة البقرة",
            surahState = 2,
            isSurahMakia = true
        )) as Surah


        val pagesAdapter = SingleSurahPagesAdapter(currentSelectedSurah, requireContext(), this)
        binding.apply {
            pagesRecyclerForSingleSurah.adapter = pagesAdapter
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            pagesViewModel.getPagesOfSurahWithId(currentSelectedSurah.id).collect {
                pagesAdapter.submitList(it)
            }

        }
        viewLifecycleOwner.lifecycleScope.launch {
            pagesViewModel.eventsFlow.collect { event ->
                when (event) {
                    is SingleSurahViewModel.SingleSurahEvents.UserChoosePage -> {
                        Log.d(TAG, "onViewCreated: when is working")
                        navigateToMemorizePageFragment(event.choosedPage)
                    }
                }
            }
        }
//        pagesViewModel.createMoveToFolder(currentSelectedSurah.surahName)
    }


    private fun navigateToMemorizePageFragment(page: Page) {
        Log.d("TAG", "navigateToMemorizePageFragment: should navigate to that fragment")
        val data = bundleOf("choosedPage" to page, "pageNumber" to page.pageNumber, "surahName" to "")
        findNavController().navigate(R.id.action_singleSurahFragment_to_memorizePageFragment, data)
    }

    override fun onClickPage(clickedPage: Page, position: Int) {
        Toast.makeText(
            requireContext(),
            "Ok you clicked page number ${clickedPage.pageNumber}",
            Toast.LENGTH_SHORT
        )
            .show()
        Log.d(TAG, "onClickPage: going to call the method")
        pagesViewModel.userClickedPage(clickedPage, position)
        Log.d(TAG, "onClickPage: called  the method done")
    }

}
