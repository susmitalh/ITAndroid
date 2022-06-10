package com.locatocam.app.views.order_online

import com.locatocam.app.data.responses.resp_products_new.Item

interface ProductItemClick {
    fun addItem(item: Item)
    fun increeseItem(item: Item)
    fun decreeseItem(item: Item)
    fun addItemCustomizable(item: Item)
}