package com.locatocam.app.data.responses.settings.Approved

data class DataX(
    val approved: Int,
    val brand_pending: Int,
    val details: List<Detail>,
    val pending: Int,
    val post: Int,
    val rejected: Int,
    val rolls: Int
)
data class PageDetails(val key: String,var currentPage: Int,var totalPages: Int)