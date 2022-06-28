package com.locatocam.app.data.responses.favOrder

data class Data(
    val amount: Int,
    val brand_name: String,
    val items: List<Item>,
    val location: String,
    val logo: String,
    val order_id: String,
    val ordered_on: String,
    val status: String
)