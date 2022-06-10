package com.locatocam.app.data.responses.register

import com.locatocam.app.data.responses.verify_ptp.UserDetails

data class RespRegister(
    val msg: String?,
    val post: Post?,
    val user_details: UserDetails?,
    val otp: String?,
    val status: String?,
    val approval_pending:String?
)