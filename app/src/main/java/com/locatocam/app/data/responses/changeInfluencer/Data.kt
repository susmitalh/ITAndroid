package com.locatocam.app.data.responses.changeInfluencer

data class Data(
    val inf_code: String,
    val inf_email: String,
    val inf_id: String,
    val inf_name: String,
    val inf_phone: String,
    val inf_photo: String,
    val inf_social_details: List<InfSocialDetail>,
    val is_influencer: Int,
    val requested_details: String
)