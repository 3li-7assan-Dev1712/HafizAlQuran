package app.netlify.dev_ali_hassan.hafizalquran.ui.allsurahs

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import app.netlify.dev_ali_hassan.hafizalquran.R
import app.netlify.dev_ali_hassan.hafizalquran.data.models.Surah
import app.netlify.dev_ali_hassan.hafizalquran.databinding.AllSurahsFragmentBinding
import app.netlify.dev_ali_hassan.hafizalquran.util.MediaUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

/**
 * This fragment will be responsible for displaying all Surahs in Quran.
 * All the work (business logic will be delegated to a dedicated view model).
 */
const val TAG = "AllSurahsFragment"

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
            Log.d(TAG, "onViewCreated: data count is ${it.size}")
        }


        // collect orders from AllSurahViewModel
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            surahsViewModel.surahEventsFlow.collect { event ->
                when (event) {
                    is AllSurahsViewModel.SurahsEvents.NavigateToSingleSurahFragment -> {
                        // navigate to the SingleSurahFragment
                        MediaUtil.createTestFile(requireContext())
                        Log.d(
                            TAG,
                            "onViewCreated: the current number of files now is: ${
                                MediaUtil.getFileList(requireContext())
                            }"
                        )
                        Toast.makeText(
                            requireContext(),
                            "number of files in is ${MediaUtil.getFileList(requireContext())}",
                            Toast.LENGTH_LONG
                        ).show()
//                        store("almaidah0")
                        playMedia()
                        /*val bundle = bundleOf(
                            "selectedSurah" to event.surah,
                            "name" to event.surah.surahName
                        )
                        findNavController().navigate(
                            R.id.action_allSurahsFragment_to_singleSurahFragment,
                            bundle
                        )*/
                    }
                }
            }
        }

        //--------------------------------


    }

    /*the method will be called when a user select a specific Surah.
    * for the separation of concern we will delegate to work to
    * a view model to tell the fragment what to do when a user select a Surah.*/
    override fun onSurahSelected(selectedSurah: Surah) {
        surahsViewModel.surahIsSelected(selectedSurah)
    }

    private suspend fun readDataFromAsset(fileName: String): ByteArray? {
        return try {
            requireActivity().assets.open("$fileName.mp3", Activity.MODE_PRIVATE).use {
                it.readBytes()
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }
    }

    private suspend fun loadFileFromInternalStorage(): List<File> {
        val files = requireActivity().filesDir.listFiles()
        return try {
            files?.filter { it.name.endsWith(".mp3") }?.map {
                it
            } ?: listOf()
        } catch (ioe: IOException) {
            ioe.printStackTrace()
            listOf()
        }

    }

    suspend fun storeFileInInteranlStorage(fileName: String): Boolean {
        val data = readDataFromAsset(fileName)
        return try {
            requireContext().openFileOutput("sky_is_the_limit.mp3", MODE_PRIVATE).use { stream ->
                stream.write(data)
            }
            true
        } catch (exception: IOException) {
            exception.printStackTrace()
            false
        }

    }

    fun store(fileName: String) {

        lifecycleScope.launch {
            val result = storeFileInInteranlStorage(fileName)
            if (result)
                Log.d(TAG, "store: store the file successfully")
            else
                Log.d(TAG, "store: faild to store the file")
        }


    }

    private fun playMedia() {
        lifecycleScope.launch {
            val files = loadFileFromInternalStorage()
            if (files.isNotEmpty()) {
                val file = files[0]
                Log.d(TAG, "playMedia: the name of the file is ${file.name}")
                val player = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    // that is one of the most important thing I have been working
                    // on it a Quran app
                    /**
                     * I did the following thing in the app
                     * first I stored a mp3 file in the assest folder
                     * then I read the file from the assets as a ByteArray, and store in the internal storage
                     * finally I read the file from the internal storage and play it using the MediaPlayer
                     * man! what an accomplishment!
                     */
                    /*set the data to the MediaPlayer to play iit, so that the user can
                    * listen to the clip of the Holy Quran*/
                    setDataSource(file.path)
                    prepare()
                    start()
                }
            } else {
                Log.d(TAG, "playMedia: file is empty")
            }

        }
//        val audioPath = File(requireContext().filesDir, "my_files")
//        val any = File(audioPath, "sky_is_the_limit.mp3")
//
//        val myUri = FileProvider.getUriForFile(
//            requireContext(),
//            "${requireContext().applicationContext.packageName}.fileprovider",
//            any
//        )
//
//
//        val player = MediaPlayer().apply {
//            setAudioAttributes(
//                AudioAttributes.Builder()
//                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                    .setUsage(AudioAttributes.USAGE_MEDIA)
//                    .build()
//            )
//
//
//            setDataSource(
//                requireContext().contentResolver.openFileDescriptor(myUri, "r")?.fileDescriptor
//            )
//            prepare()
//            start()
//        }
    }

}
