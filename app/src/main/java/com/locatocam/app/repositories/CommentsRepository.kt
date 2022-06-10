package com.locatocam.app.repositories

import android.app.Application
import android.util.Log
import com.locatocam.app.data.requests.*
import com.locatocam.app.data.responses.LoginResp
import com.locatocam.app.data.responses.add_comments.RespComments
import com.locatocam.app.data.responses.comments.RespGetComments
import com.locatocam.app.data.responses.feed.Feed
import com.locatocam.app.data.responses.playrolls.Rolls
import com.locatocam.app.data.responses.playrolls.RollsData
import com.locatocam.app.data.responses.register.RespRegister
import com.locatocam.app.data.responses.rolls_and_short_videos.Data
import com.locatocam.app.data.responses.social_login.SocialLogin
import com.locatocam.app.data.responses.verify_ptp.RespVerifyOTP
import com.locatocam.app.di.DaggerAppComponent
import com.locatocam.app.network.WebApi
import com.locatocam.app.security.SharedPrefEnc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Retrofit
import javax.inject.Inject

class CommentsRepository(val application: Application) {

    @Inject
    lateinit var retrofit: Retrofit


    init {
        DaggerAppComponent.create().inject(this)
    }

    fun getComments(reqGetCommets: ReqGetCommets): Flow<RespGetComments> {
        return flow {
            val res= retrofit.create(WebApi::class.java).view_comments(reqGetCommets)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }

    fun addComments(reqAddComment: ReqAddComment): Flow<RespComments> {
        return flow {
            val res= retrofit.create(WebApi::class.java).add_comments(reqAddComment)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }


    fun getUserID():String{
        return SharedPrefEnc.getPref(application,"user_id")
    }
}