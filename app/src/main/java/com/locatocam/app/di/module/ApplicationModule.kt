package com.locatocam.app.di.module

import android.content.Context
import android.content.SharedPreferences
import com.locatocam.app.network.ApprovedDataMapper
import com.locatocam.app.network.DocumentMapper
import com.locatocam.app.network.NetworkMapper
import com.locatocam.app.network.UserDetailsMapper
import com.locatocam.app.repositories.MainRepository
import com.locatocam.app.repositories.SettingsRepository
import com.locatocam.app.security.Decode
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {


    @Singleton
    @Provides
    fun providesNetworkMapper() : NetworkMapper{
        return NetworkMapper()
    }

    @Singleton
    @Provides
    fun providesUserDetailMapper() : UserDetailsMapper{
        return UserDetailsMapper()
    }



    @Singleton
    @Provides
    fun providesApprovedDataMapper() : ApprovedDataMapper{
        return ApprovedDataMapper()
    }

    @Singleton
    @Provides
    fun providesDocumentMapper() : DocumentMapper{
        return DocumentMapper()
    }

    @Singleton
    @Provides
    fun provideMainRepository(networkMapper: NetworkMapper,approvedDataMapper: ApprovedDataMapper) : MainRepository {

        return MainRepository(networkMapper,approvedDataMapper)

    }

    @Singleton
    @Provides
    fun provideSettingsRepository(@ApplicationContext context: Context) : SettingsRepository {

        return SettingsRepository(context)

    }



}