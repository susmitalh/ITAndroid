package com.locatocam.app.viewmodels

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.locatocam.app.Activity.PlayPostActivity
import com.locatocam.app.ModalClass.PlayPostData
import com.locatocam.app.data.requests.ReqLike
import com.locatocam.app.repositories.PlayPostRepository
import com.locatocam.app.views.PlayPost.PlayPost
import com.locatocam.app.views.PlayPost.PlayPostFragment
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import net.minidev.json.JSONObject


class PlayPostViewModal(
    val repository: PlayPostRepository
) : ViewModel() {


    var post_id=""
    var influencer_code=""
    var offset: Int
    var lastposition=0

    var playPostList = MutableLiveData<List<PlayPostData>>()

    init {
        offset = 0
    }

    fun PlayPost(activity: PlayPostActivity) {
        val jsonObject = JSONObject()
        jsonObject.put("user_id", repository.getUserID())
        jsonObject.put("influencer_code", influencer_code)
        jsonObject.put("post_id", post_id)
        jsonObject.put("offset",offset.toString())

        Log.e("TAG", "PlayPost: "+repository.getUserID()+","+influencer_code+","+post_id+","+offset.toString() )


        viewModelScope.launch {
            repository.PlayPost(jsonObject).catch {
                Toast.makeText(activity, ""+it.message, Toast.LENGTH_SHORT).show()

            }.collect {
                playPostList.value=it.data
                offset++
                post_id= it.data?.get(it.data!!.size-1)!!.id!!
            }
        }

    }

    fun like(process: String, postid: Int, playPost: PlayPost) {
        var request = ReqLike(process, "post", postid, repository.getUserID().toInt())
        viewModelScope.launch {
            repository.likeplay(request)
                .catch {
                }
                .collect {
                    Log.i("hnm777", it.message.toString())
                    playPost.like(it.data?.like_count)
                }
        }
    }


}