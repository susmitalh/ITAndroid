package com.locatocam.app.repositories

import android.app.Application
import com.locatocam.app.ModalClass.FAQ
import com.locatocam.app.data.requests.ReqOnlineOrderHelp
import com.locatocam.app.data.responses.comments.RespGetComments
import com.locatocam.app.di.DaggerAppComponent
import com.locatocam.app.network.WebApi
import com.locatocam.app.security.SharedPrefEnc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Retrofit
import javax.inject.Inject

class OnlineOrderHelpRepository(val application: Application) {
    @Inject
    lateinit var retrofit: Retrofit


    init {
        DaggerAppComponent.create().inject(this)
    }

    fun getUserID():String{
        return SharedPrefEnc.getPref(application,"user_id")
    }

    fun getFAQ(request: ReqOnlineOrderHelp) : Flow<FAQ> {
        return flow {
            val res= retrofit.create(WebApi::class.java).FAQ(request)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }
}