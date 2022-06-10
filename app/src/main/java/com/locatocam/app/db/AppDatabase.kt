package com.locatocam.app.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.locatocam.app.db.entity.Addon
import com.locatocam.app.db.entity.Cart
import com.locatocam.app.db.entity.Varient

@Database(entities = [Cart::class,Addon::class,Varient::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): CartDao
    companion object{
        lateinit var INSTANCE:AppDatabase

        fun getDatabase(context: Context): AppDatabase {

               synchronized(AppDatabase::class.java){
                    if (!::INSTANCE.isInitialized){
                        INSTANCE=Room.databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java,
                            "app_database"
                        ).build()
                }

            }
            return INSTANCE
        }
    }
}