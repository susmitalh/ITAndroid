package com.locatocam.app.data.responses

import android.os.Parcel
import android.os.Parcelable

class VideoItem(
    var videoURL: String?,
    var videoTitle: String?,
    var videoDesc: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(videoURL)
        parcel.writeString(videoTitle)
        parcel.writeString(videoDesc)
    }

    companion object CREATOR : Parcelable.Creator<VideoItem> {
        override fun createFromParcel(parcel: Parcel): VideoItem {
            return VideoItem(parcel)
        }

        override fun newArray(size: Int): Array<VideoItem?> {
            return arrayOfNulls(size)
        }
    }

}