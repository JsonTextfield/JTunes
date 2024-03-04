package com.jsontextfield.jtunes.entities

import android.os.Parcel
import android.os.Parcelable

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
) : Parcelable {
    constructor(parcel: Parcel) : this(
        title = parcel.readString() ?: "",
        artist = parcel.readString() ?: "",
        album = parcel.readString() ?: "",
        id = parcel.readLong(),
        path = parcel.readString() ?: "",
        duration = parcel.readLong(),
        date = parcel.readLong(),
        dateAdded = parcel.readLong(),
        trackNumber = parcel.readInt(),
        genre = parcel.readString() ?: "",
    )

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
        parcel.writeString(genre)
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
    }
}