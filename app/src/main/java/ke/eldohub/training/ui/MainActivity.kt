package ke.eldohub.training.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import ke.eldohub.training.ui.theme.EldoHubTheme

import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import ke.eldohub.training.ui.accessibility.AccessibilityViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val accessibilityViewModel: AccessibilityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val accessibilityState by accessibilityViewModel.uiState.collectAsState()
            EldoHubTheme(highContrast = accessibilityState.highContrast) {
                AppNavigation()
            }
        }
    }
}
