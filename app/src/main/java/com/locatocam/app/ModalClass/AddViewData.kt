package com.locatocam.app.ModalClass

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class AddViewData {
    @SerializedName("views_count")
    @Expose
    var viewsCount: String? = null
}