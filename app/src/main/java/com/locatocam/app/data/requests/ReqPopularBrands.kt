package com.locatocam.app.data.requests

data class ReqPopularBrands(
    val infl_brand: String,
    val infl_company_id: String,
    val latitude: String,
    val longitude: String,
    val portal: String,
    val rating_filter: String
)