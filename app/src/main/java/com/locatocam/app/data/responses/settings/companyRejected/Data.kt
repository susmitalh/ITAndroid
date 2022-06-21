package com.locatocam.app.data.responses.settings.companyRejected

data class Data(
    val approved: Int,
    val details: List<Detail>,
    val pending: Int,
    val rejected: Int
)