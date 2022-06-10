package com.locatocam.app.ModalClass

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import com.locatocam.app.data.responses.playrolls.Rolls


class MyPostData {
    @SerializedName("user_id")
    @Expose
    var userId: String? = null

    @SerializedName("influencer_code")
    @Expose
    var influencerCode: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("photo")
    @Expose
    var photo: String? = null

    @SerializedName("follow_count")
    @Expose
    var followCount: String? = null

    @SerializedName("post_count")
    @Expose
    var postCount: String? = null

    @SerializedName("follow_status")
    @Expose
    var followStatus: String? = null

    @SerializedName("posts")
    @Expose
    var posts: List<MyPostsPost>? = null
    @SerializedName("rolls")
    @Expose
    var rolls: List<MyPostsPost>? = null
}