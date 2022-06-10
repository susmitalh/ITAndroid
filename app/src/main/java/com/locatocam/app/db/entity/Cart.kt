package com.locatocam.app.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class Cart(
    @PrimaryKey
    val item_id:String,
    val item_name:String,
    val quantity:Int,
    val rate:Double,
    val brand:String,
    val tax:Double,
    val discount:Double,
    val hasAddon:Boolean=false,
    val hasVatient:Boolean=false
)
