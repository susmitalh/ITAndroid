package com.locatocam.app.data.responses.user_model

import java.io.Serializable

data class UserDetails (
    val address: String,
    val adhaar_no: String,
    val bank_ac_name: String,
    val bank_ac_no: String,
    val bank_branch: String,
    val bank_ifsc: String,
    val bank_name: String,
    var city_id: String,
    val city_name: String,
    val dob: String,
    val documents: List<Document>,
    val email: String,
    val gender: String,
    val influencer_code: String,
    val is_admin: String,
    val name: String,
    val pan_no: String,
    val phone: String,
    val pincode: String,
    val social_details: List<SocialDetail>,
    var state_id: String,
    val state_name: String,
    val user_id: String,
    val user_type: String
):Serializable