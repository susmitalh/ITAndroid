package com.locatocam.app.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.locatocam.app.data.ApprovedPosts
import com.locatocam.app.data.PendingPost
import com.locatocam.app.data.requests.ReqPendingPost
import com.locatocam.app.data.requests.ReqPostApproval
import com.locatocam.app.data.responses.followers.RespFollowers
import com.locatocam.app.network.Resource
import com.locatocam.app.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PendingApprovalViewModel @Inject constructor(private val mainRepository: MainRepository) : ViewModel() {

//    var pendingApprovals = MutableLiveData<List<PendingPost>>()

    val pendingApprovals = MutableStateFlow<Resource<List<PendingPost>>>(Resource.loading(null))
    val approvedApprovals = MutableStateFlow<Resource<List<ApprovedPosts>>>(Resource.loading(null))

    fun getBrandPending(reqfeed: ReqPostApproval) : MutableStateFlow<Resource<List<PendingPost>>>{

        viewModelScope.launch {

            mainRepository.getBrandPending(reqfeed)
                .catch {e->
                    pendingApprovals.value = (Resource.error(e.toString(), null))
                }.collect {
                    pendingApprovals.value=(Resource.success(it))
                }
        }

        return pendingApprovals

    }

    fun getApprovedApprovals(reqfeed: ReqPostApproval) : MutableStateFlow<Resource<List<ApprovedPosts>>>{

        viewModelScope.launch {

            mainRepository.getApprovedApprovals(reqfeed)
                .catch {e->
                    approvedApprovals.value = (Resource.error(e.toString(), null))
                }.collect {
                    approvedApprovals.value=(Resource.success(it))
                }
        }

        return approvedApprovals

    }
}