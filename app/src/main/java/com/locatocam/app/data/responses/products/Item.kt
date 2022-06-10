package com.locatocam.app.data.responses.products

data class Item(
    val active: Any,
    val addons: Any,
    val buy_item_qty: Int,
    val campaign_type: String,
    val description: String,
    val discount: Int,
    val dish_type: String,
    val get_item_qty: Int,
    val id: String,
    val image_url: String,
    val in_stock: Int,
    val item_tags: String,
    val name: String,
    val next_starting: Any,
    val offer_applied: Int,
    val packaging_charge: String,
    val price: String,
    val sell_count: String,
    val stock_status: String,
    val type: String,
    val variants: Any
)