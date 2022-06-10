package com.locatocam.app.views.order_online

import com.locatocam.app.data.responses.resp_products_new.Item
import com.locatocam.app.db.entity.Addon
import com.locatocam.app.db.entity.Varient

interface AddonItemClick {
    fun addItem(item: Addon)
    fun increeseItem(item: Addon)
    fun decreeseItem(item: Addon)
}