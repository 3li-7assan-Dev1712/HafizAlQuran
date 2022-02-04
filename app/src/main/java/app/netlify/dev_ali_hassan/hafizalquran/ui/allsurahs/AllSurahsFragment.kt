package app.netlify.dev_ali_hassan.hafizalquran.ui.allsurahs

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import app.netlify.dev_ali_hassan.hafizalquran.R
import app.netlify.dev_ali_hassan.hafizalquran.data.models.Surah
import app.netlify.dev_ali_hassan.hafizalquran.databinding.AllSurahsFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

/**
 * This fragment will be responsible for displaying all Surahs in Quran.
 * All the work (business logic will be delegated to a dedicated view model).
 */
@AndroidEntryPoint
class AllSurahsFragment : Fragment(R.layout.all_surahs_fragment), AllSurahsAdapter.OnSurahSelected {

    private val surahsViewModel: AllSurahsViewModel by viewModels()
    private lateinit var binding: AllSurahsFragmentBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = AllSurahsAdapter(requireContext(), this)
        binding = AllSurahsFragmentBinding.bind(view)
        binding.apply {
            allSurahsRecyclerView.adapter = adapter
        }
        surahsViewModel.surahsLiveData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }


        // collect orders from AllSurahViewModel
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            surahsViewModel.surahEventsFlow.collect {event ->
                when (event) {
                    is AllSurahsViewModel.SurahsEvents.NavigateToSingleSurahFragment -> {
                        // navigate to the SingleSurahFragment
                        val bundle = bundleOf("selectedSurah" to event.surah, "name" to event.surah.surahName)
                        findNavController().navigate(R.id.action_allSurahsFragment_to_singleSurahFragment, bundle)
                    }
                }
            }
        }

    }

    /*the method will be called when a user select a specific Surah.
    * for the separation of concern we will delegate to work to
    * a view model to tell the fragment what to do when a user select a Surah.*/
    override fun onSurahSelected(selectedSurah: Surah) {
        surahsViewModel.surahIsSelected(selectedSurah)
    }

}
