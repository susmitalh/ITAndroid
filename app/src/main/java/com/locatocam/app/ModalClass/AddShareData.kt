package com.locatocam.app.ModalClass

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class AddShareData {
    @SerializedName("share_count")
    @Expose
    var shareCount: String? = null
}