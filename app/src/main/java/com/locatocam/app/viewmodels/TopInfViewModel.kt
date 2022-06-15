package com.locatocam.app.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.locatocam.app.data.requests.ReqAddress
import com.locatocam.app.data.requests.ReqFeed
import com.locatocam.app.data.responses.address.RespAddress
import com.locatocam.app.data.responses.feed.Data
import com.locatocam.app.repositories.HeaderRepository
import com.locatocam.app.repositories.HomeRepository
import com.locatocam.app.views.home.header.TopInfluencers
import com.locatocam.app.views.search.Locationitem
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TopInfViewModel(
    val repository: HeaderRepository
    ):ViewModel() {
    var topInfluencer= MutableLiveData<List<com.locatocam.app.data.responses.top_influencers.Data>>()
    var tmp=  mutableListOf<com.locatocam.app.data.responses.top_influencers.Data>()


    init {

    }

    fun performSearch(keyword:String){
        if (keyword.equals("")){
            topInfluencer.value=tmp
        }else{
            var tp=  mutableListOf<com.locatocam.app.data.responses.top_influencers.Data>()
            tmp.forEach {
                if (it.inf_name.toString().lowercase().contains(keyword.lowercase()))
                {
                    tp.add(it)
                }
            }
            topInfluencer.value=tp
        }

    }
    fun getTopInfluencersV(userid: String, type: String, topInfluencers: TopInfluencers) {
        topInfluencers.showLoader()
        viewModelScope.launch {
            repository.getTopInfluencersFlow(userid,type).collect {
                try {
                    topInfluencers.hideLoader()
                    topInfluencer.value=it
                    tmp.addAll(it!!)
                } catch (e: Exception) {
                }
            }
        }
    }
}