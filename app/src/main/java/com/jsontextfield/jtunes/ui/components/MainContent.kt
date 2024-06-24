package com.jsontextfield.jtunes.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.jsontextfield.jtunes.MusicViewModel
import com.jsontextfield.jtunes.PageState
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
                    songs = musicViewModel.getSongs(searchText),
                    hintText = "Search from ${musicViewModel.getSongs().size} songs",
                    onCreatePlaylist = { musicViewModel.createPlaylistFromSearch() },
                    onShuffleClick = { musicViewModel.shuffleAndPlay() },
                    onItemClick = { musicViewModel.playSong(it) }
                )
            }

            PageState.ALBUMS.ordinal -> {
                AlbumPage(
                    musicViewModel = musicViewModel,
                    albums = musicViewModel.getAlbums(searchText),
                    hintText = "Search from ${musicViewModel.getAlbums().size} albums",
                    onItemClick = { musicViewModel.playAlbum(it) },
                    onCreatePlaylist = { musicViewModel.createPlaylistFromSearch() },
                )
            }

            PageState.ARTISTS.ordinal -> {
                ArtistPage(
                    musicViewModel = musicViewModel,
                    artists = musicViewModel.getArtists(searchText),
                    hintText = "Search from ${musicViewModel.getArtists().size} artists",
                    onItemClick = { musicViewModel.playArtist(it) },
                    onCreatePlaylist = { musicViewModel.createPlaylistFromSearch() },
                )
            }

            PageState.GENRES.ordinal -> {
                GenrePage(
                    musicViewModel = musicViewModel,
                    genres = musicViewModel.getGenres(searchText),
                    hintText = "Search from ${musicViewModel.getGenres().size} genres",
                    onItemClick = { musicViewModel.playGenre(it) },
                    onCreatePlaylist = { musicViewModel.createPlaylistFromSearch() },
                )
            }

            PageState.PLAYLISTS.ordinal -> {
                PlaylistPage(
                    musicViewModel = musicViewModel,
                    playlists = musicViewModel.getPlaylists(searchText),
                    hintText = "Search from ${musicViewModel.getPlaylists().size} playlists",
                    onItemClick = { musicViewModel.playPlaylist(it) },
                )
            }
        }
    }
}