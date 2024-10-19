package com.onebilliongod.android.jetpackandroid.view.layout


sealed class Screen(val route: String) {
    data object SideScreen : Screen("side")
    data object MainScreen : Screen("main")
    data object HomeScreen : Screen("home")
    data object LoginScreen : Screen("login")
    data object ProfileScreen : Screen("profile")
}