package com.locatocam.app.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "addon")
data class Addon(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val addon_id:Int,
    val master_id:String,
    val item_id:String,
    val item_name:String,
    var quantity:Int,
    val rate:Double,
    val brand:String,
    val tax:Double,
    val discount:Double
)
