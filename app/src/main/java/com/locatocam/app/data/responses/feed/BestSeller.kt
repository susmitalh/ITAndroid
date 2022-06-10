package com.locatocam.app.data.responses.feed

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class BestSeller {
    var brand_id: String? = null
    var brand_name: String? = null
    var item_id: String? = null
    var item_name: String? = null
    var image: String? = null
    var percentage: String? = null
}