package com.example.buynest_admin

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.buynest.views.categories.AllProductsScreen
import com.example.buynest.views.favourites.OffersScreen
import com.example.buynest.views.home.HomeScreen
import com.example.buynest.views.profile.ProfileScreen
import com.example.buynest_admin.remote.RemoteDataSourceImpl
import com.example.buynest_admin.remote.ShopifyRetrofitBuilder
import com.example.buynest_admin.repo.ProductRepository
import com.example.buynest_admin.views.authentication.login.view.LoginScreen
import com.example.buynest_admin.views.avaliableProducts.AvaliableProductsScreen
import com.example.buynest_admin.views.allProducts.viewModel.ProductViewModel
import com.example.buynest_admin.views.allProducts.viewModel.ProductViewModelFactory
import com.example.buynest_admin.views.splash.SplashScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetupNavHost(mainNavController: NavHostController) {

    val sharedViewModel: ProductViewModel = viewModel(
        factory = ProductViewModelFactory(
            ProductRepository.getInstance(
                RemoteDataSourceImpl(ShopifyRetrofitBuilder.service)
            )
        )
    )
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

        composable(RoutesScreens.Offers.route) {
            OffersScreen()
        }
        composable(RoutesScreens.Categories.route) {
            AllProductsScreen()
        }
        composable(RoutesScreens.Profile.route) {
            ProfileScreen()
        }

        composable(RoutesScreens.Home.route) {
            HomeScreen(mainNavController, sharedViewModel)
        }
        composable(RoutesScreens.AvailableProducts.route) {
            AvaliableProductsScreen(sharedViewModel)
        }
    }
}
