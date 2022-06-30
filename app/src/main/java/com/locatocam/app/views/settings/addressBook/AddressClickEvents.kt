package com.locatocam.app.views.settings.addressBook

import android.view.View
import com.locatocam.app.data.responses.address.Data

interface AddressClickEvents {
    fun Remove(v: View, data:Data)

}