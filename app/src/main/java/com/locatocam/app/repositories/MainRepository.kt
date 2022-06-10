package com.locatocam.app.repositories

import android.util.Log
import com.locatocam.app.data.ApprovedPosts
import com.locatocam.app.data.PendingPost
import com.locatocam.app.data.requests.ReqCity
import com.locatocam.app.data.requests.ReqPostApproval
import com.locatocam.app.data.requests.ReqSendOtp
import com.locatocam.app.data.requests.reqUserProfile.ReqProfileData
import com.locatocam.app.data.responses.*
import com.locatocam.app.di.DaggerAppComponent
import com.locatocam.app.network.ApprovedDataMapper
import com.locatocam.app.network.NetworkMapper
import com.locatocam.app.network.WebApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import javax.inject.Inject


class MainRepository ( val networkMapper: NetworkMapper,val approvedDataMapper: ApprovedDataMapper){

    @Inject
    lateinit var retrofitService: Retrofit

    init {
        DaggerAppComponent.create().inject(this)
    }

    suspend fun getBrandPending(reqfeed: ReqPostApproval): Flow<List<PendingPost>> {


        return flow {

            val res = retrofitService.create(WebApi::class.java).getBrandPending(reqfeed)

            val data = res.data

            Log.e("data",data.pending.toString())

            var networkData = data.details

            if (networkData.isNullOrEmpty()){
                networkData = emptyList()
            }

            val pendingPost = networkMapper.mapFromEntityList(networkData)

            emit(pendingPost)
        }.flowOn(Dispatchers.IO)

    }


    suspend fun getApprovedApprovals(reqfeed: ReqPostApproval): Flow<List<ApprovedPosts>> {


        return flow{
            val res = retrofitService.create(WebApi::class.java).getApprovedApprovals(reqfeed)


            val data = res.data


            var networkData = data.details

            if (networkData.isNullOrEmpty()){
                networkData = emptyList()
            }

            val pendingPost = approvedDataMapper.mapFromEntityList(networkData)

            emit(pendingPost)
        }.flowOn(Dispatchers.IO)

    }

    suspend fun sendOtp(reqSendOtp: ReqSendOtp) : Flow<ResOtp>{

        return flow {
            val res = retrofitService.create(WebApi::class.java).sendOtp(reqSendOtp)


            emit(res)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun editProfile(reqEditProfile: ReqProfileData) : Flow<ResEditProfile>{
        return flow {
            val res = retrofitService.create(WebApi::class.java).editProfile(reqEditProfile)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getStates() : Flow<ResState>{
        return flow {
            val res = retrofitService.create(WebApi::class.java).getStates()
            Log.e("res",res.toString())
            emit(res)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getCities(req : ReqCity) : Flow<ResCity>{
        return flow {
            val res = retrofitService.create(WebApi::class.java).getCities(req)
            Log.e("getCities res",res.toString())
            emit(res)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun uploadDocument(multipartBody: MultipartBody): Flow<ResDocUpload>{
        return flow {

            val res = retrofitService.create(WebApi::class.java).uploadDocument(multipartBody)
            Log.e("uploadDocument",res.toString())
            emit(res)
        }.flowOn(Dispatchers.IO)
    }
}