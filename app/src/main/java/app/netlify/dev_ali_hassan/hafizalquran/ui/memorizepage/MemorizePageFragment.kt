package app.netlify.dev_ali_hassan.hafizalquran.ui.memorizepage

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
class MemorizePageFragment : Fragment(R.layout.memorize_page_fragment), AdapterView.OnItemSelectedListener {

    //  request permission
    private val requestMultiplePermissionsLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            permissions.entries.forEach {
                // make cause a problem, will be considered soon
            }
            // download if the page is not already downloaded
            if (!currentPage.isDownloaded) {
                startDownloadMedia()
            }
        }

    // using view binding
    private lateinit var binding: MemorizePageFragmentBinding

    //TAG for logging
    private val TAG = "MemorizePageFragment"

    // the view model for this fragment to delegate all the business logic for it
    private val viewModel: MemorizePageViewModel by viewModels()

    // current/selected page which the user wants to memorize/study
    private lateinit var currentPage: Page


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MemorizePageFragmentBinding.bind(view)
        currentPage = arguments?.getParcelable("choosedPage") ?: Page("",-1, 0, false, false)

        if (savedInstanceState != null) return

        binding.downloadMediaProgressBar.isVisible = !currentPage.isDownloaded
        binding.downloadAudioBtn.isVisible = !currentPage.isDownloaded
        binding.repeatSpecificTirmIv.isVisible = currentPage.isDownloaded

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

        binding.apply {
            playAudioBtn.setOnClickListener {
                viewModel.userClickedPlayBtn()
            }
            downloadAudioBtn.setOnClickListener {
                showProgressBar()
                viewModel.userConfirmDownloadOperation()
            }
        }

        // collect all events (orders) come from the MemorizePageViewModel
        collectEventsFromViewModel(view)
        // observe the network response when the view model make a network call
        observeNetworkResponseFromViewModel()
        // collect the downloading progress to fill the determined progress bar on the screen.
        collectProgressFromViewModel()
        if (currentPage.isDownloaded) {
            binding.pageAyahsTextView.text = currentPage.pageText
        }
    }

    /**
     * tell the view model that this fragment is going to be destroyed
     * to release the media player.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.fragmentDestroyed()
    }
    /**
     * this function is responsible for listening to the network result that comes from @MemorizePageViewModel
     * the result is a LifeData, so it's easy for the fragment to do such an operation by using
     * viewLifecycleOwner
     */
    private fun observeNetworkResponseFromViewModel() {
        viewModel.pageDataFromServer.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showProgressBar()
                    Toast.makeText(requireContext(), "Please wait some seconds", Toast.LENGTH_SHORT)
                        .show()
                }
                is Resource.Success -> {
                    response.data.let {
                        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                            viewModel.receivedAyahsSuccessfully(it.data.ayahs)
                        }

                        /*Toast.makeText(
                            requireContext(),
                            "done, the number of ayahs is ${it.data.ayahs.size}",
                            Toast.LENGTH_SHORT
                        )
                            .show()*/
                    }

                }
                else -> {
                    // this last condition means that there is an error
                    Toast.makeText(requireContext(), "Error occurs!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    /**
     * This function will collect the orders from the view model, for example when the downloading is finished
     * the view model sends an order to hide the progress bar and the download button.
     * it will also show a SnackBar to inform the user about the achieved operation
     *
     * @param view: The view (screen) of the fragment to be used for displaying the SnackBar
     */
    private fun collectEventsFromViewModel(view: View) {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.eventsFlow.collect { events ->
                when (events) {
                    is MemorizePageViewModel.MemorizePageEvents.AudioDownloadCompleted -> {
                        binding.downloadAudioBtn.visibility = View.INVISIBLE
                        binding.ayahsNumberToDownloadTextView.visibility = View.INVISIBLE
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

                    is MemorizePageViewModel.MemorizePageEvents.PlayPauseEvent -> {
                        if (events.isPlay) {
                            binding.playAudioBtn.setImageResource(R.drawable.ic_play)
                        } else {
                            binding.playAudioBtn.setImageResource(R.drawable.ic_pause)
                        }
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

    /**
     * This function will take the progress bar progress functionality, for example when the first 1 ayah
     * is downloaded successfully it will set the progress to a specific  percentage and when the entire page
     * is downloaded it will set the progress to 100% and then hide it.
     */
    private fun collectProgressFromViewModel() {
        Log.d(TAG, "collectProgressFromViewModel: listen to flow")
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            viewModel.folderUtil.singleAyahProgressFlow.collect { ayahProgress ->
                binding.downloadMediaProgressBar.progress = ayahProgress
                Log.d(
                    TAG,
                    "collectProgressFromViewModel: the progress should set to be $ayahProgress%"
                )
            }


        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.folderUtil.pageProgressFlow.collect { (pageProgress, remainingAyahs) ->

                binding.pageProgressView.progress = pageProgress
                Log.d(
                    TAG,
                    "collectProgressFromViewModel: the remaining ayahs now is $remainingAyahs"
                )
                if (remainingAyahs == 0) {
                    binding.pageProgressView.progress = 100
                    binding.downloadMediaProgressBar.visibility = View.INVISIBLE
                }
                binding.ayahsNumberToDownloadTextView.text =
                    resources.getString(
                        R.string.remaining_ayahs_indicator_msg,
                        remainingAyahs.toString()
                    )
//                        "downloading $remainingAyahs ayahs..."
                Log.d(
                    TAG,
                    "collectProgressFromViewModel: the page progress is  $pageProgress%"
                )
            }
        }

        if (!currentPage.isDownloaded) {
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.folderUtil.ayahsTexts.collect {
                    Log.d(TAG, "collectProgressFromViewModel: from fragment ayah text is $it")
                    binding.pageAyahsTextView.text = it
                }
            }
        }

    }

    private fun startDownloadMedia() {
        viewModel.getPageData()
    }


    private fun showProgressBar() {
        binding.downloadMediaProgressBar.visibility = View.VISIBLE
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

        when (p2) {
            0 -> {viewModel.setRepeatNumber(3)}
            1 -> {viewModel.setRepeatNumber(5)}
            2 -> {viewModel.setRepeatNumber(10)}
            3 -> {viewModel.setRepeatNumber(20)}
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}