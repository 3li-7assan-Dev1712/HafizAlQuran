package app.netlify.dev_ali_hassan.hafizalquran.ui.memorizepage

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import app.netlify.dev_ali_hassan.hafizalquran.R
import app.netlify.dev_ali_hassan.hafizalquran.data.models.Page
import app.netlify.dev_ali_hassan.hafizalquran.databinding.MemorizePageFragmentBinding
import app.netlify.dev_ali_hassan.hafizalquran.util.Resource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MemorizePageFragment : Fragment(R.layout.memorize_page_fragment) {

    //  request permission
    val requestMultiplePermissionsLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            permissions.entries.forEach {
                /* if (it.value) {
                     // continue with app flow
                 } else {
                     // tell the user we need this permission
                 }  */
            }

            if (!currentPage.isDownloaded) {
                startDownloadMedia()
            }
        }


    private lateinit var binding: MemorizePageFragmentBinding

    private val TAG = "MemorizePageFragment"
    private val viewModel: MemorizePageViewModel by viewModels()

    var currentPage: Page = arguments?.getParcelable("choosedPage") ?: Page(-1, 0, false, false)



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MemorizePageFragmentBinding.bind(view)

        Log.d(TAG, "onViewCreated: the page download status is ${currentPage.isDownloaded}")
        // check the permission
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                // go ahead with app functionality
                if (!currentPage.isDownloaded) {
                    startDownloadMedia()
                }
            }
            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                // create an educational dialog fragment to tell why we need this permission
            }
            else -> {
                requestMultiplePermissionsLauncher.launch(
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                )
            }
        }


        binding.downloadAudioBtn.isVisible = !currentPage.isDownloaded

        binding.apply {
            playAudioBtn.setOnClickListener {
                viewModel.userClickedPlayBtn()
            }
            pauseAudioBtn.setOnClickListener {
                viewModel.userClickedPauseBtn()
            }
            downloadAudioBtn.setOnClickListener {
                showProgressBar()
                viewModel.userConfirmDownloadOperation()

            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.eventsFlow.collect { events ->
                when (events) {
                    is MemorizePageViewModel.MemorizePageEvents.AudioDownloadCompleted -> {
                        hideProgressBar()
                        binding.downloadAudioBtn.visibility = View.INVISIBLE
                        Toast.makeText(
                            requireContext(),
                            "Audio downloaded successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        currentPage = currentPage.copy(isDownloaded = true)
                    }
                    is MemorizePageViewModel.MemorizePageEvents.DownloadConfirmationEvent -> {
                        // show alert dialog to let the uer confirm downloading the audio
                        Log.d(TAG, "onViewCreated: should display download confirmation snackbar")
                        Snackbar.make(
                            view,
                            "Audio is not downloaded, want to download?",
                            Snackbar.LENGTH_LONG
                        ).setAction(R.string.ok) {
                            showProgressBar()
                            viewModel.userConfirmDownloadOperation()
                        }.show()
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


        viewModel.pageDataFromServer.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showProgressBar()
                    Toast.makeText(requireContext(), "Please wait some seconds", Toast.LENGTH_SHORT)
                        .show()
                }
                is Resource.Success -> {
                    hideProgressBar()
                    response.data.let {
                        viewModel.receivedAyahsSuccessfully(it.data.ayahs)
                        Toast.makeText(
                            requireContext(),
                            "done, the number of ayahs is ${it.data.ayahs.size}",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                }
                is Error -> {
                    hideProgressBar()
                    Toast.makeText(requireContext(), "Error occured!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun startDownloadMedia() {
        viewModel.getPageData()
    }


    private fun hideProgressBar() {
        binding.downloadMediaProgressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        binding.downloadMediaProgressBar.visibility = View.VISIBLE
    }
}