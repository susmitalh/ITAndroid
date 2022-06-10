package com.locatocam.app.db

import androidx.room.*
import androidx.room.OnConflictStrategy.IGNORE
import com.locatocam.app.db.entity.Addon
import com.locatocam.app.db.entity.Cart
import com.locatocam.app.db.entity.Varient
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT  C.item_id,C.item_name ,\n" +
            "case when C.hasVatient==1 then sum(V.quantity) else C.quantity end  quantity, \n" +
            "case when C.hasVatient==1 then avg(V.rate)  else C.rate end  rate, \n" +
            "C.brand,C.tax,C.discount,C.hasAddon,C.hasVatient\n" +
            " FROM cart C left join varient V on V.master_id=C.item_id group by V.master_id")
    fun getAll(): Flow<List<Cart>>

    @Insert(onConflict = IGNORE)
    fun insert(vararg users: Cart)

    @Delete
    fun delete(user: Cart)

    @Update
    fun update(user: Cart)

    @Insert
    fun insertAddon(addon: Addon)

    @Delete
    fun deleteAddon(addon: Addon)

    @Update
    fun updateAddon(addon: Addon)

    @Insert
    fun insertVarient(varient: Varient)

    @Delete
    fun deleteVarient(varient: Varient)

    @Update
    fun updateVarient(varient: Varient)

    @Query("SELECT  C.item_id,C.item_name , case when C.hasVatient==1 then sum(V.quantity) else C.quantity end  quantity, case when C.hasVatient==1 then avg(V.rate)  else C.rate end  rate, C.brand,C.tax,C.discount,C.hasAddon,C.hasVatient  FROM cart C left join varient V on V.master_id=C.item_id and C.brand=:brand group by V.master_id")
    fun getAllBrandWise(brand:String): List<Cart>

    @Query("SELECT quantity FROM varient where varient_id=:varient_id and item_id=:item_id")
    fun checkvarientCount(varient_id:Int,item_id:String):Int

    @Query("UPDATE varient set quantity=:quantity where varient_id=:varient_id and item_id=:item_id")
    fun updatevarientCountWith(varient_id:Int,item_id:String,quantity:Int):Unit

    @Query("select * from varient  where   item_id=:item_id")
    fun getAllVarients(item_id:String):Flow<List<Varient>>


    @Query("SELECT quantity FROM addon where addon_id=:addon_id and item_id=:item_id")
    fun checkaddonCount(addon_id:Int,item_id:String):Int

    @Query("UPDATE addon set quantity=:quantity where addon_id=:addon_id and item_id=:item_id")
    fun updateaddonCountWith(addon_id: Int,item_id:String,quantity:Int):Unit

    @Query("select * from addon  where   item_id=:item_id")
    fun getAllAddons(item_id:String):Flow<List<Addon>>

}