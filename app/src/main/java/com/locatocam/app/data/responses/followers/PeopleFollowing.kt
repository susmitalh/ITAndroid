package com.locatocam.app.data.responses.followers

data class PeopleFollowing(
    val follow_status: String,
    val influencer_code: String,
    val name: String,
    val photo: String,
    val user_id: String,
    val user_type: String
)