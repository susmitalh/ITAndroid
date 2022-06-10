package com.locatocam.app.data.requests

data class ReqAddComment(
    val comment: String,
    val comment_type: String,
    val post_id: Int,
    val user_id: Int
)