package com.locatocam.app.data.responses.resp_products_new

data class Addon(
    val group_option_label: String,
    val grp_id: String,
    val items: List<ItemX>,
    val max_sel: String,
    val min_sel: String
)