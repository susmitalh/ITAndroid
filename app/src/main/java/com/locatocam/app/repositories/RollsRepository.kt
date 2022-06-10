package com.locatocam.app.repositories

import android.app.Application
import com.locatocam.app.data.requests.*
import com.locatocam.app.data.responses.RespFollow
import com.locatocam.app.data.responses.feed.Feed
import com.locatocam.app.data.responses.like.RespLike
import com.locatocam.app.data.responses.playrolls.Rolls
import com.locatocam.app.data.responses.playrolls.RollsData
import com.locatocam.app.data.responses.rolls_and_short_videos.Data
import com.locatocam.app.di.DaggerAppComponent
import com.locatocam.app.network.WebApi
import com.locatocam.app.security.SharedPrefEnc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Retrofit
import javax.inject.Inject

class RollsRepository(val application: Application)  {

    @Inject
    lateinit var retrofit: Retrofit

    init {
        DaggerAppComponent.create().inject(this)
    }
    fun getRolls(reqfeed: ReqPlayRolls): Flow<Rolls> {

        return flow {
            val res= retrofit.create(WebApi::class.java).getPlayRolls(reqfeed)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }

    fun like(reqlike: ReqLike): Flow<RespLike> {

        return flow {
            val res= retrofit.create(WebApi::class.java).like(reqlike)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }

    fun follow(reqFollow: ReqFollow): Flow<RespFollow> {
        return flow {
            val res= retrofit.create(WebApi::class.java).follow(reqFollow)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }

    fun getUserID():String{
            return SharedPrefEnc.getPref(application,"user_id")
    }
}