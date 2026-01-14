package com.anastasiaiva.pelmenisegodnya.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.anastasiaiva.pelmenisegodnya.ui.MainScreen
import com.anastasiaiva.pelmenisegodnya.ui.WelcomeScreen
import com.anastasiaiva.pelmenisegodnya.ui.KerilViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: KerilViewModel,
    modifier: Modifier = Modifier,
    onUpdateClick: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route,
        modifier = modifier
    ) {

        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onReadyClick = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Main.route) {
            MainScreen(
                viewModel = viewModel,
                onUpdateClick = {
                    onUpdateClick()
                }
            )
        }
    }
}
