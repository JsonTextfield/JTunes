package com.jsontextfield.jtunes.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.jsontextfield.jtunes.ui.pages.AlbumPage
import com.jsontextfield.jtunes.ui.pages.ArtistPage
import com.jsontextfield.jtunes.ui.pages.GenrePage
import com.jsontextfield.jtunes.ui.pages.PlaylistPage
import com.jsontextfield.jtunes.ui.pages.SongPage
import com.jsontextfield.jtunes.ui.viewmodels.MusicViewModel
import com.jsontextfield.jtunes.ui.viewmodels.PageState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainContent(musicViewModel: MusicViewModel) {
    val pageState by musicViewModel.pageState.collectAsState()
    val musicState by musicViewModel.musicState.collectAsState()
    val pagerState = rememberPagerState { PageState.entries.size }
    val searchText = musicState.searchText

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
                    musicState = musicState,
                    songs = musicViewModel.getSongs(searchText),
                    hintText = "Search from ${musicViewModel.getSongs().size} songs",
                    onCreatePlaylist = musicViewModel::createPlaylistFromSearch,
                    onShuffleClick = musicViewModel::shuffleAndPlay,
                    onItemClick = musicViewModel::playSong,
                    onSearchTextChanged = musicViewModel::onSearchTextChanged,
                )
            }

            PageState.ALBUMS.ordinal -> {
                AlbumPage(
                    musicState = musicState,
                    albums = musicViewModel.getAlbums(searchText),
                    hintText = "Search from ${musicViewModel.getAlbums().size} albums",
                    onItemClick = musicViewModel::playAlbum,
                    onCreatePlaylist = musicViewModel::createPlaylistFromSearch,
                    onSearchTextChanged = musicViewModel::onSearchTextChanged,
                )
            }

            PageState.ARTISTS.ordinal -> {
                ArtistPage(
                    musicState = musicState,
                    artists = musicViewModel.getArtists(searchText),
                    hintText = "Search from ${musicViewModel.getArtists().size} artists",
                    onItemClick = musicViewModel::playArtist,
                    onCreatePlaylist = musicViewModel::createPlaylistFromSearch,
                    onSearchTextChanged = musicViewModel::onSearchTextChanged,
                )
            }

            PageState.GENRES.ordinal -> {
                GenrePage(
                    musicState = musicState,
                    genres = musicViewModel.getGenres(searchText),
                    hintText = "Search from ${musicViewModel.getGenres().size} genres",
                    onItemClick = musicViewModel::playGenre,
                    onCreatePlaylist = musicViewModel::createPlaylistFromSearch,
                    onSearchTextChanged = musicViewModel::onSearchTextChanged,
                )
            }

            PageState.PLAYLISTS.ordinal -> {
                PlaylistPage(
                    musicState = musicState,
                    playlists = musicViewModel.getPlaylists(searchText),
                    hintText = "Search from ${musicViewModel.getPlaylists().size} playlists",
                    onItemClick = musicViewModel::playPlaylist,
                    onSearchTextChanged = musicViewModel::onSearchTextChanged,
                )
            }
        }
    }
}