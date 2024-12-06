package com.mydoctor.pressure

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.mydoctor.pressure.ui.theme.PressureTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity of the application.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PressureTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    PressureApp()
                }
            }
        }
    }
}