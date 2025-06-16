package com.example.buynest_admin

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.buynest_admin.ui.theme.MainColor
import np.com.susanthapa.curved_bottom_navigation.CbnMenuItem
import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Surface(modifier = Modifier.fillMaxSize()) {
                hideSystemUI()
                BuyNestStart()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun BuyNestStart() {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        if (currentRoute == RoutesScreens.Login.route || currentRoute == "splash") {
            SetupNavHost(mainNavController = navController)
        } else {
            Scaffold(
                bottomBar = {
                    CurvedNavBar(navController)
                }
            ) { innerPadding ->
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    SetupNavHost(mainNavController = navController)
                }
            }
        }
    }


    @Composable
    fun CurvedNavBar(navController: NavHostController) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(White),
            factory = { context ->
                CurvedBottomNavigationView(context).apply {
                    unSelectedColor = White.toArgb()
                    selectedColor = MainColor.toArgb()
                    navBackgroundColor = MainColor.toArgb()
                    val cbnMenuItems = ScreenMenuItem.menuItems.map { screen ->
                        CbnMenuItem(
                            icon = screen.icon,
                            avdIcon = screen.selectedIcon,
                            destinationId = screen.id
                        )
                    }
                    layoutDirection = View.LAYOUT_DIRECTION_LTR
                    setMenuItems(cbnMenuItems.toTypedArray(), 0)
                    setOnMenuItemClickListener { _, i ->
                        navController.popBackStack()
                        navController.navigate(ScreenMenuItem.menuItems[i].screen.route)
                    }
                }
            }
        )
    }

    fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let {
                it.hide(WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
        }
        window.navigationBarColor = White.toArgb()
    }

//    private fun shouldShowBottomBar(navController: NavHostController): Boolean {
//        val route = navController.currentBackStackEntry?.destination?.route
//        return route != RoutesScreens.Login.route && route != "splash"
//    }

}
