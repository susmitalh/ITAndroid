package com.locatocam.app.data.requests

data class ReqSaveAddress(
    val add_save_as: String?,
    val address: String?,
    val flat_no: String?,
    val landmark: String?,
    val lat: String?,
    val lng: String?,
    val phone: String?,
    val pin_code: Int?,
    val place: String?
)