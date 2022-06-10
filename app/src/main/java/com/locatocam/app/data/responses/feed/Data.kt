package com.locatocam.app.data.responses.feed

data class Data(
    val banner_brand_id: String?,
    val banner_brand_name: String?,
    val banner_cuisine: String?,
    val banner_image: String?,
    val banner_km: String?,
    val banner_lat: String?,
    val banner_location: String?,
    val banner_long: String?,
    val banner_offer_perc: String?,
    val banner_ratings: String?,
    val banner_store_id: String?,
    val banner_time: String?,
    val best_seller: List<BestSeller>?,
    var brand_follow: String?,
    val brand_id: String?,
    val brand_lat: Any?,
    val brand_long: Any?,
    val brand_name: String?,
    var comments_count: String?,
    val description: String?,
    val `file`: String?,
    val file_extension: String?,
    val file_extension_type: String?,
    val file_size: String?,
    val header: String?,
    val is_social: String?,
    val item_id: String?,
    var liked: String?,
    var likes_count: String?,
    val offers_details: List<OffersDetail>?,
    val post_date_time: String?,
    val post_id: String?,
    var profile_follow: String?,
    var profile_follow_count: String?,
    val profile_influencer_code: String?,
    val profile_login_type: String?,
    val profile_name: String?,
    val profile_pic: String?,
    val screenshot: String?,
    val shares_count: String?,
    val social_link: String?,
    val subheader: String?,
    val top_brand_details: List<TopBrandDetail>?,
    val type: String?,
    val user_id: String?,
    var views_count: String?
){
    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        emptyList(),
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        emptyList(),
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        emptyList(),
        "",
        "",
        "",
    )

}