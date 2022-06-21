package com.locatocam.app.views.settings

import android.view.View
import com.locatocam.app.data.PendingPost
import com.locatocam.app.data.responses.address.Data
import com.locatocam.app.data.responses.settings.companyPending.Detail
import com.locatocam.app.data.responses.settings.pendingPost.DataX

interface PendingClickEvents {
    fun PendingReject(v: View, data: com.locatocam.app.data.responses.settings.pendingPost.Detail, process:String, type:String)
    fun Pendingview(v: View,data: com.locatocam.app.data.responses.settings.pendingPost.Detail,process:String,type:String)
    fun PendingApprove(v: View,data: com.locatocam.app.data.responses.settings.pendingPost.Detail,process:String,type:String)

}