package com.locatocam.app.views.settings

import android.view.View
import com.locatocam.app.data.PendingPost
import com.locatocam.app.data.responses.address.Data
import com.locatocam.app.data.responses.settings.companyPending.Detail
import com.locatocam.app.data.responses.settings.pendingPost.DataX

interface RejectedClickEvents {
    fun Rejectedview(v: View,data: com.locatocam.app.data.responses.settings.rejectedPost.Detail,process:String,type:String)
    fun RejectedRepost(v: View,data: com.locatocam.app.data.responses.settings.rejectedPost.Detail,process:String,type:String)

}