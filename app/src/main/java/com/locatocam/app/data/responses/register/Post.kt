package com.locatocam.app.data.responses.register

data class Post(
    val email: String?,
    val name: String?,
    val otp: String?,
    val password: String?,
    val phone: String?,
    val referral: String?
)