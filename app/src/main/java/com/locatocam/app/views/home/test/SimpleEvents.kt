package com.locatocam.app.views.home.test

interface SimpleEvents {
    fun like(process:String,post_id:String)
    fun trash(post_id:String,position:Int)
    fun isHeaderAdded():Boolean
    fun addHeader()
}