package com.jsontextfield.jtunes.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.pluralStringResource
import com.jsontextfield.jtunes.MusicViewModel
import com.jsontextfield.jtunes.PageState
import com.jsontextfield.jtunes.R
import com.jsontextfield.jtunes.entities.Playlist
import com.jsontextfield.jtunes.entities.Song
import com.jsontextfield.jtunes.ui.pages.AlbumPage
import com.jsontextfield.jtunes.ui.pages.ArtistPage
import com.jsontextfield.jtunes.ui.pages.GenrePage
import com.jsontextfield.jtunes.ui.pages.PlaylistPage
import com.jsontextfield.jtunes.ui.pages.SongPage


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainContent(musicViewModel: MusicViewModel) {
    val pageState by musicViewModel.pageState.collectAsState()
    val searchText by musicViewModel.searchText.collectAsState()
    val pagerState = rememberPagerState { PageState.entries.size }

    LaunchedEffect(pageState) {
        pagerState.scrollToPage(pageState.ordinal)
    }
    LaunchedEffect(pagerState.currentPage) {
        musicViewModel.onPageChanged(PageState.entries[pagerState.currentPage])
    }
    HorizontalPager(pagerState) { index ->
        when (index) {
            PageState.SONGS.ordinal -> {
                SongPage(
                    musicViewModel = musicViewModel,
                    songs = musicViewModel.musicLibrary.songs.filter { song ->
                        song.title.contains(searchText, true)
                    },
                    hintText = "Search from ${musicViewModel.musicLibrary.songs.size} songs",
                    onCreatePlaylist = {
                        musicViewModel.musicLibrary.playlists.add(
                            Playlist(
                                title = musicViewModel.searchText.value,
                                songs = musicViewModel.musicLibrary.songs.filter { song ->
                                    song.title.contains(searchText, true)
                                },
                            )
                        )
                    },
                    onShuffleClick = {
                        musicViewModel.mediaController?.shuffleModeEnabled = true
                        musicViewModel.musicLibrary.queue =
                            ArrayList(musicViewModel.musicLibrary.songs)
                        musicViewModel.loadQueue()
                        musicViewModel.mediaController?.play()
                    },
                    onItemClick = { song ->
                        if (musicViewModel.mediaController?.mediaItemCount == 0 ||
                            musicViewModel.musicLibrary.queue != ArrayList(musicViewModel.musicLibrary.songs)
                        ) {
                            musicViewModel.musicLibrary.queue =
                                ArrayList(musicViewModel.musicLibrary.songs)
                            musicViewModel.loadQueue()
                        }
                        musicViewModel.mediaController?.seekToDefaultPosition(
                            musicViewModel.musicLibrary.queue.indexOf(song)
                        )
                        musicViewModel.mediaController?.play()
                        musicViewModel.onSongChanged(
                            song = song,
                            nextSong = musicViewModel.musicLibrary.queue.getOrNull(
                                musicViewModel.mediaController?.nextMediaItemIndex ?: -1
                            ),
                            previousSong = musicViewModel.musicLibrary.queue.getOrNull(
                                musicViewModel.mediaController?.previousMediaItemIndex ?: -1
                            ),
                        )
                    }
                )
            }

            PageState.ALBUMS.ordinal -> {
                AlbumPage(
                    musicViewModel = musicViewModel,
                    albums = musicViewModel.musicLibrary.albums.filter { album ->
                        album.title.contains(searchText, true)
                    },
                    hintText = "Search from ${musicViewModel.musicLibrary.albums.size} albums",
                    onItemClick = { album ->
                        musicViewModel.musicLibrary.queue =
                            ArrayList(musicViewModel.musicLibrary.songs
                                .filter { song: Song ->
                                    song.album == album.title
                                }.sortedBy { song: Song ->
                                    song.trackNumber
                                }
                            )
                        musicViewModel.loadQueue()
                        musicViewModel.mediaController?.play()
                        musicViewModel.onSongChanged(musicViewModel.musicLibrary.queue.first())
                    },
                )
            }

            PageState.ARTISTS.ordinal -> {
                ArtistPage(
                    musicViewModel = musicViewModel,
                    artists = musicViewModel.musicLibrary.artists.filter { artist ->
                        artist.name.contains(searchText, true)
                    },
                    hintText = "Search from ${musicViewModel.musicLibrary.artists.size} artists",
                    onItemClick = { artist ->
                        musicViewModel.musicLibrary.queue =
                            ArrayList(musicViewModel.musicLibrary.songs
                                .filter { song: Song ->
                                    song.artist == artist.name
                                }
                            )
                        musicViewModel.loadQueue()
                        musicViewModel.mediaController?.play()
                        musicViewModel.onSongChanged(musicViewModel.musicLibrary.queue.first())
                    },
                )
            }

            PageState.GENRES.ordinal -> {
                GenrePage(
                    musicViewModel = musicViewModel,
                    genres = musicViewModel.musicLibrary.genres.filter { genre ->
                        genre.name.contains(searchText, true)
                    },
                    hintText = "Search from ${musicViewModel.musicLibrary.genres.size} genres",
                    onItemClick = { genre ->
                        musicViewModel.musicLibrary.queue =
                            ArrayList(musicViewModel.musicLibrary.songs
                                .filter { song: Song ->
                                    song.genre == genre.name
                                }
                            )
                        musicViewModel.loadQueue()
                        musicViewModel.mediaController?.play()
                        musicViewModel.onSongChanged(musicViewModel.musicLibrary.queue.first())
                    },
                )
            }

            PageState.PLAYLISTS.ordinal -> {
                PlaylistPage(
                    musicViewModel = musicViewModel,
                    playlists = musicViewModel.musicLibrary.playlists.sortedBy { it.title }
                        .filter { playlist ->
                            playlist.title.contains(searchText, true)
                        },
                    hintText = "Search from ${
                        pluralStringResource(
                            R.plurals.playlists,
                            musicViewModel.musicLibrary.playlists.size,
                            musicViewModel.musicLibrary.playlists.size
                        )
                    }",
                    onItemClick = { playlist ->
                        musicViewModel.musicLibrary.queue = ArrayList(playlist.songs)
                        musicViewModel.loadQueue()
                        musicViewModel.mediaController?.play()
                        musicViewModel.onSongChanged(musicViewModel.musicLibrary.queue.first())
                    },
                )
            }
        }
    }
}