package com.locatocam.app.repositories

import android.app.Application
import com.locatocam.app.ModalClass.PlayPost
import com.locatocam.app.data.requests.ReqLike
import com.locatocam.app.data.responses.SearchModal.RespSearch
import com.locatocam.app.data.responses.like.RespLike
import com.locatocam.app.di.DaggerAppComponent
import com.locatocam.app.network.WebApi
import com.locatocam.app.security.SharedPrefEnc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import net.minidev.json.JSONObject
import retrofit2.Retrofit
import javax.inject.Inject

class PlayPostRepository(val application: Application) {

    @Inject
    lateinit var retrofit: Retrofit


    init {
        DaggerAppComponent.create().inject(this)
    }

    fun getUserID():String{
        return SharedPrefEnc.getPref(application,"user_id")
    }

    fun PlayPost(jsonObject: JSONObject): Flow<PlayPost> {
        return flow {
            val res=retrofit.create(WebApi::class.java).playPost(jsonObject)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }
    fun likeplay(reqlike: ReqLike): Flow<RespLike> {

        return flow {
            val res= retrofit.create(WebApi::class.java).like(reqlike)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }
}