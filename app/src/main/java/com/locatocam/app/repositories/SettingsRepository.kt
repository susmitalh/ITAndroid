package com.locatocam.app.repositories

import android.content.Context
import com.locatocam.app.data.requests.*
import com.locatocam.app.data.responses.customer_model.Customer
import com.locatocam.app.data.responses.settings.RespSettings
import com.locatocam.app.data.responses.user_model.User
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
}