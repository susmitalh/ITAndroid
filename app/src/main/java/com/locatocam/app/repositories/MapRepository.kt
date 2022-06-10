package com.locatocam.app.repositories

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.locatocam.app.data.requests.ReqAddress
import com.locatocam.app.data.requests.ReqFeed
import com.locatocam.app.data.requests.ReqMostPopularVideos
import com.locatocam.app.data.requests.ReqSaveAddress
import com.locatocam.app.data.responses.address.RespAddress
import com.locatocam.app.data.responses.feed.Data
import com.locatocam.app.data.responses.feed.Feed
import com.locatocam.app.data.responses.saveaddress.RespSaveAddress
import com.locatocam.app.di.DaggerAppComponent
import com.locatocam.app.network.WebApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

class MapRepository constructor(val application: Application) {

    @Inject
    lateinit var retrofit: Retrofit

    init {
        DaggerAppComponent.create().inject(this)
    }
    fun saveAddress(saveAddress: ReqSaveAddress): Flow<RespSaveAddress> {
        Log.e("saveAddress",saveAddress.toString())
        return flow {
            val res= retrofit.create(WebApi::class.java).savetAddress(saveAddress)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }
   /* fun getAdress(reqAddress: ReqAddress):Flow<RespAddress>{
        return flow {
            val res= retrofitService.create(WebApi::class.java).getAddress(reqAddress)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }*/
}