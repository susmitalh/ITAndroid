package com.locatocam.app.repositories

import android.app.Application
import com.locatocam.app.ModalClass.MyPosts
import com.locatocam.app.ModalClass.PlayPost
import com.locatocam.app.data.requests.ReqFollow
import com.locatocam.app.data.requests.ReqTrash
import com.locatocam.app.data.responses.RespFollow
import com.locatocam.app.data.responses.RespTrash
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

class ViewMyPostRepository(val application: Application) {

    @Inject
    lateinit var retrofit: Retrofit


    init {
        DaggerAppComponent.create().inject(this)
    }

    fun getUserID():String{
        return SharedPrefEnc.getPref(application,"user_id")
    }

    fun MyPost(jsonObject: JSONObject): Flow<MyPosts>{
        return flow {
            val res=retrofit.create(WebApi::class.java).myposts(jsonObject)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }
    fun MyShort(jsonObject: JSONObject): Flow<MyPosts>{
        return flow {
            val res=retrofit.create(WebApi::class.java).myshort(jsonObject)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }
    fun follow(reqFollow: ReqFollow): Flow<RespFollow> {
        return flow {
            val res = retrofit.create(WebApi::class.java).follow(reqFollow)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }
    fun trash(reqTrash: ReqTrash): Flow<RespTrash> {

        return flow {
            val res= retrofit.create(WebApi::class.java).trash(reqTrash)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }
}