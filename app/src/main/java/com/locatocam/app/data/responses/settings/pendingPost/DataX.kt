package com.locatocam.app.data.responses.settings.pendingPost

data class DataX(
    val approved: Int,
    val brand_pending: String,
    val details: List<Detail>,
    val pending: Int,
    val post: Int,
    val rejected: Int,
    val rolls: Int
)