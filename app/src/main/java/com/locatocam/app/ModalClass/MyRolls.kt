package com.locatocam.app.ModalClass

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class MyRolls {

        @SerializedName("views")
        @Expose
        var views: String? = null

        @SerializedName("screenshot")
        @Expose
        var screenshot: String? = null
    }