package com.locatocam.app.views.settings.myPostReelsApprovalPending

import android.view.View
import com.locatocam.app.data.responses.address.Data
import com.locatocam.app.data.responses.settings.companyPending.Detail

interface CompanyApprovedClickEvents {
    fun Approvedview(v: View,data: com.locatocam.app.data.responses.settings.Approved.Detail,process:String,type:String)

}