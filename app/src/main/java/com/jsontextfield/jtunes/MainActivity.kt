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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Person
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.media3.common.util.UnstableApi
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.jsontextfield.jtunes.ui.components.ErrorScreen
import com.jsontextfield.jtunes.ui.components.LoadingScreen
import com.jsontextfield.jtunes.ui.components.MainScreen
import com.jsontextfield.jtunes.ui.components.PlayerButton
import com.jsontextfield.jtunes.ui.components.menu.Action
import com.jsontextfield.jtunes.ui.theme.AppTheme

@UnstableApi
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val musicViewModel by viewModels<MusicViewModel> { MusicViewModel.MusicViewModelFactory }
        if (intent.getBooleanExtra("fromService", false)) {
            musicViewModel.onSongChanged(
                musicViewModel.musicLibrary.queue[intent.getIntExtra("song", 0)]
            )
        }
        loadPage(musicViewModel)
    }

    @OptIn(ExperimentalPermissionsApi::class)
    private fun loadPage(musicViewModel: MusicViewModel) {
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
                val pageState by musicViewModel.pageState.collectAsState()
                when (uiState) {
                    UIState.LOADING -> {
                        LoadingScreen()
                    }

                    UIState.ERROR -> {
                        ErrorScreen()
                    }

                    UIState.LOADED -> {
                        val onPlayerButtonPressed: (PlayerButton) -> Unit = { playerButton ->
                            when (playerButton) {
                                PlayerButton.PLAY_PAUSE -> {
                                    musicViewModel.mediaController?.let {
                                        if (it.isPlaying) {
                                            it.pause()
                                        }
                                        else {
                                            it.play()
                                        }
                                    }
                                }

                                PlayerButton.NEXT -> {
                                    musicViewModel.mediaController?.let {
                                        if (it.mediaItemCount > 0) {
                                            it.seekToNextMediaItem()
                                        }
                                    }
                                }

                                PlayerButton.PREVIOUS -> {
                                    musicViewModel.mediaController?.let {
                                        if (it.mediaItemCount > 0) {
                                            it.seekToPrevious()
                                        }
                                    }
                                }

                                PlayerButton.PREVIOUS_SONG -> {
                                    musicViewModel.mediaController?.let {
                                        if (it.mediaItemCount > 0) {
                                            it.seekToPreviousMediaItem()
                                        }
                                    }
                                }

                                PlayerButton.SHUFFLE -> {
                                    musicViewModel.mediaController?.let {
                                        it.shuffleModeEnabled = !it.shuffleModeEnabled
                                    }
                                }

                                PlayerButton.LOOP -> {
                                    musicViewModel.mediaController?.let {
                                        it.repeatMode = (it.repeatMode + 2) % 3
                                    }
                                }
                            }
                        }
                        val actions: List<Action> = listOf(
                            Action(
                                tooltip = stringResource(id = R.string.songs),
                                icon = Icons.Rounded.MusicNote,
                                isChecked = pageState == PageState.SONGS,
                                onClick = { musicViewModel.onPageChanged(PageState.SONGS) },
                            ),
                            Action(
                                tooltip = stringResource(id = R.string.albums),
                                icon = Icons.Rounded.Album,
                                isChecked = pageState == PageState.ALBUMS,
                                onClick = { musicViewModel.onPageChanged(PageState.ALBUMS) },
                            ),
                            Action(
                                tooltip = stringResource(id = R.string.artists),
                                icon = Icons.Rounded.Person,
                                isChecked = pageState == PageState.ARTISTS,
                                onClick = { musicViewModel.onPageChanged(PageState.ARTISTS) },
                            ),
                            Action(
                                tooltip = stringResource(id = R.string.genres),
                                icon = Icons.Rounded.Brush,
                                isChecked = pageState == PageState.GENRES,
                                onClick = { musicViewModel.onPageChanged(PageState.GENRES) },
                            ),
                            Action(
                                tooltip = stringResource(id = R.string.playlists),
                                icon = Icons.AutoMirrored.Rounded.QueueMusic,
                                isChecked = pageState == PageState.PLAYLISTS,
                                onClick = { musicViewModel.onPageChanged(PageState.PLAYLISTS) },
                            ),
                        )
                        MainScreen(
                            musicViewModel = musicViewModel,
                            onPlayerAction = onPlayerButtonPressed,
                            actions = actions,
                        )
                    }
                }
            }
        }
    }
}
