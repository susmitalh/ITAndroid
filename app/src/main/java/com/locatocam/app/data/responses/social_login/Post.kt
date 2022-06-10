package com.locatocam.app.data.responses.social_login

data class Post(
    var user_id: String?,
    var phone: String?,
    var email: String?,
    var name: String?,
    var login_type: String?,
    var influencer_code: String?,
    var is_admin: String?,
    var influencer_id: String?,
    var influencer_company: String?,
    var influencer_user_id: String?,
    var customer_id: String?
)