package com.locatocam.app.views.settings

import android.view.View
import com.locatocam.app.data.responses.address.Data
import com.locatocam.app.data.responses.settings.companyPending.Detail

interface CompanyApprovedClickEvents {
    fun ApprovedReject(v: View, data: com.locatocam.app.data.responses.settings.companyApproved.Detail, process:String, type:String)
    fun Approvedview(v: View,data: com.locatocam.app.data.responses.settings.companyApproved.Detail,process:String,type:String)

}