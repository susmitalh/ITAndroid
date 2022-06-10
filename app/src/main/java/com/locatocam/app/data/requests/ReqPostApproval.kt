package com.locatocam.app.data.requests

data class ReqPostApproval(
    val offset: String,
    val process: String,
    val type: String,
    val user_id: String
)