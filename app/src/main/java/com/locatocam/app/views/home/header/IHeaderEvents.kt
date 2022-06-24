package com.locatocam.app.views.home.header

interface IHeaderEvents {
    fun onItemClick(user_id:String,inf_code:String)
    fun onItemMostPopularVideos(user_id:String,inf_code:String)
    fun onItemRollsAndShortVideos(firstid:String)
    fun onBrandSearchClick(searchId: String?, influencerCode: String?)
}