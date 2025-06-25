package com.example.buynest_admin.views.authentication.login.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController


import com.example.buynest_admin.R
import com.example.buynest_admin.RoutesScreens
import com.example.buynest_admin.ui.theme.MainColor
import com.example.buynest_admin.ui.theme.white
import com.example.buynest_admin.views.authentication.login.viewmodel.LoginViewModel

@Composable
fun LoginScreen(mainNavController: NavHostController, viewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val passwordVisible = remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val email = viewModel.email
    val password = viewModel.password
    val loginSuccess = viewModel.loginSuccess.value
    val errorMessage = viewModel.errorMessage.value

    if (loginSuccess) {
        LaunchedEffect(Unit) {
            mainNavController.navigate(RoutesScreens.Home.route) {
                popUpTo(RoutesScreens.Login.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainColor)
            .imePadding()
            .focusable()
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .padding(top = 100.dp),
            horizontalAlignment = Alignment.Start
        ) {

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = "BuyNest",
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = FontFamily(Font(R.font.phenomena_bold)),
                    fontSize = 50.sp,
                    color = white
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Welcome Back To BuyNest", color = white, fontSize = 25.sp, fontFamily = FontFamily(Font(R.font.phenomena_bold)))
            Spacer(modifier = Modifier.height(4.dp))
            Text("Please sign in with your mail", color = white, fontSize = 20.sp, fontFamily = FontFamily(Font(R.font.phenomena_regular)))
            Spacer(modifier = Modifier.height(24.dp))

            Text("Email", color = white, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(10.dp))

            CustomTextField(
                value = email.value,
                onValueChange = { email.value = it },
                placeholder = "Enter your email"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Password", color = white, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(10.dp))

            CustomTextField(
                value = password.value,
                onValueChange = { password.value = it },
                placeholder = "Enter your password",
                isPassword = true,
                isPasswordVisible = passwordVisible.value,
                onVisibilityToggle = { passwordVisible.value = !passwordVisible.value }
            )

            Spacer(modifier = Modifier.height(8.dp))



            Spacer(modifier = Modifier.height(24.dp))

            errorMessage?.let {
                Text(
                    text = it,
                    color = androidx.compose.ui.graphics.Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Button(
                onClick = {
                    viewModel.onLoginClick(context)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = white),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Login",
                    color = MainColor,
                    fontSize = 24.sp,
                    fontFamily = FontFamily(Font(R.font.phenomena_bold))
                )
            }
        }
    }
}

