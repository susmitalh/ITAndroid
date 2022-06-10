package com.locatocam.app.viewmodels

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.locatocam.app.ModalClass.AddShare
import com.locatocam.app.data.requests.*
import com.locatocam.app.data.responses.playrolls.RollsData
import com.locatocam.app.di.module.NetworkModule
import com.locatocam.app.network.WebApi
import com.locatocam.app.repositories.HeaderRepository
import com.locatocam.app.repositories.HomeRepository
import com.locatocam.app.repositories.RollsRepository
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.views.PlayPost.PlayPost
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import net.minidev.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RollsViewModel(
    val repository: RollsRepository
    ):ViewModel() {

    var roll_items=MutableLiveData<List<RollsData>>()
    var apiInterface: WebApi? = null

    var offset:Int
    var lastid:Int
    var lastposition=0
    var playfirst=0
    var influencer_code=""
    init {
        offset=0
        lastid=0
    }

    fun getRolls(){
        Log.i("offn6yymm",playfirst.toString())
        var request=ReqPlayRolls(repository.getUserID(),lastid,offset,influencer_code)
        repository.getRolls(request)

        viewModelScope.launch {
            repository.getRolls(request)
                .catch {

                }
                .collect {
                roll_items.value= it.data
                offset++
                lastid= it.data?.get(it.data.size-1)!!.id!!.toInt()
            }
        }
    }

    fun like(process: String, postid: Int, playPost: PlayPost){
        var request=ReqLike(process,"rolls",postid,repository.getUserID().toInt())
        viewModelScope.launch {
            repository.like(request)
                .catch {

                }
                .collect {
                    Log.i("hnm777",it.message.toString())
                    playPost.like(it.data?.like_count)
                }
        }
    }

    fun follow(followStatus: String, userId: String?) {
        var request=ReqFollow(followStatus,"influencer",userId?.toInt(),repository.getUserID().toInt())
        viewModelScope.launch {
            repository.follow(request)
                .catch {

                }
                .collect {
                    Log.i("hnm777",it.message.toString())
                    if (it.status.equals("true")){

                        Toast.makeText(repository.application, "" + it.message, Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(repository.application, "" + it.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }
    }
    fun share(id: String?, playType: String?, pref: String) {
        apiInterface = NetworkModule.getClient()!!.create(WebApi::class.java)
        val jsonObject = JSONObject()
        jsonObject["id"] = id
        jsonObject["share_type"] = playType
        jsonObject["user_id"] = pref
        apiInterface?.getAddShare(jsonObject)?.enqueue(object : Callback<AddShare> {
            override fun onResponse(call: Call<AddShare>, response: Response<AddShare>) {

                Log.e("TAG", "onResponseff: "+response.body()?.message )
//                shares.setText(" " + response.body()!!.data!!.shareCount)
            }

            override fun onFailure(call: Call<AddShare>, t: Throwable) {

//                Toast.makeText(app, "Item Share Fail", Toast.LENGTH_SHORT).show()
                Log.e("TAG", "onResponseff: "+t.message )
            }
        })
    }
}