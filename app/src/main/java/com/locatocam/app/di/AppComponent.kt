package com.locatocam.app.di

import com.locatocam.app.MyApp
import com.locatocam.app.di.module.NetworkModule
import com.locatocam.app.reportpost.ReportPostActivity
import com.locatocam.app.repositories.*
import com.locatocam.app.views.home.HomeFragment
import com.locatocam.app.views.home.header.HeaderFragment
import dagger.Component
import javax.inject.Singleton

@Component(modules = [NetworkModule::class])
@Singleton
interface AppComponent {
    fun inject(app: MyApp)
    fun inject(homerepo: HomeRepository)
    fun inject(hr: HeaderRepository)
    fun inject(rollsRepository: RollsRepository)
    fun inject(loginRepository: LoginRepository)
    fun inject(commentsRepository: CommentsRepository)
    fun inject(settingsRepository: SettingsRepository)
    fun inject(mapRepository: MapRepository)
    fun inject(mainRepository: MainRepository)
    fun inject(orderOnlineRepository: OrderOnlineRepository)
    fun inject(orderOnlineRepository: AddProductRepository)
    fun inject(reportPostActivity: ReportPostActivity)
    fun inject(reportPostActivity: FollowersRepository)
    fun inject(reportPostActivity: ViewActivityRepository)
    fun inject(reportPostActivity: PlayPostRepository)
    fun inject(viewMyPostRepository: ViewMyPostRepository)
    fun inject(onlineOrderHelpRepository: OnlineOrderHelpRepository)
}