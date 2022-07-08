package com.locatocam.app.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.locatocam.app.data.requests.*
import com.locatocam.app.data.responses.SearchModal.DataSeach
import com.locatocam.app.data.responses.top_influencers.Data
import com.locatocam.app.data.responses.user_details.RespUserDetails
import com.locatocam.app.data.responses.userblock_reasons.ResAddUserBlock
import com.locatocam.app.data.responses.userblock_reasons.ResBlockUserReason
import com.locatocam.app.repositories.HeaderRepository
import com.locatocam.app.views.home.header.HeaderFragment
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import net.minidev.json.JSONObject


class HeaderViewModel(
    val repository: HeaderRepository
) : ViewModel() {
    var topInfluencer = MutableLiveData<List<Data>>()
    var mostPopularVideos =
        MutableLiveData<List<com.locatocam.app.data.responses.popular_videos.Data>>()
    var rollsAndShortvdos =
        MutableLiveData<List<com.locatocam.app.data.responses.rolls_and_short_videos.Data>>()
    var userDetails = MutableLiveData<RespUserDetails>()
    lateinit var filter: String
    var userBlockReason = MutableLiveData<List<com.locatocam.app.data.responses.userblock_reasons.Data>>()
    var addUserBlock = MutableLiveData<ResAddUserBlock>()


    fun getTopInfluencersV(userid: String, type: String) {
        viewModelScope.launch {
            repository.getTopInfluencersFlow(userid, type).catch {

            }.collect {
                try {
                    topInfluencer.value = it
                    Log.e("TAG", "getTopInfluencersV: " + it?.get(0)?.inf_name)
                } catch (e: Exception) {
                }
            }
        }
    }

    fun getMostPopularVideos() {
        Log.e("TAG", "getMostPopularVideos: " )
        var request = ReqMostPopularVideos(HeaderFragment.infcode, 0, repository.getUserID(), HeaderFragment.userType)
        Log.e("TAG", "getMostPopularVideos: "+HeaderFragment.infcode+","+0 +repository.getUserID()+","+HeaderFragment.userType)
        viewModelScope.launch {
            repository.getTopMostPopularVideos(request).catch {

            }
                .collect {
                    try {
                        mostPopularVideos.value = it
                    } catch (e: Exception) {
                    }
                }
        }
    }

    fun getUserDetails(userid: String) {

        repository.userid=userid
        var request =
            ReqUserDetails(repository.userid.toString(), repository.getUserID().toString())

        //repository.getAdress(request)

        Log.i("tghbbb", repository.userid.toString() + ", " + repository.getUserID().toString())
        viewModelScope.launch {
            repository.getUserDetails(request)
                .catch {
                    Log.e("TAG", "tghbbb: " + it.message.toString())
                }
                .collect {
                    Log.e("TAG", "tghbbb: " + it.data?.name)
                    userDetails.value = it
                }
        }
    }

    fun follow() {
        var request = ReqUserDetails(repository.userid, repository.getUserID())
        //repository.getAdress(request)

        Log.i("tghbbb", repository.userid)
        viewModelScope.launch {
            repository.getUserDetails(request)
                .catch {
                    Log.i("tghbbb", it.message.toString())
                }
                .collect {
                    userDetails.value = it
                }
        }
    }

    fun getRollsAndShortVideos(infCode: String) {
        var userid = repository.getUserID()

        var request = ReqRollsAndShortVideos(userid, HeaderFragment.userType, infCode)
        viewModelScope.launch {
            repository.getRollsAndShortVideos(request).catch {

            }.collect {
                try {
                    rollsAndShortvdos.value = it
                } catch (e: Exception) {
                }
            }
        }
    }

    fun follow(followerid: Int, followtype: String) {
        Log.i("hnm777", followtype)
        var request =
            ReqFollow(followtype, "influencer", followerid, repository.getUserID().toInt())
        viewModelScope.launch {
            repository.follow(request)
                .catch {

                }
                .collect {
                    Log.i("hnm777", it.message.toString())
                }
        }
    }

    fun searchApi(userId: String, dataList: ArrayList<DataSeach>?) {

        val jsonObject = JSONObject()
        jsonObject.put("user_id", userId.toInt())
        viewModelScope.launch {
            repository.search(jsonObject).catch {
                Log.e("TAG", "searchApisearch1: " + it.message)
            }.collect {

                try {
                    Log.e("TAG", "searchApisearch: " + it.data?.get(0)?.name)
                    dataList?.addAll(it.data!!)
                    Log.e("TAG", "searchApigr: " + dataList?.get(0)?.name)
                } catch (e: Exception) {
                }


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
                    .contains(filter.toLowerCase()))
            ) {
                searchDataList.add(t)
            }

        }
        return searchDataList
    }
    fun getblockUserReasons() {
        viewModelScope.launch {
            repository.getUserBlockReason().catch {
            }
                .collect {
                    try {
                        userBlockReason.value = it
                    } catch (e: Exception) {
                    }
                }
        }
    }

    fun addblockUserReasons(request:ReqAddUserBlock) {
        viewModelScope.launch {
            repository.addUserBlock(request).catch {
            }
                .collect {
                    try {
                        addUserBlock.value = it
                    } catch (e: Exception) {
                    }
                }
        }
    }

}