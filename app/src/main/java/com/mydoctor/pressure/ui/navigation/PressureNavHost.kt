package com.mydoctor.pressure.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mydoctor.pressure.ui.data.AddPressureDestination
import com.mydoctor.pressure.ui.data.AddPressureScreen
import com.mydoctor.pressure.ui.pressure.PressureDestination
import com.mydoctor.pressure.ui.pressure.PressureScreen

/**
 * Provides Navigation graph for the application.
 */
@Composable
fun PressureNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = PressureDestination.route,
        modifier = modifier
    ) {
        composable(route = PressureDestination.route) {
            PressureScreen(
                navigateToAddPressure = { navController.navigate(AddPressureDestination.route) },
            )
        }
        composable(route = AddPressureDestination.route) {
            AddPressureScreen(
                navigateBack = { navController.popBackStack() },
            )
        }
    }
}
