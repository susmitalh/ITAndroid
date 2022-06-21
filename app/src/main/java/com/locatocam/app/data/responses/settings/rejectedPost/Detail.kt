package com.locatocam.app.data.responses.settings.rejectedPost

data class Detail(
    val approved_by: Any,
    val approved_time: Any,
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
    val rejected_by: String,
    val rejected_reason: String,
    val rejected_time: String,
    val screenshot: String,
    val sub_header: String
)