package app.netlify.dev_ali_hassan.hafizalquran.ui.allsurahs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import app.netlify.dev_ali_hassan.hafizalquran.R
import app.netlify.dev_ali_hassan.hafizalquran.data.Surah
import app.netlify.dev_ali_hassan.hafizalquran.databinding.AllSurahsFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

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

    }

    /*the method will be called when a user select a specific Surah.
    * for the separation of concern we will delegate to work to
    * a view model to tell the fragment what to do when a user select a Surah,
    * but for now we will just display a toast.*/
    override fun onSurahSelected(selectedSurah: Surah) {
        Toast.makeText(requireContext(), "Ok you selected surah number ${selectedSurah.id}",
        Toast.LENGTH_SHORT).show()
    }

}
