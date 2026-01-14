package com.anastasiaiva.pelmenisegodnya.navigation

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Main : Screen(route = "Main")
}