package com.onebilliongod.android.jetpackandroid.view.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.map

/**
 * side navigation bar for landscape screen devices.
 * Provides navigation options to the Home and Profile screens.
 */
@Composable
fun SideNavigationRail(modifier: Modifier = Modifier, navController: NavHostController) {
    val currentRoute by navController.currentBackStackEntryFlow
        .map { it.destination.route }
        .collectAsState(initial = Screen.HomeScreen.route)

    NavigationRail(
        modifier = modifier.padding(start = 8.dp, end = 8.dp),
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NavigationRailItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null
                    )
                },
                label = {
                    Text("Home")
                },
                selected = currentRoute == Screen.HomeScreen.route,
                onClick = {
                    navController.navigate(Screen.HomeScreen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            NavigationRailItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null
                    )
                },
                label = {
                    Text("Profile")
                },
                selected = currentRoute == Screen.ProfileScreen.route,
                onClick = {
                    navController.navigate(Screen.ProfileScreen.route) {
                        //Used to control cleaning specific pages in the navigation stack when navigating to a new destination
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }

                        //Prevents the creation of duplicates pages if the target destination already exists at the top of the stack.
                        launchSingleTop = true

                        //With saveState work together to save and restore the state of the page
                        //enhancing user experience by maintaining the state when navigating back to the page.
                        restoreState = true
                    }
                }
            )
        }
    }
}