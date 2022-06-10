package com.locatocam.app.data.requests

data class ReqMostPopularVideos(
    val influencer_code: String?,
    val offset: Int?,
    val user_id: String?,
    val user_type: String?
)