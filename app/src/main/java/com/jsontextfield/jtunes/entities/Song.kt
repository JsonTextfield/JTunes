package com.jsontextfield.jtunes.entities

data class Song(
    val title: String = "",
    val artist: String = "",
    val album: String = "",
    val id: Long = 0L,
    val path: String = "",
    val genre: String = "",
    val duration: Long = 1L,
    val date: Long = 1000L,
    val dateAdded: Long = 1000L,
    val trackNumber: Int = 0,
) {
    var plays = 0
    var lastPlayed = 0L
}