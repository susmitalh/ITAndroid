package com.locatocam.app.repositories

import android.app.Application
import com.locatocam.app.data.requests.*
import com.locatocam.app.data.responses.resp_products_new.Item
import com.locatocam.app.data.responses.resp_products_new.RespProductsNew
import com.locatocam.app.data.responses.selected_brands.RespSelectedBrand
import com.locatocam.app.db.AppDatabase
import com.locatocam.app.db.CartDao
import com.locatocam.app.db.entity.Addon
import com.locatocam.app.db.entity.Cart
import com.locatocam.app.db.entity.Varient
import com.locatocam.app.di.DaggerAppComponent
import com.locatocam.app.network.WebApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Retrofit
import javax.inject.Inject

class AddProductRepository(val application: Application)  {
    @Inject
    lateinit var retrofitService: Retrofit

    lateinit var dao: CartDao
    init {
        DaggerAppComponent.create().inject(this)
        dao= AppDatabase.getDatabase(application).userDao()
    }

    suspend fun getSelectedBrands(reqItems: ReqItems): Flow<RespSelectedBrand> {
        return flow {
            val res= retrofitService.create(WebApi::class.java).getMenuBrands(reqItems)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getproducts(reqItems: ReqItems): Flow<RespProductsNew> {
        return flow {
            val res= retrofitService.create(WebApi::class.java).getPoducts(reqItems)
            val cart_items=dao.getAllBrandWise(reqItems.store_id)
            res.data.menu.apply {
                this.forEach { menu ->
                    menu.items.forEach{ item->
                        cart_items.forEach { cart->
                            if (cart.item_id.equals(item.id)){
                                item.quantity=cart.quantity
                            }
                        }
                    }
                }
            }
            emit(res)
        }.flowOn(Dispatchers.IO)
    }

    fun getCart()=dao.getAll()

    suspend fun addItem(item: Cart): Flow<Unit> {
        return flow {
            emit(dao.insert(item))
        }.flowOn(Dispatchers.IO)

    }

    suspend fun deleteItem(item: Cart): Flow<Unit> {
        return flow {
            emit(dao.delete(item))
        }.flowOn(Dispatchers.IO)

    }

    suspend fun updateItem(item: Cart): Flow<Unit> {
        return flow {
            emit(dao.update(item))
        }.flowOn(Dispatchers.IO)

    }

    suspend fun checkvarientCount(varient_id:String,item_id:String): Flow<Int> {
        return flow {
            emit(dao.checkvarientCount(varient_id.toInt(),item_id))
        }.flowOn(Dispatchers.IO)

    }

    suspend fun insertVarient(varient: Varient,item:Item,brand_id:String): Flow<Unit> {
        return flow {
            dao.insert(Cart(item.id,item.name,0,0.0,brand_id,5.0,item.discount.toDouble(),hasVatient = true))
            emit(dao.insertVarient(varient))
        }.flowOn(Dispatchers.IO)
    }


    suspend fun updateVarient(varient: Varient): Flow<Unit> {
        return flow {
            emit(dao.updatevarientCountWith(varient.varient_id.toInt(),varient.master_id,varient.quantity))
        }.flowOn(Dispatchers.IO)

    }

    suspend fun getAllVarient(item_id: String)=dao.getAllVarients(item_id)


    //varient
    suspend fun checkaddonCount(addon_id:String,item_id:String): Flow<Int> {
        return flow {
            emit(dao.checkaddonCount(addon_id.toInt(),item_id))
        }.flowOn(Dispatchers.IO)

    }

    suspend fun insertAddon(addon: Addon,item:Item,brand_id:String): Flow<Unit> {
        return flow {
            dao.insert(Cart(item.id,item.name,0,0.0,brand_id,5.0,item.discount.toDouble(),hasVatient = true))
            emit(dao.insertAddon(addon))
        }.flowOn(Dispatchers.IO)
    }


    suspend fun updateAddon(addon: Addon): Flow<Unit> {
        return flow {
            emit(dao.updateaddonCountWith(addon.addon_id.toInt(),addon.master_id,addon.quantity))
        }.flowOn(Dispatchers.IO)

    }

    suspend fun getAllAddon(item_id: String)=dao.getAllAddons(item_id)
}