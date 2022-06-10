package com.locatocam.app.repositories

import android.app.Application
import com.locatocam.app.data.requests.*
import com.locatocam.app.data.responses.RespFollow
import com.locatocam.app.data.responses.brand_list.BrandList
import com.locatocam.app.data.responses.feed.Feed
import com.locatocam.app.data.responses.like.RespLike
import com.locatocam.app.data.responses.offers_for_you.RespOffersForYou
import com.locatocam.app.data.responses.playrolls.Rolls
import com.locatocam.app.data.responses.playrolls.RollsData
import com.locatocam.app.data.responses.popular_brands.RespPopularBrands
import com.locatocam.app.data.responses.resp_products_new.Varients
import com.locatocam.app.data.responses.rolls_and_short_videos.Data
import com.locatocam.app.data.responses.selected_brands.RespSelectedBrand
import com.locatocam.app.data.responses.top_brands.RespTopBrands
import com.locatocam.app.data.responses.top_pics.RespTopPics
import com.locatocam.app.db.AppDatabase
import com.locatocam.app.db.CartDao
import com.locatocam.app.db.entity.Varient
import com.locatocam.app.di.DaggerAppComponent
import com.locatocam.app.network.WebApi
import com.locatocam.app.security.SharedPrefEnc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Call
import retrofit2.Retrofit
import javax.inject.Inject

class OrderOnlineRepository(val application: Application)  {
    @Inject
    lateinit var retrofitService: Retrofit
    lateinit var dao: CartDao
    init {
        DaggerAppComponent.create().inject(this)
        dao= AppDatabase.getDatabase(application).userDao()
    }

    suspend fun getSelectedBrands(reqSelectedBrand: ReqSelectedBrand): Flow<RespSelectedBrand> {
        return flow {
            val res= retrofitService.create(WebApi::class.java).getSelectedBrands(reqSelectedBrand)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getOffers(reqOffersForYou: ReqOffersForYou): Flow<BrandList> {
        return flow {
            val res= retrofitService.create(WebApi::class.java).getBrandList(reqOffersForYou)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }
    suspend fun getOffersForYou(): Flow<RespOffersForYou> {
        return flow {
            val res= retrofitService.create(WebApi::class.java).getOffersForYou()
            emit(res)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getTopBrands(reqTopBrands: ReqTopBrands): Flow<RespTopBrands> {
        return flow {
            val res= retrofitService.create(WebApi::class.java).getTopBrands(reqTopBrands)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getTopPics(): Flow<RespTopPics> {
        return flow {
            val res= retrofitService.create(WebApi::class.java).getTopPicks()
            emit(res)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getPopularBrands(reqPopularBrands: ReqPopularBrands): Flow<RespPopularBrands> {
        return flow {
            val res= retrofitService.create(WebApi::class.java).getPopularBrands(reqPopularBrands)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }


}