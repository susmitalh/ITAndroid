package com.locatocam.app.data.responses.playrolls

import android.os.Parcel
import android.os.Parcelable

data class RollsData(
    val color: String?,
    val user_id: String?,
    val comment_count: String?,
    var i_following_this_influencer: String?,
    var i_liked_this_rolls: String?,
    val id: String?,
    val influencer_code: String?,
    val influencer_logo: String?,
    val influencer_name: String?,
    val like_count: String?,
    val p_tag: String?,
    val play_type: String?,
    val screenshot: String?,
    val video_desc: String?,
    val video_url: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(color)
        parcel.writeString(comment_count)
        parcel.writeString(i_following_this_influencer)
        parcel.writeString(i_liked_this_rolls)
        parcel.writeString(id)
        parcel.writeString(influencer_code)
        parcel.writeString(influencer_logo)
        parcel.writeString(influencer_name)
        parcel.writeString(like_count)
        parcel.writeString(p_tag)
        parcel.writeString(play_type)
        parcel.writeString(screenshot)
        parcel.writeString(video_desc)
        parcel.writeString(video_url)
    }

    companion object CREATOR : Parcelable.Creator<RollsData> {
        override fun createFromParcel(parcel: Parcel): RollsData {
            return RollsData(parcel)
        }

        override fun newArray(size: Int): Array<RollsData?> {
            return arrayOfNulls(size)
        }
    }
}