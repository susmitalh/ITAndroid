package com.locatocam.app.views.order_online

import com.locatocam.app.data.responses.resp_products_new.Item
import com.locatocam.app.db.entity.Varient

interface VarientItemClick {
    fun addItem(item: Varient)
    fun increeseItem(item: Varient)
    fun decreeseItem(item: Varient)
    fun addItemCustomizable(item: Varient)
}