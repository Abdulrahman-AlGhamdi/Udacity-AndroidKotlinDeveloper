package com.udacity.project4.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.udacity.project4.ui.login.LoginViewModel.AuthenticationState.*

class LoginViewModel : ViewModel() {

    fun authenticatedUser() = MutableLiveData<AuthenticationState>().apply {
        this.postValue(Authenticated)
    }

    fun authenticationState() = FirebaseUserLiveData().map {
        if (it != null) Authenticated
        else Unauthenticated
    }

    enum class AuthenticationState {
        Authenticated,
        Unauthenticated
    }
}