package com.locatocam.app.views.settings.settingInterface

import android.view.View

interface SettingClickListener {
    fun unblock(v: View, userName:String, userId:String)
}