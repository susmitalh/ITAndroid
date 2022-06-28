package com.locatocam.app.repositories

import android.content.Context
import com.locatocam.app.data.requests.*
import com.locatocam.app.data.requests.reqUserProfile.ReqBlockedUser
import com.locatocam.app.data.requests.reqUserProfile.ReqViewApproval
import com.locatocam.app.data.requests.viewApproval.ReqApprove
import com.locatocam.app.data.requests.viewApproval.ReqCompanyApprove
import com.locatocam.app.data.requests.viewApproval.ReqCompanyReject
import com.locatocam.app.data.requests.viewApproval.ReqReject
import com.locatocam.app.data.responses.customer_model.Customer
import com.locatocam.app.data.responses.favOrder.ResFavOrder
import com.locatocam.app.data.responses.settings.*
import com.locatocam.app.data.responses.settings.Approved.ApprovedPost
import com.locatocam.app.data.responses.settings.companyApproved.companyApproved
import com.locatocam.app.data.responses.settings.companyPending.CompanyPending
import com.locatocam.app.data.responses.settings.companyRejected.companyRejected
import com.locatocam.app.data.responses.settings.pendingPost.RespViewApproval
import com.locatocam.app.data.responses.settings.rejectedPost.ResRejected
import com.locatocam.app.data.responses.user_model.User
import com.locatocam.app.data.responses.yourOrder.ResYourOrder
import com.locatocam.app.di.DaggerAppComponent
import com.locatocam.app.network.WebApi
import com.locatocam.app.security.SharedPrefEnc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Retrofit
import javax.inject.Inject

class SettingsRepository(val context: Context) {

    @Inject
    lateinit var retrofit: Retrofit


    init {
        DaggerAppComponent.create().inject(this)
    }

    fun getSetttings(reqSettings: ReqSettings): Flow<RespSettings> {
        return flow {
            val res= retrofit.create(WebApi::class.java).settings(reqSettings)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }

    fun getUserSetttingsData(reqSettings: ReqSettings): Flow<User> {
        return flow {
            val res= retrofit.create(WebApi::class.java).settingsData(reqSettings)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }

    fun getCompanySetttingsData(reqSettings: ReqSettings): Flow<com.locatocam.app.data.responses.company.Company> {
        return flow {
            val res= retrofit.create(WebApi::class.java).companySettingsData(reqSettings)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }

    fun getCustomerSettingsData(reqSettings: ReqSettings): Flow<Customer> {
        return flow {
            val res= retrofit.create(WebApi::class.java).customerSettingsData(reqSettings)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }




    fun getUserID():String{
        return SharedPrefEnc.getPref(context,"user_id")
    }

    fun getInfluencerSop(): Flow<InfluencerSop> {
        return flow {
            val res= retrofit.create(WebApi::class.java).getInfluencerSop()
            emit(res)
        }.flowOn(Dispatchers.IO)
    }
    fun getPocSop(): Flow<InfluencerSop> {
        return flow {
            val res= retrofit.create(WebApi::class.java).getPocSop()
            emit(res)
        }.flowOn(Dispatchers.IO)
    }
    fun getViewBlockUserData(reqSettings: ReqSettings): Flow<ViewBlockUser> {
        return flow {
            val res= retrofit.create(WebApi::class.java).getViewBlock(reqSettings)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getBlockedUser(reqBlockedUser: ReqBlockedUser) : Flow<ResBlockedUser>{
        return flow {
            val res = retrofit.create(WebApi::class.java).postBlockedUser(reqBlockedUser)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }
    suspend fun getViewApprovalList(reqViewApproval: ReqViewApproval) : Flow<RespViewApproval> {
        return flow {
            val res = retrofit.create(WebApi::class.java).getViewApproval(reqViewApproval)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }
    suspend fun getViewApprovedList(reqViewApproval: ReqViewApproval) : Flow<ApprovedPost> {
        return flow {
            val res = retrofit.create(WebApi::class.java).getViewApproved(reqViewApproval)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }
    suspend fun getViewRejectedList(reqViewApproval: ReqViewApproval) : Flow<ResRejected> {
        return flow {
            val res = retrofit.create(WebApi::class.java).getViewRejected(reqViewApproval)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getCompanyPendindList(reqViewApproval: ReqViewApproval) : Flow<CompanyPending> {
        return flow {
            val res = retrofit.create(WebApi::class.java).getCompanyPending(reqViewApproval)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }
    suspend fun getCompanyApprovedList(reqViewApproval: ReqViewApproval) : Flow<companyApproved> {
        return flow {
            val res = retrofit.create(WebApi::class.java).getCompanyApproved(reqViewApproval)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }
    suspend fun getCompanyRejectedList(reqViewApproval: ReqViewApproval) : Flow<companyRejected> {
        return flow {
            val res = retrofit.create(WebApi::class.java).getCompanyeRejected(reqViewApproval)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun postReject(reqReject: ReqReject) : Flow<StatusApproved>{
        return flow {
            val res = retrofit.create(WebApi::class.java).postReject(reqReject)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }
    suspend fun postApprove(reqApprove: ReqApprove) : Flow<StatusApproved>{
        return flow {
            val res = retrofit.create(WebApi::class.java).postApprove(reqApprove)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }
    suspend fun repost(reqApprove: ReqApprove) : Flow<StatusApproved>{
        return flow {
            val res = retrofit.create(WebApi::class.java).postRepost(reqApprove)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }


    suspend fun companyReject(reqCompanyReject: ReqCompanyReject) : Flow<StatusApproved>{
        return flow {
            val res = retrofit.create(WebApi::class.java).postCompanyReject(reqCompanyReject)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }
    suspend fun companyApprove(reqCompanyApprove: ReqCompanyApprove) : Flow<StatusApproved>{
        return flow {
            val res = retrofit.create(WebApi::class.java).postCompanyApprove(reqCompanyApprove)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }
    suspend fun companyrepost(reqCompanyApprove: ReqCompanyApprove) : Flow<StatusApproved>{
        return flow {
            val res = retrofit.create(WebApi::class.java).postCompanyRepost(reqCompanyApprove)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getMyPostReelPending(reqViewApproval: ReqViewApproval) : Flow<RespViewApproval> {
        return flow {
            val res = retrofit.create(WebApi::class.java).getMyPostReelPending(reqViewApproval)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }
    suspend fun getMyPostReelApproved(reqViewApproval: ReqViewApproval) : Flow<ApprovedPost> {
        return flow {
            val res = retrofit.create(WebApi::class.java).getMyPostReelApproved(reqViewApproval)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }
    suspend fun getMyPostReelRejected(reqViewApproval: ReqViewApproval) : Flow<ResRejected> {
        return flow {
            val res = retrofit.create(WebApi::class.java).getMyPostReelRejected(reqViewApproval)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }
    suspend fun getYourOrders(reqOrders: ReqOrders) : Flow<ResYourOrder> {
        return flow {
            val res = retrofit.create(WebApi::class.java).getYourOrders(reqOrders)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }
    suspend fun getFavOrders(reqOrders: ReqOrders) : Flow<ResFavOrder> {
        return flow {
            val res = retrofit.create(WebApi::class.java).getFavOrders(reqOrders)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }

}