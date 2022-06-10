package com.locatocam.app.di.module

import com.locatocam.app.BuildConfig
import com.locatocam.app.network.WebApi
import dagger.Module
import dagger.Provides
import dagger.hilt.migration.DisableInstallInCheck
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import com.google.gson.GsonBuilder


@Module
@DisableInstallInCheck
//@InstallIn(SingletonComponent::class)
class NetworkModule {
    companion object{

            var retrofit: Retrofit? = null
            fun getClient(): Retrofit? {

                val interceptor = HttpLoggingInterceptor()
                interceptor.level = HttpLoggingInterceptor.Level.BODY
                val httpClient = OkHttpClient.Builder()
                httpClient.addInterceptor(interceptor) // <-- this is the important line!
                retrofit = Retrofit.Builder()
                    .baseUrl("https://loca-toca.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build()
                return retrofit
            }

    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://loca-toca.com/")
            .client(okHttpClient)
            .build()
    }


    @Singleton
    @Provides
    fun provideApi(retrofit: Retrofit): WebApi {
        return retrofit.create(WebApi::class.java)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(
        interceptors: ArrayList<Interceptor>
    ): OkHttpClient {
        val clientBuilder = OkHttpClient.Builder()
            .followRedirects(false)
        interceptors.forEach {
            clientBuilder.addInterceptor(it)
        }
        return clientBuilder.build()
    }


    @Singleton
    @Provides
    fun provideInterceptors(): ArrayList<Interceptor> {
        val interceptors = arrayListOf<Interceptor>()
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        interceptors.add(loggingInterceptor)
        return interceptors
    }
}