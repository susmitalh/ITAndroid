package com.locatocam.app.viewmodels

import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.locatocam.app.data.requests.*
import com.locatocam.app.data.requests.reqUserProfile.ReqProfileData
import com.locatocam.app.data.requests.reqUserProfile.SocialDetail
import com.locatocam.app.data.responses.*
import com.locatocam.app.data.responses.customer_model.Customer
import com.locatocam.app.data.responses.settings.RespSettings
import com.locatocam.app.data.responses.user_model.Document
import com.locatocam.app.data.responses.user_model.User
import com.locatocam.app.network.Resource
import com.locatocam.app.repositories.*
import com.locatocam.app.utils.Constant
import com.locatocam.app.views.settings.DocLauncher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel@Inject constructor(private val mainRepository: MainRepository,private val settingsRepository: SettingsRepository):ViewModel() {

    var respsettings=MutableLiveData<RespSettings>()
    var userSettingsData = MutableStateFlow<Resource<User>>(Resource.loading(null))
    var customerSettingsData = MutableStateFlow<Resource<Customer>>(Resource.loading(null))
    var companySettingsData = MutableStateFlow<Resource<com.locatocam.app.data.responses.company.Company>>(Resource.loading(null))
    var stateList = MutableStateFlow<Resource<ResState>>(Resource.loading(null))
    var cityList = MutableStateFlow<Resource<ResCity>>(Resource.loading(null))

    val otpResponse = MutableStateFlow<Resource<ResOtp>>(Resource.loading(null))
    val editProfileResponse = MutableStateFlow<Resource<ResEditProfile>>(Resource.loading(null))
    val docUploadResponse = MutableStateFlow<Resource<ResDocUpload>>(Resource.loading(null))

    var socialDetailsList: MutableList<SocialDetail> = ArrayList()

    var socialDetails : MutableLiveData<List<SocialDetail>> = MutableLiveData()
    var isFinal = false

    var launchDocPicker : MutableLiveData<DocLauncher> = MutableLiveData()

    fun launchDocPickerForResult(document: Document, documentView: ImageView){

        launchDocPicker.value = DocLauncher(document,documentView)

    }

    fun getSettings(){
        var reqSettings=ReqSettings(settingsRepository.getUserID())
        Log.i("rtggggg",settingsRepository.getUserID())
        viewModelScope.launch {
            settingsRepository.getSetttings(reqSettings)
                .catch {
                    Log.i("uname",it.message.toString())
                }
                .collect {
                    respsettings.value= it
                    Log.i("uname",it.status.toString())
                }
        }
    }

    fun getSettingsData(userType : String){
        val reqSettings=ReqSettings(settingsRepository.getUserID())
        Log.i("rtggggg",settingsRepository.getUserID())

        when(userType){
            Constant.USER_TYPE->
                viewModelScope.launch {
                    settingsRepository.getUserSetttingsData(reqSettings)
                        .catch {
                            Log.i("getSettingsData",it.message.toString())
                        }
                        .collect {
                            userSettingsData.value= Resource.success(it)
                            Log.i("getSettingsData",it.data.user_details.user_type)
                        }
                }

            Constant.COMPANY_TYPE->
                viewModelScope.launch {
                    settingsRepository.getCompanySetttingsData(reqSettings)
                        .catch {
                            Log.i("getSettingsData",it.message.toString())
                        }
                        .collect {
                            companySettingsData.value= Resource.success(it)
                            Log.i("getSettingsData", it.data.user_details.user_type!!)
                        }
                }

            Constant.CUSTOMER_TYPE->
                viewModelScope.launch {
                    settingsRepository.getCustomerSettingsData(reqSettings)
                        .catch {
                            Log.i("getSettingsData",it.message.toString())
                        }
                        .collect {
                            customerSettingsData.value= Resource.success(it)
                            Log.i("getSettingsData", it.data.user_details.user_type!!)
                        }
                }

        }


    }
   fun sendOtp(reqOtp: ReqSendOtp,isFinal : Boolean) : MutableStateFlow<Resource<ResOtp>>{

       viewModelScope.launch {
           mainRepository.sendOtp(reqOtp)
               .catch {
                   Log.i("Error",it.message.toString())

               }
               .collect {
                   otpResponse.value = Resource.success(it)
               }
       }

       this.isFinal = isFinal

       return otpResponse

   }

    fun editProfile(reqProfileData: ReqProfileData) : MutableStateFlow<Resource<ResEditProfile>>{

        Log.e("profileData",reqProfileData.toString())
        viewModelScope.launch {
            mainRepository.editProfile(reqProfileData)
                .catch {

                }
                .collect {
                    editProfileResponse.value = Resource.success(it)
                }
        }

        return editProfileResponse
    }

    fun getStates() : MutableStateFlow<Resource<ResState>>{
        viewModelScope.launch {
            mainRepository.getStates()
                .catch {
                    Log.e("getStates", "catch")
                }
                .collect {
                    Log.e("getStates", it.data.toString())
                    stateList.value = Resource.success(it)
                }
        }

        return stateList
    }

    fun getCities(req : ReqCity) : MutableStateFlow<Resource<ResCity>>{
        viewModelScope.launch {
            mainRepository.getCities(req)
                .catch {
                    Log.e("getStates", "catch")
                }
                .collect {
                    Log.e("getStates", it.data.toString())
                    cityList.value = Resource.success(it)
                }
        }

        return cityList
    }

    fun uploadDocument(userID: String, docName: String, file: File) : MutableStateFlow<Resource<ResDocUpload>>{

//        Log.e("file",file)

        val multipartBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("user_id", userID)
            .addFormDataPart("doc_name", docName)
            .addFormDataPart("file", file.name, file.asRequestBody())
            .build()

        viewModelScope.launch {
            mainRepository.uploadDocument(multipartBody)
                .catch {
                    Log.e("uploadDocument", it.localizedMessage.toString())
                }
                .collect {
                    Log.e("uploadDocument", it.data.toString())
                    docUploadResponse.value = Resource.success(it)
                }
        }

        return docUploadResponse
    }

    fun setSocialDetailsList(socialDetail: SocialDetail){
        socialDetailsList.add(socialDetail)
    }

    fun addSocialData(socialDetail: SocialDetail,position : Int){
        Log.e("socialDetail", socialDetail.link.toString())
        var so = socialDetailsList[position]
        so.follower = socialDetail.follower
        so.name = socialDetail.name
        so.link = socialDetail.link
        so.id = socialDetail.id
        socialDetailsList[position] = so
        socialDetails.value = (socialDetailsList)

    }

}