package com.example.buynest_admin

import kotlinx.serialization.Serializable

@Serializable
sealed class RoutesScreens(val route: String, val icon: Int) {
    @Serializable
    object Home : RoutesScreens("home", R.drawable.baseline_home_24)
    object Offers : RoutesScreens("offers", R.drawable.baseline_discount_avd_24)
    object Categories : RoutesScreens("categories", R.drawable.baseline_category_24)
    object Profile : RoutesScreens("profile", R.drawable.baseline_person_24)
    object Login : RoutesScreens("login", 0)
    object AvailableProducts : RoutesScreens("available_products", 0)
    object ProductInfo : RoutesScreens("product_info", 0)
}
