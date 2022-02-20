package app.netlify.dev_ali_hassan.hafizalquran.ui.memorizepage

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import app.netlify.dev_ali_hassan.hafizalquran.R
import app.netlify.dev_ali_hassan.hafizalquran.databinding.MemorizePageFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MemorizePageFragment : Fragment(R.layout.memorize_page_fragment) {


    private lateinit var binding: MemorizePageFragmentBinding

    private val viewModel: MemorizePageViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MemorizePageFragmentBinding.bind(view)
        binding.apply {
            playAudioBtn.setOnClickListener {
                viewModel.playMedia(binding.mediaFileNameEditText.text.toString())
            }
            pauseAudioBtn.setOnClickListener {

            }
            downloadAudioBtn.setOnClickListener {
                val fileName = binding.mediaFileNameEditText.text.toString()
                if (fileName.isEmpty()) {
                    Toast.makeText(requireContext(), "please insert name", Toast.LENGTH_LONG).show()
                }
                binding.downloadMediaProgressBar.visibility = View.VISIBLE
                viewModel.downloadAudioFileFromStorage(fileName)

            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.eventsFlow.collect {events ->
                when (events) {
                    is MemorizePageViewModel.MemorizePageEvents.AudioDownloadCompleted -> {
                        binding.downloadMediaProgressBar.visibility = View.INVISIBLE
                        Toast.makeText(
                            requireContext(),
                            "Audio downloaded successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}