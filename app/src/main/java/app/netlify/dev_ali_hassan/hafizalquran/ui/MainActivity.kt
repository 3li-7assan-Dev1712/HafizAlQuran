package app.netlify.dev_ali_hassan.hafizalquran.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import app.netlify.dev_ali_hassan.hafizalquran.R
import app.netlify.dev_ali_hassan.hafizalquran.databinding.ActivityMainBinding
import app.netlify.dev_ali_hassan.hafizalquran.ui.allsurahs.AllSurahsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    private lateinit var navController: NavController

    private lateinit var bottomNav: BottomNavigationView

    private val surahsViewModel: AllSurahsViewModel by viewModels()

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                as NavHostFragment
        navController = navHostFragment.findNavController()
        binding = ActivityMainBinding.inflate(layoutInflater)
        /*setupActionBarWithNavController(navController)*/

        bottomNav = findViewById(R.id.bottom_nav)
        bottomNav.setupWithNavController(navController)
        navController.addOnDestinationChangedListener(this)


    }

    /*  override fun onStart() {
          super.onStart()


      }*/

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        navController.removeOnDestinationChangedListener(this)
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {

        /*
        when the user navigates to the screen of a single Surah or Page hide
        the bottom navigation view, otherwise show it.
         */
        when (destination.id) {
            R.id.singleSurahFragment -> {
                bottomNav.visibility = View.GONE
                Log.d(TAG, "onDestinationChanged: the bottom nav should be disappeared")
            }
            R.id.memorizePageFragment -> {
                bottomNav.visibility = View.GONE
                Log.d(TAG, "onDestinationChanged: the bottom nav should be disappeared")
            }
            else -> {
                bottomNav.visibility = View.VISIBLE
                Log.d(TAG, "onDestinationChanged: the bottom nav should not be disappeared")
            }
        }
    }

    companion object {
        const val TAG = "MainActivity"
    }
}