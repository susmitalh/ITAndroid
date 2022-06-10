package com.locatocam.app.data.responses.user_model

import java.io.Serializable

data class Document(
    val doc_description: String?,
    val doc_id: String?,
    var doc_location: String?,
    val doc_name: String?
):Serializable