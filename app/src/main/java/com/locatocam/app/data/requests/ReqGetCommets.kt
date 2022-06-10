package com.locatocam.app.data.requests

data class ReqGetCommets(
    val comment_type: String,
    val post_id: Int,
    val user_id: Int
)