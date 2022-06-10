package com.locatocam.app.ModalClass

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class MyPostsPost {
    @SerializedName(value = "post_id", alternate = ["rolls_id"])
    var postId: String? = null

    @SerializedName("views")
    @Expose
    var views: String? = null

    @SerializedName("screenshot")
    @Expose
    var screenshot: String? = null
}