package com.locatocam.app.data.responses.social_login

import com.locatocam.app.data.responses.verify_ptp.UserDetails

data class SocialLogin(
    val msg: String?,
    val post: Post?,
    val user_details: UserDetails?,
    val status: String?,
    val approval_pending: String?
)