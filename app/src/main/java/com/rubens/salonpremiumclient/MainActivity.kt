package com.rubens.salonpremiumclient

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.rubens.salonpremiumclient.databinding.ActivityMainBinding
import com.rubens.salonpremiumclient.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configNavigation()
        initViewModel()
        initCollectors()
        setBottomNavigationVisibility(false)
    }

    private fun initCollectors() {
        lifecycleScope.launch {
            sharedViewModel.mostrarBottomNavigation.collectLatest {
                podeMostrar->
                if(podeMostrar){
                    setBottomNavigationVisibility(true)
                }
            }
        }
    }

    private fun initViewModel() {
        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]

    }

    private fun configNavigation() {
        val navHostFragment = (supportFragmentManager.findFragmentById(binding.fragmentContainerView.id))as NavHostFragment

        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)
    }

    private fun setBottomNavigationVisibility(podeMostrar: Boolean){
        if(podeMostrar){
            binding.bottomNavigationView.visibility = View.VISIBLE

        }else{
            binding.bottomNavigationView.visibility = View.GONE

        }
    }
}