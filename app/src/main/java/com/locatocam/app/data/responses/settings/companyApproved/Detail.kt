package com.locatocam.app.data.responses.settings.companyApproved

data class Detail(
    val approved_by: String,
    val approved_time: String,
    val brand: String,
    val brand_status: String,
    val description: String,
    val `file`: String,
    val file_type: String,
    val header: String,
    val id: String,
    val is_social: String,
    val posted_by: String,
    val posted_on: String,
    val rejected_by: Any,
    val rejected_reason: Any,
    val rejected_time: Any,
    val screenshot: String,
    val sub_header: String
)