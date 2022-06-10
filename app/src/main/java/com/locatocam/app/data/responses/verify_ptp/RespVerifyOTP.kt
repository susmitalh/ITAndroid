package com.locatocam.app.data.responses.verify_ptp

data class RespVerifyOTP(
    val msg: String?,
    val post: Post?,
    val user_details: UserDetails?,
    val status: String?
)