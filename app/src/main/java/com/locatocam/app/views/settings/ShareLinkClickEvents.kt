package com.locatocam.app.views.settings

import android.view.View
import com.locatocam.app.data.responses.address.Data
import com.locatocam.app.data.responses.settings.companyPending.Detail

interface ShareLinkClickEvents {
    fun sharelink(v: View,infcode:String)

}