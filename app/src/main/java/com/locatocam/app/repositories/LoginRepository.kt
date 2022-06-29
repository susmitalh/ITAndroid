package com.locatocam.app.repositories

import android.app.Application
import android.util.Log
import com.locatocam.app.ModalClass.SocialAddNumber
import com.locatocam.app.data.requests.ReqFeed
import com.locatocam.app.data.requests.ReqPlayRolls
import com.locatocam.app.data.requests.ReqRollsAndShortVideos
import com.locatocam.app.data.responses.LoginResp
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

class LoginRepository(val application: Application) {

    @Inject
    lateinit var retrofit: Retrofit


    init {
        DaggerAppComponent.create().inject(this)
    }

    fun login(phone:String,otp:String): Flow<LoginResp> {
        return flow {
            val res= retrofit.create(WebApi::class.java).login(phone,otp)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }
    fun addNumberVerify(phone: String):Flow<SocialAddNumber> {
        return flow {
            val res=retrofit.create(WebApi::class.java).socialAddNumber(phone)
            emit(res)
        }.flowOn(Dispatchers.IO)

    }

    fun verifyOtp(phone:String,otp:String): Flow<RespVerifyOTP> {
        return flow {
            val res= retrofit.create(WebApi::class.java).verifyOTP(phone,otp)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }
    fun socialLogin(name:String,email:String,phone:String): Flow<SocialLogin> {
        return flow {
            val res= retrofit.create(WebApi::class.java).socialLogin(email,phone,name)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }


    fun register(
        name: String,
        email: String,
        phone: String,
        influencer_code: String,
        post_id: String,
        otp: String
    ): Flow<RespRegister> {
        return flow {
            val res= retrofit.create(WebApi::class.java).register(name,
                email,
                phone,
                influencer_code,
                post_id,
                otp
            )
            emit(res)
        }.flowOn(Dispatchers.IO)
    }

    fun setUserPrefs(
       user_id:String,
       phone: String,
       email: String,
       name: String,
       login_type:String,
       influencer_code: String,
       is_admin:String,
       influencer_id:String,
       influencer_company:String,
       influencer_user_id:String,
       customer_id:String
    ) {
        Log.e("setUserPrefs",login_type)


        SharedPrefEnc.setPref("user_id", user_id, application)
        SharedPrefEnc.setPref("mobile", phone, application)
        SharedPrefEnc.setPref("email", email, application)
        SharedPrefEnc.setPref("name", name, application)
        SharedPrefEnc.setPref("user_type", login_type, application)
        SharedPrefEnc.setPref("influencer_code", influencer_code, application)
        SharedPrefEnc.setPref("is_admin", is_admin, application)
        SharedPrefEnc.setPref("influencer_id", influencer_id, application)
        SharedPrefEnc.setPref("type", "influencer", application)
        SharedPrefEnc.setPref("influencer_company", influencer_company, application)
        SharedPrefEnc.setPref("influencer_user_id", influencer_user_id, application)
        SharedPrefEnc.setPref("customer_id", customer_id, application)



    }


}