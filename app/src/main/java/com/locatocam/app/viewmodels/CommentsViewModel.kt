package com.locatocam.app.viewmodels

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.locatocam.app.Activity.PlayPostActivity
import com.locatocam.app.data.requests.ReqAddComment
import com.locatocam.app.data.requests.ReqGetCommets
import com.locatocam.app.data.responses.comments.Data
import com.locatocam.app.repositories.CommentsRepository
import com.locatocam.app.views.PlayPost.PlayPost
import com.locatocam.app.views.PlayPost.PlayPostFragment
import com.locatocam.app.views.comments.CommentsActivity
import com.locatocam.app.views.home.HomeFragment.Companion.commet
import com.locatocam.app.views.home.OtherProfileWithFeedFragment
import com.locatocam.app.views.home.test.SimpleExoPlayerViewHolder
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CommentsViewModel(
    val repository: CommentsRepository
    ):ViewModel() {

    var comments = MutableLiveData<List<Data>>()
    var save_status = MutableLiveData<Boolean>()



    var post_id=""
    var userId=""
    var commentType=""
    init {

    }

    fun getComments() {
        var reqComments=ReqGetCommets(commentType,post_id.toInt(),userId.toInt())

        Log.e(
            "TAG", "getComments: "+commentType+","+post_id.toInt()+","+userId.toInt() )
        viewModelScope.launch {
            repository.getComments(reqComments)
                .catch {
                    Log.i("uname", it.message.toString())
                }
                .collect {
                    Log.i("uname", it.message)
                    if (it.message.equals("success")){
                        comments.value=it.data
                        Log.e("TAG", "getCommentccfs: "+it.data.size )
                    }
                }
        }
    }

    fun addComments(comment: String, position: Int) {

        var reqComments=ReqAddComment(
            comment,
            commentType,
            post_id.toInt(),
            repository.getUserID().toInt())


        viewModelScope.launch {
            repository.addComments(reqComments)
                .catch {
                    Log.i("uname", it.message.toString())
                }
                .collect {
                   save_status.value=true
                    CommentsActivity.commentNo=true
                    Log.e("TAG", "addComments: "+it.data.comment_count )
                    commet?.commentCount(it.data.comment_count,position)
                    OtherProfileWithFeedFragment.postCountData?.commentCount(it.data.comment_count,position)
//                    PlayPostFragment.playPost.comment(it.data.comment_count) }
                    var intent = Intent();
                    intent.putExtra("comment_count", it.data.comment_count)
                    CommentsActivity.activity.setResult(Activity.RESULT_OK, intent)
                    CommentsActivity.activity.finish();
        }
    }
}}