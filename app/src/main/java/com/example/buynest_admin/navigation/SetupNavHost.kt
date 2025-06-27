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
import com.example.buynest_admin.views.ProductInfo.ProductInfoScreen
import com.example.buynest_admin.views.authentication.login.view.LoginScreen
import com.example.buynest_admin.views.avaliableProducts.AvaliableProductsScreen
import com.example.buynest_admin.viewModels.ProductViewModel
import com.example.buynest_admin.viewModels.ProductViewModelFactory
import com.example.buynest_admin.views.discountDetails.DiscountDetailsScreen
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
            SplashScreen(mainNavController)
        }
        composable(RoutesScreens.Login.route) {
            LoginScreen(mainNavController)
        }

        composable(RoutesScreens.Offers.route) {
            OffersScreen(mainNavController)
        }
        composable(RoutesScreens.Categories.route) {
            AllProductsScreen(mainNavController, sharedViewModel)
        }
        composable(RoutesScreens.Profile.route) {
            ProfileScreen(mainNavController)
        }

        composable(RoutesScreens.Home.route) {
            HomeScreen(mainNavController, sharedViewModel)
        }
        composable(RoutesScreens.AvailableProducts.route) {
            AvaliableProductsScreen(mainNavController, sharedViewModel)
        }
        composable(RoutesScreens.ProductInfo.route) {
            ProductInfoScreen(sharedViewModel, mainNavController)
        }

        composable("discount_details/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull()
            id?.let {
                DiscountDetailsScreen(priceRuleId = it, navController = mainNavController)
            }
        }


    }
}
