package com.locatocam.app.data.responses.resp_products_new

data class Menu(
    val category_id: String,
    val category_name: String,
    val items: List<Item>
)