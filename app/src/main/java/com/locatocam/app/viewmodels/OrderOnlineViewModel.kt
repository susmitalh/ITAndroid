package com.locatocam.app.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.locatocam.app.data.requests.ReqOffersForYou
import com.locatocam.app.data.requests.ReqPopularBrands
import com.locatocam.app.data.requests.ReqSelectedBrand
import com.locatocam.app.data.requests.ReqTopBrands
import com.locatocam.app.data.responses.brand_list.BrandList
import com.locatocam.app.network.Resource
import com.locatocam.app.repositories.OrderOnlineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class OrderOnlineViewModel(
    val repository: OrderOnlineRepository
    ):ViewModel() {

    var selectedbrands=MutableLiveData<List<com.locatocam.app.data.responses.selected_brands.Data>>()
    var offersForYouList = MutableStateFlow<Resource<BrandList>>(Resource.loading(null))
    var offersForYou=MutableLiveData<List<com.locatocam.app.data.responses.offers_for_you.Data>>()
    var topBrands=MutableLiveData<List<com.locatocam.app.data.responses.top_brands.Data>>()
    var topPics=MutableLiveData<List<com.locatocam.app.data.responses.top_pics.Data>>()
    var popular_brands=MutableLiveData<List<com.locatocam.app.data.responses.popular_brands.Data>>()
    init {

    }

    fun getSelectedBrands(pref: String, pref1: String) {
        var request= ReqSelectedBrand("49",pref,pref1)
        viewModelScope.launch {
            repository.getSelectedBrands(request)
                .catch {
                }.collect {
                    selectedbrands.value=it.data
                }

        }
    }

    fun getOffers(offerID: String?, pref: String, pref1: String): MutableStateFlow<Resource<BrandList>> {
        var request= ReqOffersForYou(
            latitude = pref,
            longitude = pref1,
            infl_brand = "49",
            infl_company_id = "",
            portal = "",
            keyword = "offer",
            cuisine_ids = "",
            offer_id = offerID,
            brand_ids = ""
        )
        viewModelScope.launch {
            repository.getOffers(request)
                .catch {
                }.collect {
                    offersForYouList.value=Resource.success(it)
                }

        }
        return offersForYouList
    }

    fun getOffersForYou(pref: String, pref1: String) {
        viewModelScope.launch {
            repository.getOffersForYou()
                .catch {
                }.collect {
                    offersForYou.value=it.data
                }

        }
    }

    fun getTopBrands(lat: String, lng: String) {
        Log.e("lat",lat)
        var request=ReqTopBrands("49","",lat,lng,"")
        viewModelScope.launch {
            repository.getTopBrands(request)
                .catch {
                    Log.i("y5ttggg",it.message.toString())
                }.collect {
                    topBrands.value=it.data
                    Log.i("y5ttggg",it.message)
                }

        }
    }

    fun getTopPics(pref: String, pref1: String) {
        Log.i("y5ttggg","thgn666")
        viewModelScope.launch {
            repository.getTopPics()
                .catch {
                    Log.i("y5ttggg",it.message.toString())
                }.collect {
                    topPics.value=it.data
                    Log.i("y5ttggg",it.message)
                }

        }
    }

    fun getPopularBrands(pref: String, pref1: String) {
        Log.i("y5ttggg","thgn666")
        var req=ReqPopularBrands("49","",pref,pref1,"","")
        viewModelScope.launch {
            repository.getPopularBrands(req)
                .catch {
                    Log.i("y5ttggg",it.message.toString())
                }.collect {
                    popular_brands.value=it.data
                    Log.i("y5ttggg",it.message)
                }

        }
    }


}