package com.locatocam.app.data.responses.popular_brands

data class Data(
    val banner_image_url: String,
    val brand_active: Int,
    val brand_size: Any,
    val brand_size_id: Any,
    val closing_hours_time: String,
    val cuisine: String,
    val distance: Double,
    val foodstamart_status: String,
    val id: String,
    val image_url: String,
    val is_pure_veg: Any,
    val latitude: String,
    val longitude: String,
    val merchant_store_status: String,
    val name: String,
    val name_sec: String,
    val next_starting: Any,
    val offer_perc: Any,
    val opening_hours: String,
    val opening_hours_time: String,
    val outlet_fullname: String,
    val rating: Int,
    val slides: Any,
    val store_id: String
)