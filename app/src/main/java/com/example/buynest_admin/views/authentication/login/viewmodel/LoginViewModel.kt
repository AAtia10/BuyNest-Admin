package com.example.buynest_admin.views.authentication.login.viewmodel


import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    var email = mutableStateOf("")
    var password = mutableStateOf("")
    var loginSuccess = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)

    fun onLoginClick() {
        val validEmail = "admin10@user.com"
        val validPassword = "buyNest10"

        if (email.value == validEmail && password.value == validPassword) {
            loginSuccess.value = true
            errorMessage.value = null
        } else {
            loginSuccess.value = false
            errorMessage.value = "Invalid Email or Password"
        }
    }
}
