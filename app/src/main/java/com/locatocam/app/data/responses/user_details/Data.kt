package com.locatocam.app.data.responses.user_details

data class Data(
    val about: String?,
    val address: String?,
    val comments: String?,
    val created: String?,
    val email: String?,
    val influencer_code: String?,
    val likes: String?,
    val logo: List<Logo>,
    val name: String?,
    val phone: String?,
    val post: String?,
    val social_details: List<SocialDetail>?,
    val top_or_our_brands: TopOrOurBrands?,
    val user_id: String?,
    val user_type: String?,
    val views: String?,
    val follow_status: String?
)