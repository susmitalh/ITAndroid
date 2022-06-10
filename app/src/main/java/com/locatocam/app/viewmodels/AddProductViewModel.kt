package com.locatocam.app.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.locatocam.app.data.requests.ReqItems
import com.locatocam.app.data.responses.resp_products_new.Data
import com.locatocam.app.data.responses.resp_products_new.Item
import com.locatocam.app.data.responses.resp_products_new.ItemX
import com.locatocam.app.data.responses.resp_products_new.ItemXX
import com.locatocam.app.db.entity.Addon
import com.locatocam.app.db.entity.Cart
import com.locatocam.app.db.entity.Varient
import com.locatocam.app.network.Resource
import com.locatocam.app.repositories.AddProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AddProductViewModel(
    val repository: AddProductRepository
    ):ViewModel() {

    val selectedbrands = MutableStateFlow<Resource<List<com.locatocam.app.data.responses.selected_brands.Data>>>(Resource.loading(null))
    val items = MutableStateFlow<Resource<Data>>(Resource.loading(null))

    val cart = MutableStateFlow<Resource<List<Cart>>>(Resource.loading(null))
    val categories = MutableStateFlow<Resource<List<String>>>(Resource.loading(null))
    val varients = MutableStateFlow<Resource<List<Varient>>>(Resource.loading(null))
    val varientsld = MutableLiveData<List<Varient>>()
    val addons = MutableLiveData<List<Addon>>()
    var showItemPopup = MutableLiveData<Boolean>()
    val varientAddedPosition=MutableLiveData<Int>()
    val brand_id="267"
    lateinit var varient_selected: ItemXX
    lateinit var addon_selected: ItemX

    var addedQty=1

    init {
        getCart()

    }
    fun getCart(){
        Log.i("hnjjjj","started")

        viewModelScope.launch {
            repository.getCart().catch {e->
                Log.i("hnjjjj",e.toString())
                cart.value = (Resource.error(e.toString(), null))
            }.collect {
                cart.value=(Resource.success(it))
            }
        }
    }
    fun getSelectedBrands(pref: String, pref1: String) {
        var request= ReqItems("","12.9166025","77.6101018","","267")
        viewModelScope.launch {
            repository.getSelectedBrands(request)
                .catch {e->
                    selectedbrands.value = (Resource.error(e.toString(), null))
                }.collect {
                    selectedbrands.value=(Resource.success(it.data))
                }
        }
    }

    fun getProducts(storeID : String,pref: String, pref1: String) {
        var request= ReqItems("",pref,pref1,"",storeID)
        viewModelScope.launch {
            repository.getproducts(request)
                .catch {e->
                    Log.i("hnjjjj",e.toString())
                    items.value = (Resource.error(e.toString(), null))
                }.collect {
                    items.value=(Resource.success(it.data))
                    var categoriesList= mutableListOf<String>()
                    it.data.menu.forEach {
                        categoriesList.add(it.category_name)
                    }

                    runBlocking {
                        Log.i("kjjj","inloop")
                        categories.value=Resource.success(categoriesList.toList())
                    }
                    Log.i("kjjj",categoriesList.size.toString())

                }

        }
    }

    fun addItem(item: Item){

        viewModelScope.launch {
            Log.e("addItem",item.name)
            repository.addItem(Cart(item.id,item.name,item.quantity,item.price.toDouble(),brand_id,5.0,item.discount.toDouble())).collect {  }
        }

    }

    fun deleteItem(item: Item){
         viewModelScope.launch {
             repository.deleteItem(Cart(item.id,item.name,item.quantity,item.price.toDouble(),brand_id,5.0,item.discount.toDouble())).collect {  }
         }
    }

    fun updateItem(item: Item){
        Log.e("updateItem",item.name)
        viewModelScope.launch {
            repository.updateItem(Cart(item.id,item.name,item.quantity,item.price.toDouble(),brand_id,5.0,item.discount.toDouble())).collect {  }
        }
    }

    fun addVarients(item: Item){

        var varient= Varient(0,varient_selected.id.toInt(), item.id ,item.id,varient_selected.name,addedQty,varient_selected.price.toDouble(),"",5.0,item.discount.toDouble())

        viewModelScope.launch {
            repository.checkvarientCount(varient_selected.id,item.id)
                .catch {e->
                    Log.i("hbnn",e.message.toString())
                }.collect {
                    Log.i("hbnn","in1")
                    if (it>0){
                        Log.i("hbnn","in2")
                        varient.quantity=it+addedQty
                        Log.i("hbnn",varient.varient_id.toString()+"--"+varient.item_id)

                        repository.updateVarient(varient).catch { e->
                            Log.i("hbnn",e.message.toString())
                        }.collect()
                    }else{
                        Log.i("hbnn","in3")
                        varient.quantity=addedQty
                        repository.insertVarient(varient,item,brand_id).catch { e->
                            Log.i("hbnn",e.message.toString())
                        }.collect()
                    }

                }
        }
    }

    fun getAllVarients(product_id:String){
        viewModelScope.launch {
            repository.getAllVarient(product_id)
                .catch {e->
                    Log.i("hnjjjj",e.toString())
                    varients.value = (Resource.error(e.toString(), null))
                }.collect {
                    Log.i("hnjjjj",it.toString())

                    //varients.value=(Resource.success(it))
                    varientsld.value=it
                }

        }
    }
    fun updatevarientCount(varient:Varient){

//        varient_selected = ItemX(
//            id = varient.id,
//            name = varient.name,
//            price
//        )
        viewModelScope.launch {
            repository.updateVarient(varient)
                .catch {e->
                    Log.i("hnjjjj",e.toString())
                }.collect {
                }

        }
    }

    fun addAddon(item: Item){
        var addon= Addon(0,addon_selected.id.toInt(), item.id ,item.id,addon_selected.name,addedQty,addon_selected.price.toDouble(),"",5.0,item.discount.toDouble())

        viewModelScope.launch {
            repository.checkvarientCount(addon_selected.id,item.id)
                .catch {e->
                    Log.i("hbnn",e.message.toString())
                }.collect {
                    Log.i("hbnn","in1")
                    if (it>0){
                        Log.i("hbnn","in2")
                        addon.quantity=it+addedQty
                       // Log.i("hbnn",varient.varient_id.toString()+"--"+varient.item_id)

                        repository.updateAddon(addon).catch { e->
                            Log.i("hbnn",e.message.toString())
                        }.collect()
                    }else{
                        Log.i("hbnn","in3")
                        addon.quantity=addedQty
                        repository.insertAddon(addon,item,brand_id).catch { e->
                            Log.i("hbnn",e.message.toString())
                        }.collect()
                    }

                }
        }
    }
    fun getAllAddons(product_id:String){
        viewModelScope.launch {
            repository.getAllAddon(product_id)
                .catch {e->
                    Log.i("hnjjjj",e.toString())
                    //addons.value = (Resource.error(e.toString(), null))
                }.collect {
                    Log.i("hnjjjj",it.toString())

                    //varients.value=(Resource.success(it))
                    addons.value=it
                }

        }
    }
    fun updateAddonCount(varient:Varient){
        viewModelScope.launch {
            repository.updateVarient(varient)
                .catch {e->
                    Log.i("hnjjjj",e.toString())
                }.collect {
                }

        }
    }
}