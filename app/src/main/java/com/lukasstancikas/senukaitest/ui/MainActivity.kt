package com.lukasstancikas.senukaitest.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.lukasstancikas.booklists.ui.booklist.MyListFragmentArgs
import com.lukasstancikas.senukaitest.R
import com.lukasstancikas.senukaitest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val navHostFragment =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?)!!
        val navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(
            setOf(
                com.lukasstancikas.booklists.R.id.bookLists
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navController.addOnDestinationChangedListener { controller: NavController,
                                                        destination: NavDestination,
                                                        arguments: Bundle? ->
            arguments?.let {
                when (destination.id) {
                    com.lukasstancikas.booklists.R.id.myList -> MyListFragmentArgs.fromBundle(it).bookList.title
                    else -> null
                }?.let { title ->
                    binding.toolbar.title = title
                }
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}