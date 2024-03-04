package com.jsontextfield.jtunes.entities

data class Album(
    val title: String = "",
    val artist: String = "",
    val coverArt: String = "",
    val date: Long = 0L,
    val id: Long = 0L
)