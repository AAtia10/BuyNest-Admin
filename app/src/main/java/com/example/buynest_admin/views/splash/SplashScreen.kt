package com.example.buynest_admin.views.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.example.buynest_admin.R
import com.example.buynest_admin.ui.theme.MainColor
import com.example.buynest_admin.ui.theme.white
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onAnimationComplete: () -> Unit) {
    val phenomenaFontFamily = FontFamily(
        Font(R.font.phenomena_bold)
    )

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.splash))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    LaunchedEffect(Unit) {
        delay(5000)
        onAnimationComplete()
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
