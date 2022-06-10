package com.locatocam.app.data.requests

data class ReqOffersForYou(
    val brand_ids: String?,
    val cuisine_ids: String?,
    val infl_brand: String?,
    val infl_company_id: String?,
    val keyword: String?,
    val latitude: String?,
    val longitude: String?,
    val offer_id: String?,
    val portal: String?
)