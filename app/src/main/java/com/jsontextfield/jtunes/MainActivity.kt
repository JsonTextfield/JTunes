package com.jsontextfield.jtunes

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.media3.common.util.UnstableApi
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.jsontextfield.jtunes.ui.components.ErrorScreen
import com.jsontextfield.jtunes.ui.components.LoadingScreen
import com.jsontextfield.jtunes.ui.components.MainScreen
import com.jsontextfield.jtunes.ui.theme.AppTheme

@UnstableApi
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val musicViewModel by viewModels<MusicViewModel> { MusicViewModel.MusicViewModelFactory }
        if (intent.getBooleanExtra("fromService", false)) {
            musicViewModel.onSongChanged(
                musicViewModel.musicLibrary.queue[intent.getIntExtra("song", 0)]
            )
        }
        setContent {
            AppTheme {
                val permissionState = rememberPermissionState(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Manifest.permission.READ_MEDIA_AUDIO
                    }
                    else {
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    }
                )

                val requestPermissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    if (isGranted) {
                        musicViewModel.load(this@MainActivity)
                    }
                    else {
                        musicViewModel.onUIStateChanged(UIState.ERROR)
                    }
                }

                LaunchedEffect(permissionState) {
                    requestPermissionLauncher.launch(permissionState.permission)
                }

                val uiState by musicViewModel.uiState.collectAsState()
                when (uiState) {
                    UIState.LOADING -> {
                        LoadingScreen()
                    }

                    UIState.ERROR -> {
                        ErrorScreen()
                    }

                    UIState.LOADED -> {
                        MainScreen(musicViewModel)
                    }
                }
            }
        }
    }
}
