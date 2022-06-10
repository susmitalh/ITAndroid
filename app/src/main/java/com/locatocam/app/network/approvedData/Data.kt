package com.locatocam.app.network.approvedData

data class Data(
    val approved: Int,
    val brand_pending: Int,
    val details: List<Detail>,
    val pending: Int,
    val post: Int,
    val rejected: Int,
    val rolls: Int
)