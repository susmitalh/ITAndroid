package com.locatocam.app.data.responses.comments

data class RespGetComments(
    val `data`: List<Data>,
    val message: String,
    val status: String
)