package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.VerbaViewModel
import com.example.ui.screens.VerbaMainScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.VerbaDarkBackground

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: VerbaViewModel = viewModel()
            val appSettings by viewModel.appSettings.collectAsStateWithLifecycle()

            MyApplicationTheme(
                themeMode = appSettings.themeMode,
                accentTheme = appSettings.accentTheme
            ) {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(VerbaDarkBackground)
                ) { innerPadding ->
                    VerbaMainScreen(
                        viewModel = viewModel,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}
