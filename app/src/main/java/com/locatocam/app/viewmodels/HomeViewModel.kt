package com.locatocam.app.viewmodels

import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.provider.FontsContractCompat.Columns.RESULT_CODE
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.locatocam.app.data.requests.*
import com.locatocam.app.data.responses.RespCounts
import com.locatocam.app.data.responses.RespTrash
import com.locatocam.app.data.responses.address.RespAddress
import com.locatocam.app.data.responses.feed.Data
import com.locatocam.app.repositories.HomeRepository
import com.locatocam.app.views.MainActivity
import com.locatocam.app.views.comments.CommentsActivity.Companion.activity
import com.locatocam.app.views.home.HomeFragment
import com.locatocam.app.views.home.HomeFragment.Companion.binding
import com.locatocam.app.views.home.test.PostCountData
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import net.minidev.json.JSONObject


class HomeViewModel(
    val repository: HomeRepository
) : ViewModel() {


    var feed_items = MutableLiveData<List<Data>>()
    var addressresp = MutableLiveData<RespAddress>()
    var approvalCounts = MutableLiveData<RespCounts>()
    var trash = MutableLiveData<RespTrash>()

    var offset: Int
    var lastid: Int
    var lastposition = 0
    var loading = true
    var user_id = ""

    var user_phone = ""
    var _isheader_added = false

    init {
        offset = 0
        lastid = 0
    }

    fun getAllFeeds(infcode: String, lat: Double, lng: Double) {
        Log.e("TAG", "getAllFeedsdd : " + lat + "  " + lng + " " + lastid.toString()+","+offset)
        Log.e("paggination", "getAllFeeds: " )
        loading = true
        Log.i("rtgbbb", offset.toString())
        Log.i("rtgbbb", lastid.toString())
        Log.e("paggination", "getAllhhFeeds: " + lat + " " + lng)
        var request = ReqFeed(
            "inside",
            infcode,
            lat,
            lng,
            offset.toString(),
            "influencer",
            repository.getUserID().toInt(),
            lastid.toString()
        )
        Log.e("paggination", "getAllFeedsss: "+"inside"+","+infcode+","+lat+","+lng+","+ offset+","+"influencer"+","+repository.getUserID().toInt()+","+lastid)
        repository.getAllFeeds(request)

        viewModelScope.launch {

            repository.getAllFeeds(request)
                .catch {

                    try {
                        val res = ArrayList<Data>()
                        res.add(Data());
                        feed_items.value = res
                    } catch (e: Exception) {
                        Log.e("paggination", "getAllFeeds: catch catch" )
                    }

                    Log.e("paggination", it.message.toString())
                }
                .collect {
                    try {
                        Log.e("paggination", "getAllFeeds: collect" )
                        feed_items.value = it
                        offset++

                        lastid = getLastID(it).toInt()
                    } catch (e: Exception) {
                        Log.e("paggination", "getAllFeeds: catch" )
                    }
                }
        }
    }

    fun getLastID(list: List<Data>): String {
        var ret = ""
        for (i in list.size - 1 downTo 0) {
            ret = list.get(i).post_id.toString()
            if (!ret.equals("")) {
                break
            }
        }
        return ret
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

    fun like(process: String, postid: Int) {
        var request = ReqLike(process, "post", postid, repository.getUserID().toInt())
        viewModelScope.launch {
            repository.like(request)
                .catch {

                }
                .collect {
                    Log.i("hnm777", it.message.toString())
                }
        }
    }

    fun getApprovalCounts(lat: String, lng: String) {
        //var request= ReqLike(process,"post",postid,repository.getUserID().toInt())
        Log.e("TAG", "getApprovalCounts: "+lat+","+lng )
        var request = ReqGetCounts(lat.toString(), lng.toString(), repository.getUserID())

        viewModelScope.launch {
            repository.getApprovalCounts(request)
                .catch {

                }
                .collect {
                    try {
                        Log.i("hnm777", it.message.toString())

                        Log.e("TAG", "getApprovalCountsget: " + it.data.message_count)
                        approvalCounts.value = it
                    } catch (e: Exception) {
                    }
                }
        }
    }

    fun trash(postid: Int) {
        var request = ReqTrash(postid, repository.getUserID().toInt())
        viewModelScope.launch {
            repository.trash(request)
                .catch {

                }
                .collect {
                    Log.i("hnm777", it.message.toString())
                    trash.value = it
                }
        }
    }
}