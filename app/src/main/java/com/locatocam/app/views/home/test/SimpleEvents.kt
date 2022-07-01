package com.locatocam.app.views.home.test

import com.locatocam.app.data.responses.feed.Data

interface SimpleEvents {
    fun like(process:String,post_id:String)
    fun trash(post_id:String,position:Int)
    fun isHeaderAdded():Boolean
    fun addHeader()
    fun editPost(item: Data,position: Int)
}