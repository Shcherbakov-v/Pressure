package com.mydoctor.pressure

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.mydoctor.pressure.ui.navigation.PressureNavHost

/**
 * Top level composable that represents screens for the application.
 */
@Composable
fun PressureApp(navController: NavHostController = rememberNavController()) {
    PressureNavHost(navController = navController)
}