package com.locatocam.app.data.responses.SearchModal

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class DataSeach {
    @SerializedName("user_id")
    @Expose
    var userId: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("influencer_code")
    @Expose
    var influencerCode: String? = null

    @SerializedName("user_type")
    @Expose
    var userType: String? = null

    @SerializedName("logo")
    @Expose
    var logo: String? = null
}