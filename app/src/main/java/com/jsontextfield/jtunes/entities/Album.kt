package com.jsontextfield.jtunes.entities

data class Album(
    var title: String = "",
    var artist: String = "",
    var coverArt: String = "",
    var date: Long = 0L,
    var id: Long = 0L
)