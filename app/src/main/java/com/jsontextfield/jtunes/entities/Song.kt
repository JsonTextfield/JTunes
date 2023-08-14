package com.jsontextfield.jtunes.entities

import android.os.Parcel
import android.os.Parcelable
import java.util.UUID
import kotlin.random.Random

data class Song(
    var title: String = "",
    var artist: String = "",
    var album: String = "",
    var id: Long = 0L,
    var path: String = "",
    var genres: List<String> = ArrayList<String>(),
    var duration: Long = 1L,
    var date: Long = 1000L,
    var dateAdded: Long = 1000L,
    var trackNumber: Int = 0,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        title = parcel.readString()!!,
        artist = parcel.readString()!!,
        album = parcel.readString()!!,
        id = parcel.readLong(),
        path = parcel.readString()!!,
        duration = parcel.readLong(),
        date = parcel.readLong(),
        dateAdded = parcel.readLong(),
        trackNumber = parcel.readInt(),
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(artist)
        parcel.writeString(album)
        parcel.writeLong(id)
        parcel.writeString(path)
        parcel.writeLong(duration)
        parcel.writeLong(date)
        parcel.writeLong(dateAdded)
        parcel.writeInt(trackNumber)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Song> {
        override fun createFromParcel(parcel: Parcel): Song {
            return Song(parcel)
        }

        override fun newArray(size: Int): Array<Song?> {
            return arrayOfNulls(size)
        }

        fun random(): Song {
            return Song(
                title = "Song ${UUID.randomUUID()}",
                artist = "Artist",
                album = "Album",
                duration = Random.nextLong(100, 200) * 1000,
                date = Random.nextLong(100000, 200000000) * 1000,
                trackNumber = Random.nextInt(1, 17),
            )
        }
    }
}