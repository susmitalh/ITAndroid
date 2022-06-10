package com.locatocam.app.data.requests.reqUserProfile

data class ReqProfileData(
    val address: String,
    val adhaar_no: String,
    val bank_ac_name: String,
    val bank_ac_no: String,
    val bank_branch: String,
    val bank_ifsc: String,
    val bank_name: String,
    val city: String,
    val dob: String,
    val email: String,
    val gender: String,
    val name: String,
    val pan_no: String,
    val phone: String,
    val pincode: String,
    val social_details: List<SocialDetail>,
    val state: String,
    val user_id: Int
)