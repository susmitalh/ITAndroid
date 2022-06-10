package com.locatocam.app.viewmodels

import android.os.Handler
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.locatocam.app.Activity.ViewMyPostActivity
import com.locatocam.app.ModalClass.MyPostData
import com.locatocam.app.R
import com.locatocam.app.data.requests.ReqFollow
import com.locatocam.app.data.requests.ReqTrash
import com.locatocam.app.data.responses.RespTrash
import com.locatocam.app.repositories.ViewMyPostRepository
import com.locatocam.app.views.home.test.Follow
import kotlinx.android.synthetic.main.activity_view_my_post.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import net.minidev.json.JSONObject

class ViewMyPostViewModal(
    val repository: ViewMyPostRepository
) : ViewModel() {

    var offset: Int
    var post_id:String
    var my_id = ""
    var loading = true
    lateinit var followProcess:String
    var myPostList = MutableLiveData<MyPostData>()
    var myShortList = MutableLiveData<MyPostData>()
    var trash = MutableLiveData<RespTrash>()



    init {
        offset = 0
        post_id="0"
    }

    fun MyPost(activity: ViewMyPostActivity) {
        loading = true
        val jsonObject = JSONObject()
        jsonObject.put("my_id", repository.getUserID())
        jsonObject.put("user_id", my_id)
        jsonObject.put("post_id", post_id)
        jsonObject.put("offset", offset.toString())

        Log.e("TAG", "MyPost: "+my_id+","+repository.getUserID()+","+offset )

        viewModelScope.launch {
            repository.MyPost(jsonObject).catch {
                activity.hideLoader()
                Toast.makeText(activity, ""+it.message, Toast.LENGTH_SHORT).show()
            }.collect {
                Handler().postDelayed(Runnable {
                   activity.hideLoader()

                }, 2500)

                myPostList.value=it.data
                if (!it.data?.posts.isNullOrEmpty()){
                post_id= it.data?.posts?.get(it.data?.posts!!.size-1)?.postId.toString()
                offset++

                }

            }
        }
    }
    fun myShort(activity: ViewMyPostActivity) {
        loading = true
        val jsonObject = JSONObject()
        jsonObject.put("my_id", repository.getUserID())
        jsonObject.put("user_id",my_id)
        jsonObject.put("post_id", post_id)
        jsonObject.put("offset", offset.toString())

        Log.e("TAG", "myShort: "+my_id+","+repository.getUserID()+","+offset+","+post_id )
        viewModelScope.launch {
            repository.MyShort(jsonObject).catch {
                Toast.makeText(activity, ""+it.message, Toast.LENGTH_SHORT).show()

            }.collect {
//                Toast.makeText(activity, ""+it.data?.name, Toast.LENGTH_SHORT).show()

//                myShortList.value=it.data
                Log.e("TAG", "myShort: "+it.data?.rolls )
                Handler().postDelayed(Runnable {
                    activity.hideLoader()

                }, 1000)
                myShortList.value=it.data
                if (!it.data?.rolls.isNullOrEmpty()){

                    post_id= it.data?.posts?.get(it.data?.posts!!.size-1)?.postId.toString()
                    offset++

                }
            }

        }
    }
    fun follow(
        followProcess: String,
        followId: String,
        activity: ViewMyPostActivity,
        followListner: Follow
    ) {

        var request= ReqFollow(followProcess,"influencer",followId.toInt(),repository.getUserID().toInt())

        Log.e("TAG", "followdd: "+followProcess+","+followId )
        viewModelScope.launch {
            repository.follow(request)
                .catch {
                    Log.e("TAG", "folloddw: "+it.message )
                }
                .collect {
                    Log.e("TAG", "folloddw: "+it.message )
                    Toast.makeText(activity, ""+it.message, Toast.LENGTH_SHORT).show()
                    if (it.message.equals("success")){
                        if (followProcess.equals("unfollow")){
                            this@ViewMyPostViewModal.followProcess ="follow"
                            followListner.follow(0, "Follow")

                        }else{
                            this@ViewMyPostViewModal.followProcess ="unfollow"
                            followListner.follow(0, "Following")

                        }
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

                    Log.i("hnm777", it.status)
                    trash.value = it
                }
        }
    }
}