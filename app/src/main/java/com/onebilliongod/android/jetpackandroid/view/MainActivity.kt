package com.onebilliongod.android.jetpackandroid.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.onebilliongod.android.jetpackandroid.view.layout.MainScreen
import com.onebilliongod.android.jetpackandroid.view.layout.Screen
import com.onebilliongod.android.jetpackandroid.view.login.AuthViewModel
import com.onebilliongod.android.jetpackandroid.view.login.LoginScreen
import com.onebilliongod.android.jetpackandroid.view.ui.theme.JetpackAndroidTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * @AndroidEntryPoint is used to enable Hilt to perform dependency injection in Android components such as Activities,
 * Fragments and Services by generating the necessary code for managing their dependencies.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /**
     * ExperimentalMaterial3WindowSizeClassApi allows the use of experimental Material 3 APIS
     * that adapt the layout based on window size.
     */
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Force horizontal screen
        //requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        enableEdgeToEdge()

        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            JetpackAndroidTheme {
                EntryScreen(windowSize = windowSizeClass)
            }
        }
    }
}

/**
 * Entry screen that checks authentication status and navigates to the main screen if Logged in
 */
@Composable
fun EntryScreen(authViewModel: AuthViewModel = hiltViewModel(),
                windowSize: WindowSizeClass) {
    val navController = rememberNavController()

    //listens for logout event
    val isLogin by authViewModel::isLogin
    if (isLogin) {
        navController.navigate(Screen.LoginScreen.route) {
            popUpTo(Screen.MainScreen.route) { inclusive = true }
        }
    }

    //check accessToken and navigation to MainScreen if logged in
    LaunchedEffect(Unit) {
        authViewModel.checkAccessToken()
        if (authViewModel.isLoggedIn) {
            navController.navigate(Screen.MainScreen.route)
        } else {
            navController.navigate(Screen.LoginScreen.route)
        }
    }

    //Define the navigation graph with LoginScreen as the start destination
    NavHost(navController = navController, startDestination = Screen.LoginScreen.route) {
        composable(Screen.LoginScreen.route) { LoginScreen(navController) }
        composable(Screen.MainScreen.route) { MainScreen(windowSize = windowSize) }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JetpackAndroidTheme {
        Greeting("Android")
    }
}