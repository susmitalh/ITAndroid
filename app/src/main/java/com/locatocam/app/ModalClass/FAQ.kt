package com.locatocam.app.ModalClass

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


data class FAQ (
    var status: String?,
    var message: String?,
    var data: List<FAQData>?
)