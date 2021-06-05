package com.ss.shoestore.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ss.shoestore.R
import com.ss.shoestore.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        init()
        checkLoginCredentials()

        return binding.root
    }

    private fun init() {
        binding.toolbar.setupWithNavController(findNavController())

        binding.toolbar.menu.findItem(R.id.new_account).setOnMenuItemClickListener {
            moveToOnBoardingScreens()
            true
        }
    }

    private fun checkLoginCredentials() {
        binding.login.setOnClickListener {
            moveToOnBoardingScreens()
        }
    }

    private fun moveToOnBoardingScreens() {
        val action = LoginFragmentDirections.actionLoginFragmentToOnBoardingFragment()
        findNavController().navigate(action)
    }
}