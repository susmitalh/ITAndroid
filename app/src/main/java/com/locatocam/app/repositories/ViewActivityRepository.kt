package com.locatocam.app.repositories

import android.app.Application
import com.locatocam.app.data.requests.*
import com.locatocam.app.data.responses.followers.RespFollowers
import com.locatocam.app.data.responses.my_activity.RespMyActivity
import com.locatocam.app.data.responses.notification.RespNotification
import com.locatocam.app.data.responses.popular_brands.RespPopularBrands
import com.locatocam.app.data.responses.products.RespProducts
import com.locatocam.app.data.responses.selected_brands.RespSelectedBrand
import com.locatocam.app.data.responses.top_brands.RespTopBrands
import com.locatocam.app.di.DaggerAppComponent
import com.locatocam.app.network.WebApi
import com.locatocam.app.security.SharedPrefEnc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Retrofit
import javax.inject.Inject

class ViewActivityRepository(val application: Application)  {
    @Inject
    lateinit var retrofitService: Retrofit

    init {
        DaggerAppComponent.create().inject(this)
    }

    suspend fun getViewActivity(reqMyActivity: ReqMyActivity): Flow<RespMyActivity> {
        return flow {
            val res= retrofitService.create(WebApi::class.java).getMyActivity(reqMyActivity)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }
    suspend fun getNotification(reqMyActivity: ReqMyActivity): Flow<RespNotification> {
        return flow {
            val res= retrofitService.create(WebApi::class.java).getNotification(reqMyActivity)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }

    fun getUserID():String{
        return SharedPrefEnc.getPref(application,"user_id")
    }
}