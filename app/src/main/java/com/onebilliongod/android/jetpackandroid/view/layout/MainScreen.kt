package com.onebilliongod.android.jetpackandroid.view.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.onebilliongod.android.jetpackandroid.view.home.HomeScreen
import com.onebilliongod.android.jetpackandroid.view.profile.ProfileScreen

/**
 * MainScreen composable that displays the main content after login.
 * It defines the bottom navigation bar for portrait mode and the side navigation rail for landscape mode.
 */
@Composable
fun MainScreen(windowSize: WindowSizeClass) {
    val navController = rememberNavController()
    val portrait = windowSize.widthSizeClass == WindowWidthSizeClass.Compact
    Scaffold(
        bottomBar = {
            //Displays BottomNavigation if portrait
            if (portrait) {
                BottomNavigation(navController = navController)
            } else null
        },
        content = { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                //Displays SideNavigationRail if landscape
                if (!portrait) {
                    SideNavigationRail(navController = navController)
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    //Define the navigation graph with HomeScreen as the start destination
                    NavHost(
                        navController = navController,
                        startDestination = Screen.HomeScreen.route
                    ) {
                        composable(Screen.HomeScreen.route) {
                            HomeScreen()
                        }
                        composable(Screen.ProfileScreen.route) {
                            ProfileScreen(windowSize.widthSizeClass)
                        }
                    }
                }
            }
        }
    )
}