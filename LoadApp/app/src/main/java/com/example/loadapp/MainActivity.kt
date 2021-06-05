package com.example.loadapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.loadapp.databinding.ActivityMainBinding
import com.example.loadapp.ui.home.HomeFragmentDirections

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.fragment_container)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (intent.getBooleanExtra(KEY, false)) {
            val name = intent.getStringExtra(NAME)
            val status = intent.getStringExtra(STATUS)
            if (name != null && status != null) {
                val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(name, status)
                navController.navigate(action)
            }
        }
    }

    companion object {
        const val KEY = "key"
        const val NAME = "name"
        const val STATUS = "status"
    }
}