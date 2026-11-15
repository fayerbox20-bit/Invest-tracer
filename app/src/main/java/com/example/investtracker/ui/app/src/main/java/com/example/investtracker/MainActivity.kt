package com.example.investtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import com.example.investtracker.ui.DealScreen

private val Dark = darkColorScheme(
    primary = Color(0xFF66BB6A), primaryContainer = Color(0xFF1B5E20)
)
private val Light = lightColorScheme(
    primary = Color(0xFF2E7D32), primaryContainer = Color(0xFFC8E6C9)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = if (isSystemInDarkTheme()) Dark else Light) {
                DealScreen()
            }
        }
    }
}
