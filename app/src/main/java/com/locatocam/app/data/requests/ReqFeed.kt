package com.locatocam.app.data.requests

data class ReqFeed(
    val coming_from: String?,
    val influencer_code: String?,
    val latitude: Double?,
    val longitude: Double?,
    val offset: String?,
    val search_type: String?,
    val user_id: Int?,
        val post_id: String?
)