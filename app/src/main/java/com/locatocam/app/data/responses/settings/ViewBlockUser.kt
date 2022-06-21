package com.locatocam.app.data.responses.settings

data class ViewBlockUser(
    val `data`: List<ViewBlockUserData>,
    val message: String,
    val status: String
)