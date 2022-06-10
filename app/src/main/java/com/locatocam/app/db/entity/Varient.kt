package com.locatocam.app.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "varient")
data class Varient(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val varient_id:Int,
    val master_id:String,
    val item_id:String,
    val varient_name:String,
    var quantity:Int,
    val rate:Double,
    val brand:String,
    val tax:Double,
    val discount:Double
)
