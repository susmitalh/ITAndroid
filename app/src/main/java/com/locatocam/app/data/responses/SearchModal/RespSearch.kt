package com.locatocam.app.data.responses.SearchModal

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class RespSearch {
    var status: String? = null
    var message: String? = null
    var data: List<DataSeach>? = null
}