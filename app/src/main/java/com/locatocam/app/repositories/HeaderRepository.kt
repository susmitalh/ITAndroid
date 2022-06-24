package com.locatocam.app.repositories

import android.app.Application
import com.locatocam.app.data.requests.*
import com.locatocam.app.data.responses.RespFollow
import com.locatocam.app.data.responses.SearchModal.RespSearch
import com.locatocam.app.data.responses.top_influencers.Data
import com.locatocam.app.data.responses.user_details.RespUserDetails
import com.locatocam.app.di.DaggerAppComponent
import com.locatocam.app.network.WebApi
import com.locatocam.app.security.SharedPrefEnc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import net.minidev.json.JSONObject
import retrofit2.*
import javax.inject.Inject

class HeaderRepository(var userid: String, val application: Application) {
    @Inject
    lateinit var retrofitService: Retrofit

    init {
        DaggerAppComponent.create().inject(this)
    }

    suspend fun getTopInfluencersFlow(userId: String,type:String): Flow<List<Data>?> {
        return flow {
            val comment = retrofitService.create(WebApi::class.java)
                .getTopInfluencerfl(ReqTopInfluencer("", "", type, userId))
            emit(comment.data)
        }.flowOn(Dispatchers.IO)
    }


    fun getTopMostPopularVideos(reqMostPopularVideos: ReqMostPopularVideos): Flow<List<com.locatocam.app.data.responses.popular_videos.Data>?> {
        return flow {
            val res = retrofitService.create(WebApi::class.java)
                .getMostPopularVideos(reqMostPopularVideos)
            emit(res.data)
        }.flowOn(Dispatchers.IO)
    }

    fun getRollsAndShortVideos(reqRollsAndShortVideos: ReqRollsAndShortVideos): Flow<List<com.locatocam.app.data.responses.rolls_and_short_videos.Data>?> {
        return flow {
            val res = retrofitService.create(WebApi::class.java)
                .getRollsAndShortVideos(reqRollsAndShortVideos)
            emit(res.data)
        }.flowOn(Dispatchers.IO)

    }

    fun getUserDetails(reqUserDetails: ReqUserDetails): Flow<RespUserDetails> {
        return flow {
            val res = retrofitService.create(WebApi::class.java).userDetails(reqUserDetails)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }

    fun follow(reqFollow: ReqFollow): Flow<RespFollow> {
        return flow {
            val res = retrofitService.create(WebApi::class.java).follow(reqFollow)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }

    fun getUserID(): String {
        return SharedPrefEnc.getPref(application, "user_id")
    }

    fun search(jsonObject: JSONObject): Flow<RespSearch> {
        return flow {
            val res = retrofitService.create(WebApi::class.java).search(jsonObject)
            emit(res)
        }.flowOn(Dispatchers.IO)

    }
}