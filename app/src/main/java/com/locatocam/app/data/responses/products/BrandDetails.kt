package com.locatocam.app.data.responses.products

data class BrandDetails(
    val banner: Banner,
    val minimum_order_value: String,
    val offer_val: String,
    val schedule_delivery: String,
    val schedule_max_days: String,
    val schedule_min_hours: String,
    val timing: List<Timing>
)