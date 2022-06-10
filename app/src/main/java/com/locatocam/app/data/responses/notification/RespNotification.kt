package com.locatocam.app.data.responses.notification

data class RespNotification(
    val `data`: List<Data>,
    val message: String,
    val status: String
)