package com.locatocam.app.views.settings.myPostReelsApprovalPending

import android.view.View
import com.locatocam.app.data.responses.settings.pendingPost.Detail


interface CompanyPendingClickEvents {
    fun Pendingview(v: View,data: Detail,process:String,type:String)
}