package com.locatocam.app.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.locatocam.app.data.requests.*
import com.locatocam.app.data.requests.reqUserProfile.ReqViewApproval
import com.locatocam.app.data.responses.followers.RespFollowers
import com.locatocam.app.data.responses.my_activity.RespMyActivity
import com.locatocam.app.data.responses.notification.RespNotification
import com.locatocam.app.data.responses.products.Data
import com.locatocam.app.data.responses.settings.pendingPost.RespViewApproval
import com.locatocam.app.network.Resource
import com.locatocam.app.repositories.AddProductRepository
import com.locatocam.app.repositories.FollowersRepository
import com.locatocam.app.repositories.ViewActivityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.Dispatcher

class ViewActivityViewModel(val repository: ViewActivityRepository):ViewModel() {
    val myactivity = MutableStateFlow<Resource<RespMyActivity>>(Resource.loading(null))
    val mynotification = MutableStateFlow<Resource<RespNotification>>(Resource.loading(null))
    init {
    }
    fun getActivity(request:ReqMyActivity){

        //var request= ReqMyActivity(repository.getUserID().toInt(),"1")
        viewModelScope.launch {
            repository.getViewActivity(request)
                .catch {e->
                    myactivity.value = (Resource.error(e.toString(), null))
                }.collect {
                    myactivity.value=(Resource.success(it))
                }
        }
    }
    fun getNotification(request:ReqMyActivity){

        //var request= ReqMyActivity(repository.getUserID().toInt(),"1")
        viewModelScope.launch {
            repository.getNotification(request)
                .catch {e->
                    mynotification.value = (Resource.error(e.toString(), null))
                }.collect {
                    mynotification.value=(Resource.success(it))
                }
        }
    }



}