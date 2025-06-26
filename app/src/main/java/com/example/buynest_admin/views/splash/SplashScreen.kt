package com.example.buynest_admin.views.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import com.example.buynest_admin.RoutesScreens
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.*
import com.example.buynest_admin.R
import com.example.buynest_admin.data.local.sharedpreference.SharedPreferenceManager
import com.example.buynest_admin.ui.theme.MainColor
import com.example.buynest_admin.ui.theme.white
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    val phenomenaFontFamily = FontFamily(
        Font(R.font.phenomena_bold)
    )

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.splash))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    val context = LocalContext.current
    val sharedPref = remember { SharedPreferenceManager.getInstance(context) }

    LaunchedEffect(Unit) {
        delay(3000)
        if (sharedPref.isLoggedIn()) {
            navController.navigate(RoutesScreens.Home.route) {
                popUpTo(0) { inclusive = true }
            }
        } else {
            navController.navigate(RoutesScreens.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainColor),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier
                .height(300.dp)
        )

        Text(
            text = "BuyNest",
            fontFamily = phenomenaFontFamily,
            fontSize = 62.sp,
            color = white
        )






    }


}
