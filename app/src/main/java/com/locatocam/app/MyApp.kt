package com.locatocam.app;

import android.app.Application
import android.content.Context
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.locatocam.app.di.AppComponent
import com.locatocam.app.di.DaggerAppComponent
import dagger.hilt.android.HiltAndroidApp
import im.ene.toro.exoplayer.Config
import im.ene.toro.exoplayer.MediaSourceBuilder
import java.io.File

@HiltAndroidApp
class MyApp : Application() {

        companion object {

                lateinit var instance: MyApp

                private set

                var simpleCache: SimpleCache? = null

                var context: Context? = null

        }

        lateinit var appComponent: AppComponent
        private set


        val config: Config by lazy {
                val cache = SimpleCache(
                        File(filesDir.path + "/toro_cache"),
                        LeastRecentlyUsedCacheEvictor(100 * 1024 * 1024)
                )
                Config.Builder(this)
                        .setMediaSourceBuilder(MediaSourceBuilder.LOOPING)
                        .setCache(cache)
                        .build()
        }

        override fun onCreate() {
        super.onCreate()
        instance = this
        initComponent()

                context = this

                val leastRecentlyUsedCacheEvictor = LeastRecentlyUsedCacheEvictor(90 * 1024 * 1024)
                val databaseProvider: DatabaseProvider = ExoDatabaseProvider(this)

                if (simpleCache == null) {
                        simpleCache = SimpleCache(cacheDir, leastRecentlyUsedCacheEvictor, databaseProvider)
                }
        }

        private fun initComponent() {
                appComponent = DaggerAppComponent.builder()
                .build()
                appComponent.inject(this)
        }


}
