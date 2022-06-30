package com.locatocam.app.views.search

import android.view.View
import com.locatocam.app.data.responses.address.Data

interface ClickEvents {
    fun onClickLocationItem(locationitem: Locationitem)
    fun onClickAddress(data: Data)
    fun showPopup(v: View, item: Data, position: Int)

}