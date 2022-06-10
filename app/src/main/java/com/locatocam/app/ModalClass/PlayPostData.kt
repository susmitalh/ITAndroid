package com.locatocam.app.ModalClass

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import com.locatocam.app.data.responses.playrolls.RollsData


data class PlayPostData(
    val id: String?,
    val user_id: String?,
    val influencer_name: String?,
    val influencer_logo: String?,
    val influencer_code: String?,
    var i_liked_this_post: String?,
    var i_following_this_influencer: String?,
    val video_url: String?,
    val screenshot: String?,
    val headline: String?,
    val sub_header: String?,
    val content: String?,
    val play_type: String?,
    var like_count: String?,
    var comment_count: String?
    ): Parcelable {
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
        parcel.writeString(id)
        parcel.writeString(user_id)
        parcel.writeString(influencer_name)
        parcel.writeString(influencer_logo)
        parcel.writeString(influencer_code)
        parcel.writeString(i_liked_this_post)
        parcel.writeString(i_following_this_influencer)
        parcel.writeString(video_url)
        parcel.writeString(screenshot)
        parcel.writeString(headline)
        parcel.writeString(sub_header)
        parcel.writeString(content)
        parcel.writeString(play_type)
        parcel.writeString(like_count)
        parcel.writeString(comment_count)
    }
    companion object CREATOR : Parcelable.Creator<PlayPostData> {
        override fun createFromParcel(parcel: Parcel): PlayPostData {
            return PlayPostData(parcel)
        }

        override fun newArray(size: Int): Array<PlayPostData?> {
            return arrayOfNulls(size)
        }
    }
}
