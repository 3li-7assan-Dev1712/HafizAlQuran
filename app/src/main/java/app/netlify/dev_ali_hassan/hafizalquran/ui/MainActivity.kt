package app.netlify.dev_ali_hassan.hafizalquran.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import app.netlify.dev_ali_hassan.hafizalquran.R
import app.netlify.dev_ali_hassan.hafizalquran.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    private lateinit var navController: NavController
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                as NavHostFragment
        navController = navHostFragment.findNavController()
        binding = ActivityMainBinding.inflate(layoutInflater)
        /*setupActionBarWithNavController(navController)*/


    }

    override fun onStart() {
        super.onStart()
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_nav)
        NavigationUI.setupWithNavController(bottomNav, navController)
        navController.addOnDestinationChangedListener(this)
    }

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
        Log.d(TAG, "onDestinationChanged: the new id is ${destination.id}")
        Log.d(TAG, "onDestinationChanged: single fragment id is ${R.id.singleSurahFragment}")

        /*if (destination.id == 2131362205) {
            binding.bottomNav.visibility = View.GONE
            Log.d(TAG, "onDestinationChanged: the bottom nav should be disappeared")
        } else {
            binding.bottomNav.visibility = View.VISIBLE
            Log.d(TAG, "onDestinationChanged: the bottom nav should not be disappeared")
        }*/
    }

    companion object {
        const val TAG = "MainActivity"
    }
}