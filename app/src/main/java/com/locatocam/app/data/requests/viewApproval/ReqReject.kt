package com.locatocam.app.data.requests.viewApproval

data class ReqReject(
    val id: Int,
    val reason: String,
    val type: String,
    val user_id: Int
)