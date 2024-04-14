package com.example.foodtruck

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.foodtruck.databinding.ActivityMainBinding // Assuming you are using View Binding
import com.example.foodtruck.utils.Utils


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup the nav controller for use with the NavHostFragment
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // If you have a Toolbar, ActionBar, or BottomNavigationView, set them up with the navController here
        // Example for a Toolbar
        // setSupportActionBar(binding.toolbar)
        // NavigationUI.setupActionBarWithNavController(this, navController)

        // Example for BottomNavigationView
        // NavigationUI.setupWithNavController(binding.bottomNav, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
        }
}