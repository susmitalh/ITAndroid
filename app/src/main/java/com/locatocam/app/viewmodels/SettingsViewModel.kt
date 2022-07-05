package com.locatocam.app.viewmodels

import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.locatocam.app.data.requests.*
import com.locatocam.app.data.requests.reqUserProfile.ReqBlockedUser
import com.locatocam.app.data.requests.reqUserProfile.ReqProfileData
import com.locatocam.app.data.requests.reqUserProfile.ReqViewApproval
import com.locatocam.app.data.requests.reqUserProfile.SocialDetail
import com.locatocam.app.data.requests.viewApproval.ReqApprove
import com.locatocam.app.data.requests.viewApproval.ReqCompanyApprove
import com.locatocam.app.data.requests.viewApproval.ReqCompanyReject
import com.locatocam.app.data.requests.viewApproval.ReqReject
import com.locatocam.app.data.responses.*
import com.locatocam.app.data.responses.changeInfluencer.ResChangeInfluencer
import com.locatocam.app.data.responses.customer_model.Customer
import com.locatocam.app.data.responses.favOrder.ResFavOrder
import com.locatocam.app.data.responses.settings.*
import com.locatocam.app.data.responses.settings.Approved.ApprovedPost
import com.locatocam.app.data.responses.settings.companyApproved.companyApproved
import com.locatocam.app.data.responses.settings.companyPending.CompanyPending
import com.locatocam.app.data.responses.settings.companyRejected.companyRejected
import com.locatocam.app.data.responses.settings.pendingPost.RespViewApproval
import com.locatocam.app.data.responses.settings.rejectedPost.ResRejected
import com.locatocam.app.data.responses.user_model.Document
import com.locatocam.app.data.responses.user_model.User
import com.locatocam.app.data.responses.yourOrder.ResYourOrder
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
    var respInfluencerSop=MutableLiveData<InfluencerSop>()
    var respViewBlockUSerList=MutableStateFlow<Resource<ViewBlockUser>>(Resource.loading(null))
    var respBlockedUser=MutableStateFlow<Resource<ResBlockedUser>>(Resource.loading(null))
    var respRejectedlList=MutableStateFlow<Resource<ResRejected>>(Resource.loading(null))
    var respRejectApprovStatus=MutableStateFlow<Resource<StatusApproved>>(Resource.loading(null))
    var respApprovedStatus=MutableStateFlow<Resource<StatusApproved>>(Resource.loading(null))
    var resYourOrders = MutableStateFlow<Resource<ResYourOrder>>(Resource.loading(null))
    var resFavOrders = MutableStateFlow<Resource<ResFavOrder>>(Resource.loading(null))
    var resPrivacyPolicy= MutableStateFlow<Resource<ResPrivacyPolicy>>(Resource.loading(null))
    var resTermsCon= MutableStateFlow<Resource<ResTermsCon>>(Resource.loading(null))
    var resChangeInflu=MutableStateFlow<Resource<ResChangeInfluencer>>(Resource.loading(null))
    var reqChInfluencer=MutableStateFlow<Resource<ResBlockedUser>>(Resource.loading(null))


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

    fun getInfluencerSop(){
        viewModelScope.launch {
            settingsRepository.getInfluencerSop()
                .catch {
                    Log.i("uname",it.message.toString())
                }
                .collect {
                    respInfluencerSop.value=it
                    Log.i("uname",it.status.toString())
                }
        }
    }
    fun getPocSop(){
        viewModelScope.launch {
            settingsRepository.getPocSop()
                .catch {
                    Log.i("uname",it.message.toString())
                }
                .collect {
                    respInfluencerSop.value=it
                    Log.i("uname",it.status.toString())
                }
        }
    }
    fun getViewBlockUser(): MutableStateFlow<Resource<ViewBlockUser>>{
        val reqSettings=ReqSettings(settingsRepository.getUserID())
        viewModelScope.launch {
            settingsRepository.getViewBlockUserData(reqSettings)
                .catch {
                    Log.i("uname",it.message.toString())
                }
                .collect {
                    respViewBlockUSerList.value=/*it*/Resource.success(it)
                    Log.i("uname",it.status.toString())
                }
        }
        return respViewBlockUSerList
    }
    fun postBlokedUser(reqBlockedUser: ReqBlockedUser) : MutableStateFlow<Resource<ResBlockedUser>>{

        viewModelScope.launch {
            settingsRepository.getBlockedUser(reqBlockedUser)
                .catch {
                    Log.i("Error",it.message.toString())

                }
                .collect {
                    respBlockedUser.value = Resource.success(it)
                }
        }
        return respBlockedUser

    }

    fun getViewPendingUser(reqViewApproval: ReqViewApproval): MutableStateFlow<Resource<RespViewApproval>>{
        var respViewApprovalList=MutableStateFlow<Resource<RespViewApproval>>(Resource.loading(null))
        val reqSettings=ReqSettings(settingsRepository.getUserID())
        viewModelScope.launch {
            settingsRepository.getViewApprovalList(reqViewApproval)
                .catch {
                    Log.i("uname",it.message.toString())
                }
                .collect {
                    respViewApprovalList.value=/*it*/Resource.success(it)
                    Log.i("uname",it.status.toString())
                }
        }
        return respViewApprovalList
    }
    fun getViewApprovedList(reqViewApproval: ReqViewApproval): MutableStateFlow<Resource<ApprovedPost>>{
        var respApprovedlList=MutableStateFlow<Resource<ApprovedPost>>(Resource.loading(null))
        viewModelScope.launch {
            settingsRepository.getViewApprovedList(reqViewApproval)
                .catch {
                    Log.i("uname",it.message.toString())
                }
                .collect {
                    respApprovedlList.value=/*it*/Resource.success(it)
                    Log.i("uname",it.status.toString())
                }
        }
        return respApprovedlList
    }

    fun getViewRejectedList(reqViewApproval: ReqViewApproval): MutableStateFlow<Resource<ResRejected>>{
        var respRejectedlList=MutableStateFlow<Resource<ResRejected>>(Resource.loading(null))
        viewModelScope.launch {
            settingsRepository.getViewRejectedList(reqViewApproval)
                .catch {
                    Log.i("uname",it.message.toString())
                }
                .collect {
                    respRejectedlList.value=/*it*/Resource.success(it)
                    Log.i("uname",it.status.toString())
                }
        }
        return respRejectedlList
    }

    fun getCompanyPendingUser(reqViewApproval: ReqViewApproval): MutableStateFlow<Resource<CompanyPending>>{
        var respViewCompanyPendingList=MutableStateFlow<Resource<CompanyPending>>(Resource.loading(null))
        viewModelScope.launch {
            settingsRepository.getCompanyPendindList(reqViewApproval)
                .catch {
                    Log.i("uname",it.message.toString())
                }
                .collect {
                    respViewCompanyPendingList.value=/*it*/Resource.success(it)
                    Log.i("uname",it.status.toString())
                }
        }
        return respViewCompanyPendingList
    }
    fun getCompanyApprovedList(reqViewApproval: ReqViewApproval): MutableStateFlow<Resource<companyApproved>>{
        var respCompanyApprovedlList=MutableStateFlow<Resource<companyApproved>>(Resource.loading(null))
        viewModelScope.launch {
            settingsRepository.getCompanyApprovedList(reqViewApproval)
                .catch {
                    Log.i("uname",it.message.toString())
                }
                .collect {
                    respCompanyApprovedlList.value=/*it*/Resource.success(it)
                    Log.i("uname",it.status.toString())
                }
        }
        return respCompanyApprovedlList
    }
    fun getComapnyRejectedList(reqViewApproval: ReqViewApproval): MutableStateFlow<Resource<companyRejected>>{
        var respComapnyRejectedlList=MutableStateFlow<Resource<companyRejected>>(Resource.loading(null))
        viewModelScope.launch {
            settingsRepository.getCompanyRejectedList(reqViewApproval)
                .catch {
                    Log.i("uname",it.message.toString())
                }
                .collect {
                    respComapnyRejectedlList.value=/*it*/Resource.success(it)
                    Log.i("uname",it.status.toString())
                }
        }
        return respComapnyRejectedlList
    }

    fun postReject(reqReject: ReqReject) : MutableStateFlow<Resource<StatusApproved>>{

        viewModelScope.launch {
            settingsRepository.postReject(reqReject)
                .catch {
                    Log.i("Error",it.message.toString())

                }
                .collect {
                    respRejectApprovStatus.value = Resource.success(it)
                }
        }
        return respRejectApprovStatus

    }
    fun postApprove(reqApprove: ReqApprove) : MutableStateFlow<Resource<StatusApproved>>{

        viewModelScope.launch {
            settingsRepository.postApprove(reqApprove)
                .catch {
                    Log.i("Error",it.message.toString())

                }
                .collect {
                    respApprovedStatus.value = Resource.success(it)
                }
        }
        return respApprovedStatus

    }
    fun postrepost(reqApprove: ReqApprove) : MutableStateFlow<Resource<StatusApproved>>{

        viewModelScope.launch {
            settingsRepository.repost(reqApprove)
                .catch {
                    Log.i("Error",it.message.toString())

                }
                .collect {
                    respApprovedStatus.value = Resource.success(it)
                }
        }
        return respApprovedStatus

    }


    fun postCompanyReject(reqCompanyReject: ReqCompanyReject) : MutableStateFlow<Resource<StatusApproved>>{

        viewModelScope.launch {
            settingsRepository.companyReject(reqCompanyReject)
                .catch {
                    Log.i("Error",it.message.toString())

                }
                .collect {
                    respRejectApprovStatus.value = Resource.success(it)
                }
        }
        return respRejectApprovStatus

    }
    fun postCompanyApprove(reqCompanyApprove: ReqCompanyApprove) : MutableStateFlow<Resource<StatusApproved>>{

        viewModelScope.launch {
            settingsRepository.companyApprove(reqCompanyApprove)
                .catch {
                    Log.i("Error",it.message.toString())

                }
                .collect {
                    respApprovedStatus.value = Resource.success(it)
                }
        }
        return respApprovedStatus

    }
    fun postCompanyRepost(reqCompanyApprove: ReqCompanyApprove) : MutableStateFlow<Resource<StatusApproved>>{

        viewModelScope.launch {
            settingsRepository.companyrepost(reqCompanyApprove)
                .catch {
                    Log.i("Error",it.message.toString())

                }
                .collect {
                    respApprovedStatus.value = Resource.success(it)
                }
        }
        return respApprovedStatus

    }



    fun getMyPostReelsPendingList(reqViewApproval: ReqViewApproval): MutableStateFlow<Resource<RespViewApproval>>{
        var respViewApprovalList=MutableStateFlow<Resource<RespViewApproval>>(Resource.loading(null))
        viewModelScope.launch {
            settingsRepository.getMyPostReelPending(reqViewApproval)
                .catch {
                    Log.i("uname",it.message.toString())
                }
                .collect {
                    respViewApprovalList.value=/*it*/Resource.success(it)
                    Log.i("uname",it.status.toString())
                }
        }
        return respViewApprovalList
    }
    fun getMyPostReelsApprovedList(reqViewApproval: ReqViewApproval): MutableStateFlow<Resource<ApprovedPost>>{
        var respApprovedlList=MutableStateFlow<Resource<ApprovedPost>>(Resource.loading(null))
        viewModelScope.launch {
            settingsRepository.getMyPostReelApproved(reqViewApproval)
                .catch {
                    Log.i("uname",it.message.toString())
                }
                .collect {
                    respApprovedlList.value=/*it*/Resource.success(it)
                    Log.i("uname",it.status.toString())
                }
        }
        return respApprovedlList
    }

    fun getMyPostReelsejectedList(reqViewApproval: ReqViewApproval): MutableStateFlow<Resource<ResRejected>>{
        val reqSettings=ReqSettings(settingsRepository.getUserID())
        viewModelScope.launch {
            settingsRepository.getMyPostReelRejected(reqViewApproval)
                .catch {
                    Log.i("uname",it.message.toString())
                }
                .collect {
                    respRejectedlList.value=/*it*/Resource.success(it)
                    Log.i("uname",it.status.toString())
                }
        }
        return respRejectedlList
    }
    fun getYourOrdersList(reqOrders: ReqOrders): MutableStateFlow<Resource<ResYourOrder>>{
        viewModelScope.launch {
            settingsRepository.getYourOrders(reqOrders)
                .catch {
                    Log.i("uname",it.message.toString())
                }
                .collect {
                    resYourOrders.value=/*it*/Resource.success(it)
                    Log.i("uname",it.status.toString())
                }
        }
        return resYourOrders
    }

    fun getYourFavsList(reqOrders: ReqOrders): MutableStateFlow<Resource<ResFavOrder>>{
        viewModelScope.launch {
            settingsRepository.getFavOrders(reqOrders)
                .catch {
                    Log.i("uname",it.message.toString())
                }
                .collect {
                    resFavOrders.value=/*it*/Resource.success(it)
                    Log.i("uname",it.status.toString())
                }
        }
        return resFavOrders
    }
    fun getPrivacy(reqOrders: ReqOrders): MutableStateFlow<Resource<ResPrivacyPolicy>>{
        viewModelScope.launch {
            settingsRepository.getPrivacyPolicy(reqOrders)
                .catch {
                    Log.i("uname",it.message.toString())
                }
                .collect {
                    resPrivacyPolicy.value=/*it*/Resource.success(it)
                    Log.i("uname",it.status.toString())
                }
        }
        return resPrivacyPolicy
    }
    fun getTermsCon(reqOrders: ReqOrders): MutableStateFlow<Resource<ResTermsCon>>{
        viewModelScope.launch {
            settingsRepository.getTermsCon(reqOrders)
                .catch {
                    Log.i("uname",it.message.toString())
                }
                .collect {
                    resTermsCon.value=/*it*/Resource.success(it)
                    Log.i("uname",it.status.toString())
                }
        }
        return resTermsCon
    }

    fun getChangeInfluencer(reqOrders: ReqOrders): MutableStateFlow<Resource<ResChangeInfluencer>>{
        viewModelScope.launch {
            settingsRepository.getChangeInfluencer(reqOrders)
                .catch {
                    Log.i("uname",it.message.toString())
                }
                .collect {
                    resChangeInflu.value=/*it*/Resource.success(it)
                    Log.i("uname",it.status.toString())
                }
        }
        return resChangeInflu
    }

    fun reqChangeInfluencer(req: ReqChInfluencer): MutableStateFlow<Resource<ResBlockedUser>>{
        viewModelScope.launch {
            settingsRepository.reqChangeInfluencer(req)
                .catch {
                    Log.i("uname",it.message.toString())
                }
                .collect {
                    reqChInfluencer.value=/*it*/Resource.success(it)
                    Log.i("uname",it.status.toString())
                }
        }
        return reqChInfluencer
    }

}