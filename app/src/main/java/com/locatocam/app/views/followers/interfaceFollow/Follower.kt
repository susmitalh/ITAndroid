package com.locatocam.app.views.followers.interfaceFollow

interface Follower {
    fun follow(follow_type:String,follow_process:String,userId:Int)
    fun remove(follow_type:String,follow_process:String,userId:Int)
}