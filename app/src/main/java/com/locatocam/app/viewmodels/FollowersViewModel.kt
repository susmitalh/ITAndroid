package com.locatocam.app.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.locatocam.app.data.requests.*
import com.locatocam.app.data.responses.RespFollow
import com.locatocam.app.data.responses.favOrder.ResFavOrder
import com.locatocam.app.data.responses.followers.RespFollowers
import com.locatocam.app.data.responses.products.Data
import com.locatocam.app.network.Resource
import com.locatocam.app.repositories.AddProductRepository
import com.locatocam.app.repositories.FollowersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.Dispatcher

class FollowersViewModel(
    val repository: FollowersRepository
    ):ViewModel() {

    val followers = MutableStateFlow<Resource<RespFollowers>>(Resource.loading(null))
    val follow = MutableStateFlow<Resource<RespFollow>>(Resource.loading(null))
    var list_type=MutableLiveData<String>()

    var firsttab=0
    var secondtab=0
    init {
    }

    fun makerequest(){
    var ret="influencer_followers"
        if (firsttab==0){
            if (secondtab==0){
                ret="influencer_followers"
            }else{
                ret="influencer_following"
            }
        }else if (firsttab==1){
            if (secondtab==0){
                ret="brand_followers"
            }else{
                ret="brand_following"
            }
        }else{
            if (secondtab==0){
                ret="people_followers"
            }else{
                ret="people_following"
            }
        }

        list_type.postValue(ret)
        Log.i("looo9dv","$firsttab --- $secondtab")
    }
    fun getFollowers(){
        //val followers = MutableStateFlow<Resource<RespFollowers>>(Resource.loading(null))
        var request= ReqFollowers(repository.getUserID().toInt())
        viewModelScope.launch {
            repository.getFollowers(request)
                .catch {e->
                    followers.value = (Resource.error(e.toString(), null))
                }.collect {
                    followers.value=(Resource.success(it))
                }
        }
    }
   fun postFollow(follow_process: String,followtype: String,followerid: Int): MutableStateFlow<Resource<RespFollow>>{
       var request =
           ReqFollow(followtype,follow_process,followerid, repository.getUserID().toInt())
       viewModelScope.launch {
           repository.reqfollow(request)
               .catch {
                   Log.i("uname",it.message.toString())
               }
               .collect {
                   follow.value=/*it*/Resource.success(it)
                   Log.i("uname",it.status.toString())
               }
       }
       return follow
   }


}