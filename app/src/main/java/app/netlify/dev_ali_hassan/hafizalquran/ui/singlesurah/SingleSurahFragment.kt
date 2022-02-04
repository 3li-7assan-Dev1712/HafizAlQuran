package app.netlify.dev_ali_hassan.hafizalquran.ui.singlesurah

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import app.netlify.dev_ali_hassan.hafizalquran.R
import app.netlify.dev_ali_hassan.hafizalquran.data.models.Surah
import app.netlify.dev_ali_hassan.hafizalquran.databinding.SingleSurahFragmentBinding

/*
* The Fragment will show a single surah pages, it is opened when the user
* select a surah they want to save, revision.
* */
class SingleSurahFragment : Fragment(R.layout.single_surah_fragment) {


    private lateinit var binding: SingleSurahFragmentBinding
    val openSurahInfo = arguments
    private val currentSelectedSurah: Surah = arguments?.get("selectedSurah") as Surah


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SingleSurahFragmentBinding.bind(view)

    }
}