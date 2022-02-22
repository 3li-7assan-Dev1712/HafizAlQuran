package app.netlify.dev_ali_hassan.hafizalquran.ui.memorizepage

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import app.netlify.dev_ali_hassan.hafizalquran.R
import app.netlify.dev_ali_hassan.hafizalquran.data.models.Page
import app.netlify.dev_ali_hassan.hafizalquran.databinding.MemorizePageFragmentBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MemorizePageFragment : Fragment(R.layout.memorize_page_fragment) {


    private lateinit var binding: MemorizePageFragmentBinding

    private val viewModel: MemorizePageViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MemorizePageFragmentBinding.bind(view)

        val currentPage: Page = arguments?.getParcelable("choosedPage") ?: Page(-1, 0, false, false)
        val surahName = arguments?.getString("surahName")
        val pagePosition = arguments?.getInt("position")

        binding.downloadAudioBtn.isVisible = !currentPage.isDownloaded

        binding.apply {
            playAudioBtn.setOnClickListener {
                viewModel.userClickedPlayBtn()
            }
            pauseAudioBtn.setOnClickListener {

            }
            downloadAudioBtn.setOnClickListener {
                val fileName = binding.mediaFileNameEditText.text.toString()
                if (fileName.isEmpty()) {
                    Toast.makeText(requireContext(), "please insert name", Toast.LENGTH_LONG).show()
                }
                binding.downloadMediaProgressBar.visibility = View.VISIBLE
                viewModel.userConfirmDownloadOperation()

            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.eventsFlow.collect { events ->
                when (events) {
                    is MemorizePageViewModel.MemorizePageEvents.AudioDownloadCompleted -> {
                        binding.downloadMediaProgressBar.visibility = View.INVISIBLE
                        Toast.makeText(
                            requireContext(),
                            "Audio downloaded successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is MemorizePageViewModel.MemorizePageEvents.DownloadConfirmationEvent -> {
                        // show alert dialog to let the uer confirm downloading the audio
                        context?.let {
                            Snackbar.make(
                                view,
                                it.getString(R.string.audio_not_downloaded_msg),
                                Snackbar.LENGTH_INDEFINITE
                            ).setAction(R.string.ok) {
                                binding.downloadMediaProgressBar.visibility = View.VISIBLE
                                viewModel.userConfirmDownloadOperation()
                            }
                        }
                    }
                    is MemorizePageViewModel.MemorizePageEvents.AudioIsNotAvailable -> {
                        // show a dialog to tell the user the Surah is not available in the server
                        Snackbar.make(
                            view,
                            "Audio is not available yet in the server",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    is MemorizePageViewModel.MemorizePageEvents.ErrorEvent -> {
                        Snackbar.make(
                            view,
                            "There is an unknown error occurs",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                }
            }
        }
    }
}