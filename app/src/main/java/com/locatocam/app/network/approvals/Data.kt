package com.locatocam.app.network.approvals

data class Data(
    val approved: Int,
    val brand_pending: String,
    val details: List<Detail>,
    val pending: Int,
    val post: String,
    val rejected: Int,
    val rolls: String
)