package com.mydoctor.pressure.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mydoctor.pressure.ui.data.AddDataDestination
import com.mydoctor.pressure.ui.data.AddDataScreen
import com.mydoctor.pressure.ui.data.MeasurementLogDestination
import com.mydoctor.pressure.ui.data.MeasurementLogScreen
import com.mydoctor.pressure.ui.data.PressureDetailsDestination
import com.mydoctor.pressure.ui.data.PressureDetailsScreen
import com.mydoctor.pressure.ui.targets.TargetDestination
import com.mydoctor.pressure.ui.targets.TargetScreen
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
                navigateToAddData = { navController.navigate(AddDataDestination.route) },
                navigateToMeasurementLog = { navController.navigate(MeasurementLogDestination.route) },
                navigateToTarget = { navController.navigate("${TargetDestination.route}/$it") },
            )
        }
        composable(route = AddDataDestination.route) {
            AddDataScreen(
                navigateBack = { navController.popBackStack() },
            )
        }
        composable(route = MeasurementLogDestination.route) {
            MeasurementLogScreen(
                navigateBack = { navController.popBackStack() },
                navigateToAddData = { navController.navigate(AddDataDestination.route) },
                navigateToPressureDetails = { navController.navigate("${PressureDetailsDestination.route}/$it") },
            )
        }
        composable(
            route = PressureDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(PressureDetailsDestination.pressureIdArg) {
                type = NavType.LongType
            })
        ) {
            PressureDetailsScreen(
                navigateBack = { navController.popBackStack() },
                //onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(route = TargetDestination.routeWithArgs,
            arguments = listOf(navArgument(TargetDestination.targetIdArg) {
                type = NavType.LongType
            })
        ) {
            TargetScreen(
                navigateBack = { navController.popBackStack() },
            )
        }
    }
}
