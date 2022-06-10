package com.locatocam.app.custom

import android.util.Log

class TestYu:Abcv() {
    init {
        Log.i("redcvv","child constructor invoked")
    }
    override fun invokeA(){
        super.invokeA()
        Log.i("styuu","child method invoked")
    }
}