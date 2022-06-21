package com.locatocam.app.views.settings

import android.view.View


interface CompanyPendingClickEvents {
    fun PendingReject(v: View, data: com.locatocam.app.data.responses.settings.companyPending.Detail, process:String, type:String)
    fun Pendingview(v: View,data: com.locatocam.app.data.responses.settings.companyPending.Detail,process:String,type:String)
    fun PendingApprove(v: View,data: com.locatocam.app.data.responses.settings.companyPending.Detail,process:String,type:String)

}