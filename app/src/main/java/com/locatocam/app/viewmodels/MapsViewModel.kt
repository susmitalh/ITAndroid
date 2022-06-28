package com.locatocam.app.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.locatocam.app.data.requests.ReqAddress
import com.locatocam.app.data.requests.ReqFeed
import com.locatocam.app.data.requests.ReqSaveAddress
import com.locatocam.app.data.responses.address.RespAddress
import com.locatocam.app.data.responses.feed.Data
import com.locatocam.app.repositories.HomeRepository
import com.locatocam.app.repositories.MapRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MapsViewModel(
    val repository: MapRepository
    ):ViewModel() {

    var address=""
    var landmark=""
    var flat_no=""
    var add_save_as=""
    var lat=""
    var lng=""
    var place=""
    var pin_code=""
    var phone="7012967301"
    var save_address_status=MutableLiveData<Boolean>()
    var addressresp = MutableLiveData<RespAddress>()

    init {
       // phone="7012967301" //change later
    }

    fun saveAddress(){
        Log.i("tghbbb","called")

        var request=ReqSaveAddress(
            add_save_as,
            address,
            flat_no,
            landmark,
            lat,
            lng,
            phone,
            pin_code,
            place
        )

        viewModelScope.launch {
            repository.saveAddress(request)
                .catch {
                    val res= ArrayList<Data>()
                    res.add(Data());
                    Log.e("saveAddress",it.message.toString())
                }
                .collect {
                    Log.e("saveAddress",it.status.toString())

                    if (it.status.equals("true")){
                        save_address_status.value=true
                    }else{
                        Log.e("saveAddress",it.message.toString())
                    }
            }
        }
    }

    fun getAddress(phone: String) {
        var request = ReqAddress(phone)
        //repository.getAdress(request)

        viewModelScope.launch {
            repository.getAdress(request)
                .catch {
                    Log.e("address", it.message.toString())
                }
                .collect {
                    try {
                        addressresp.value = it
                    } catch (e: Exception) {
                    }
                }
        }
    }

}