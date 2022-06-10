package com.locatocam.app.network.approvedData

data class Detail(
    val approved_by: String?,
    val approved_time: String?,
    val brand: String?,
    val brand_status: String?,
    var description: String?,
    val `file`: String?,
    val file_type: String?,
    val header: String?,
    val id: String?,
    val is_social: String?,
    val posted_by: String?,
    val posted_on: String?,
    val rejected_by: String?,
    val rejected_reason: String?,
    val rejected_time: String?,
    val screenshot: String?,
    val sub_header: String?
)