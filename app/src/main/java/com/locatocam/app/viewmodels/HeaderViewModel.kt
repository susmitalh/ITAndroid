package com.locatocam.app.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.locatocam.app.data.requests.ReqFollow
import com.locatocam.app.data.requests.ReqMostPopularVideos
import com.locatocam.app.data.requests.ReqRollsAndShortVideos
import com.locatocam.app.data.requests.ReqUserDetails
import com.locatocam.app.data.responses.SearchModal.DataSeach
import com.locatocam.app.data.responses.top_influencers.Data
import com.locatocam.app.data.responses.user_details.RespUserDetails
import com.locatocam.app.repositories.HeaderRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import net.minidev.json.JSONObject


class HeaderViewModel(
    val repository: HeaderRepository
): ViewModel() {
    var topInfluencer= MutableLiveData<List<Data>>()
    var mostPopularVideos= MutableLiveData<List<com.locatocam.app.data.responses.popular_videos.Data>>()
    var rollsAndShortvdos= MutableLiveData<List<com.locatocam.app.data.responses.rolls_and_short_videos.Data>>()
    var userDetails=MutableLiveData<RespUserDetails>()
    lateinit var filter: String

    fun getTopInfluencersV(userid: String,type:String) {
        viewModelScope.launch {
            repository.getTopInfluencersFlow(userid,type).catch {

            }.collect {
                topInfluencer.value=it
                Log.e("TAG", "getTopInfluencersV: "+it?.get(0)?.inf_name )
            }
        }
    }

    fun getMostPopularVideos(infcode:String){
        var request=ReqMostPopularVideos(infcode,0,repository.getUserID(),"influencer")
        viewModelScope.launch {
            repository.getTopMostPopularVideos(request).collect {
                mostPopularVideos.value=it
            }
        }
    }

    fun getUserDetails(){
        var request= ReqUserDetails(repository.userid.toString(),repository.getUserID().toString())

        //repository.getAdress(request)

        Log.i("tghbbb",repository.userid)
        viewModelScope.launch {
            repository.getUserDetails(request)
                .catch {
                    Log.e("TAG", "tghbbb: "+it.message.toString())
                }
                .collect {
                    Log.e("TAG", "tghbbb: "+it.data?.name )
                    userDetails.value=it
                }
        }
    }

    fun follow(){
        var request= ReqUserDetails(repository.userid,repository.getUserID())
        //repository.getAdress(request)

        Log.i("tghbbb",repository.userid)
        viewModelScope.launch {
            repository.getUserDetails(request)
                .catch {
                    Log.i("tghbbb",it.message.toString())
                }
                .collect {
                    userDetails.value=it
                }
        }
    }
    fun getRollsAndShortVideos(infCode: String) {
        var userid=repository.getUserID()

        var request=ReqRollsAndShortVideos(userid,"influencer",infCode)
        viewModelScope.launch {
            repository.getRollsAndShortVideos(request).collect {
                rollsAndShortvdos.value=it
            }
        }
    }

    fun follow(followerid:Int,followtype: String){
        Log.i("hnm777",followtype)
        var request= ReqFollow(followtype,"influencer",followerid,repository.getUserID().toInt())
        viewModelScope.launch {
            repository.follow(request)
                .catch {

                }
                .collect {
                    Log.i("hnm777",it.message.toString())
                }
        }
    }
    fun searchApi(userId: String, dataList: ArrayList<DataSeach>?){

        val jsonObject = JSONObject()
        jsonObject.put("user_id", userId.toInt())
        viewModelScope.launch {
            repository.search(jsonObject).catch {
                Log.e("TAG", "searchApisearch1: "+it.message )
            }.collect {

                Log.e("TAG", "searchApisearch: "+it.data?.get(0)?.name )
                        dataList?.addAll(it.data!!)
                        Log.e("TAG", "searchApigr: "+dataList?.get(0)?.name )


            }
        }
    }
    fun filter(searchDatalist: ArrayList<DataSeach>): ArrayList<DataSeach>? {

        if (filter == null || filter!!.isEmpty()) {
            return searchDatalist
        }
        val searchDataList: ArrayList<DataSeach> = ArrayList()
        for (t in searchDatalist) {
            if ((t.name != null && t.name!!.toLowerCase()
                    .contains(filter.toLowerCase())) || (t.userType != null && t.userType!!.toLowerCase()
                    .contains(filter.toLowerCase())) )
            {
                searchDataList.add(t)
            }

        }
        return searchDataList
    }


}