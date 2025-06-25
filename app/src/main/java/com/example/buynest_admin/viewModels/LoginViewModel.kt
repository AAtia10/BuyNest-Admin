package com.example.buynest_admin.viewModels


import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.buynest_admin.data.local.sharedpreference.SharedPreferenceManager

class LoginViewModel : ViewModel() {

    var email = mutableStateOf("")
    var password = mutableStateOf("")
    var loginSuccess = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)

    fun onLoginClick(context: Context) {
        val validEmail = "admin10@user.com"
        val validPassword = "buyNest10"

        if (email.value == validEmail && password.value == validPassword) {
            loginSuccess.value = true
            errorMessage.value = null
            SharedPreferenceManager.getInstance(context).setLoggedIn(true)
        } else {
            loginSuccess.value = false
            errorMessage.value = "Invalid Email or Password"
        }
    }
}
