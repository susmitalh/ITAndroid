package com.locatocam.app.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.locatocam.app.ModalClass.FAQ
import com.locatocam.app.ModalClass.FAQData
import com.locatocam.app.data.requests.ReqOnlineOrderHelp
import com.locatocam.app.data.responses.comments.Data
import com.locatocam.app.repositories.OnlineOrderHelpRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class OnlineOrderHelpViewModel(
    val repository: OnlineOrderHelpRepository
) : ViewModel() {

    var FAQDataList = MutableLiveData<List<FAQData>>()

    fun getDataFAQ() {
        var request=ReqOnlineOrderHelp(repository.getUserID())

        viewModelScope.launch {
            repository.getFAQ(request).catch {

            }.collect {
                try {
                    Log.e("TAG", "getDataFAQ: "+it.data?.get(0)?.answer )
                    FAQDataList.value=it.data
                } catch (e: Exception) {
                }

            }
        }

    }
}