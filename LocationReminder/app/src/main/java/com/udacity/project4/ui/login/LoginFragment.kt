package com.udacity.project4.ui.login

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.android.project4.R
import com.android.project4.databinding.FragmentLoginBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.ui.login.LoginViewModel.AuthenticationState.Authenticated
import com.udacity.project4.ui.login.LoginViewModel.AuthenticationState.Unauthenticated

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()
    private val argument: LoginFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        init()

        return binding.root
    }

    private fun init() {
        observeAuthenticationState()
        authenticatedUser()
    }

    private fun observeAuthenticationState() {
        if (argument.logout)
            AuthUI.getInstance().signOut(requireContext())
        else {
            viewModel.authenticationState().observe(viewLifecycleOwner, {
                when (it!!) {
                    Authenticated -> {
                        val action = LoginFragmentDirections.actionLoginFragmentToReminderFragment()
                        findNavController().navigate(action)
                    }
                    Unauthenticated -> {
                        binding.login.text = getString(R.string.login)
                        binding.login.setOnClickListener { userLogin() }
                    }
                }
            })
        }
    }

    private fun authenticatedUser() {
        binding.login.setOnLongClickListener {
            viewModel.authenticatedUser().observe(viewLifecycleOwner, {
                val action = LoginFragmentDirections.actionLoginFragmentToReminderFragment()
                findNavController().navigate(action)
            })
            true
        }
    }

    private fun userLogin() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val loginIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setIsSmartLockEnabled(false)
            .build()

        startForResult.launch(loginIntent)
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val response = IdpResponse.fromResultIntent(result.data)

        when(result.resultCode) {

            Activity.RESULT_OK -> {
                val action = LoginFragmentDirections.actionLoginFragmentToReminderFragment()
                findNavController().navigate(action)
            }
            Activity.RESULT_CANCELED -> {
                Snackbar.make(requireView(), "Failed: ${response?.error?.localizedMessage}", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}