package com.locatocam.app.ModalClass

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class AddShare {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("data")
    @Expose
    var data: AddShareData? = null
}