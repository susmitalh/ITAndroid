package com.locatocam.app.repositories

import android.app.Application
import android.util.Log
import com.locatocam.app.ModalClass.AddView
import com.locatocam.app.data.requests.*
import com.locatocam.app.data.requests.viewApproval.ReqDeleteAddress
import com.locatocam.app.data.responses.RespCounts
import com.locatocam.app.data.responses.RespTrash
import com.locatocam.app.data.responses.address.RespAddress
import com.locatocam.app.data.responses.feed.Data
import com.locatocam.app.data.responses.like.RespLike
import com.locatocam.app.data.responses.saveaddress.RespSaveAddress
import com.locatocam.app.di.DaggerAppComponent
import com.locatocam.app.network.WebApi
import com.locatocam.app.security.SharedPrefEnc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Retrofit
import javax.inject.Inject

class HomeRepository constructor(val application: Application,val other_user_id:String) {

    @Inject
    lateinit var retrofitService: Retrofit


    init {
        DaggerAppComponent.create().inject(this)
    }
    fun getAllFeeds(reqfeed: ReqFeed):Flow<List<Data>>{

        return flow {
            val res= retrofitService.create(WebApi::class.java).getPost(reqfeed)
            val rest= ArrayList<Data>()
            rest.addAll(res.data!!)
            rest.add(0,Data());
            emit(rest)
        }.flowOn(Dispatchers.IO)
    }
    fun getAdress(reqAddress: ReqAddress):Flow<RespAddress>{
        Log.e("reqAddress", reqAddress.toString())

        return flow {
            val res= retrofitService.create(WebApi::class.java).getAddress(reqAddress)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }

    fun like(reqlike: ReqLike): Flow<RespLike> {

        return flow {
            val res= retrofitService.create(WebApi::class.java).like(reqlike)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }

    fun trash(reqTrash:ReqTrash): Flow<RespTrash> {

        return flow {
            val res= retrofitService.create(WebApi::class.java).trash(reqTrash)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }
    fun deleteAddress(deleteAdd:ReqDeleteAddress): Flow<RespSaveAddress> {

        return flow {
            val res= retrofitService.create(WebApi::class.java).deleteAdd(deleteAdd)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }

    fun getUserID():String{

        return SharedPrefEnc.getPref(application,"user_id")
    }
    fun getLoginType():String{
        return SharedPrefEnc.getPref(application,"login_type")
    }

    fun getApprovalCounts(reqGetCounts:ReqGetCounts): Flow<RespCounts> {

        return flow {
            val res= retrofitService.create(WebApi::class.java).get_counts(reqGetCounts)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }
}