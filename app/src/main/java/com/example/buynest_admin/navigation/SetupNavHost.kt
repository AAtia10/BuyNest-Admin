package com.example.buynest_admin

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.buynest.views.categories.CategoriesScreen
import com.example.buynest.views.favourites.FavouriteScreen
import com.example.buynest.views.home.HomeScreen
import com.example.buynest.views.profile.ProfileScreen
import com.example.buynest_admin.views.authentication.LoginScreen
import com.example.buynest_admin.views.splash.SplashScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetupNavHost(mainNavController: NavHostController) {
    NavHost(
        navController = mainNavController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen {
                mainNavController.navigate(RoutesScreens.Login.route) {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }
        composable(RoutesScreens.Login.route) {
            LoginScreen(mainNavController)
        }
        composable(RoutesScreens.Home.route) {
            HomeScreen()
        }
        composable(RoutesScreens.Favourite.route) {
            FavouriteScreen()
        }
        composable(RoutesScreens.Categories.route) {
            CategoriesScreen()
        }
        composable(RoutesScreens.Profile.route) {
            ProfileScreen()
        }
    }
}
