package com.locatocam.app.views.settings

import com.locatocam.app.network.Resource
import com.locatocam.app.network.Status

data class Update<out T>(val status: Status, val data: T?){

    companion object {

        fun <T> added(data: T?): Update<T> {
            return Update(Status.SUCCESS, data)
        }

        fun <T> error(msg: String, data: T?): Update<T> {
            return Update(Status.ERROR, data)
        }

        fun <T> loading(data: T?): Update<T> {
            return Update(Status.LOADING, data)
        }
    }

}
