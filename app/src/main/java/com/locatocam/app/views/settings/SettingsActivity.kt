package com.locatocam.app.views.settings

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.locatocam.app.Activity.OtherProfileWithFeedActivity
import com.locatocam.app.R
import com.locatocam.app.data.requests.ReqSendOtp
import com.locatocam.app.data.responses.settings.companyPending.Detail
import com.locatocam.app.databinding.SettingsFragmentBinding
import com.locatocam.app.network.Status
import com.locatocam.app.network.UserDetailsMapper
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.utils.Constant
import com.locatocam.app.utils.Utils
import com.locatocam.app.viewmodels.SettingsViewModel
import com.locatocam.app.views.MainActivity
import com.locatocam.app.views.approvals.ApprovalBaseActivity
import com.locatocam.app.views.notifications.ActivityNotifications
import com.locatocam.app.views.settings.adapters.CompanyMenuAdapter
import com.locatocam.app.views.settings.adapters.CustomerMenuAdapter
import com.locatocam.app.views.settings.adapters.UserMenuAdapter
import com.locatocam.app.views.view_activity.ViewActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


@AndroidEntryPoint
class SettingsActivity : AppCompatActivity(){

    lateinit var  binding:SettingsFragmentBinding
    lateinit var viewmodel:SettingsViewModel
    private lateinit var dialog : Dialog
    private lateinit var loading : Dialog
    private lateinit var userDetails : com.locatocam.app.data.responses.user_model.UserDetails
    private lateinit var companyDetails : com.locatocam.app.data.responses.company.UserDetails
    private lateinit var customerDetails : com.locatocam.app.data.responses.customer_model.UserDetails
     var userId=""
     var influencerCode=""
    private val req_code = 101

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data!!

//                binding.image.setImageURI(fileUri)


                viewmodel.uploadDocument(SharedPrefEnc.getPref(applicationContext,"user_id"),"photo",File(
                    Utils.getActualPath(fileUri,applicationContext)
                ))

                observeImageUpload()

            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Log.e("ImagePicker",ImagePicker.getError(data))
            } else {
                Log.e("ImagePicker","cancelled")
            }
        }

    private fun observeImageUpload() {

        lifecycleScope.launch {

            viewmodel.docUploadResponse.collect {

                when (it.status) {

                    Status.SUCCESS -> {

                      //  showProgress(false, "")
                        MainActivity.binding.loader.visibility= View.GONE
                        Glide.with(applicationContext)
                            .load(Uri.parse(it.data!!.data))
                            .into(binding.image)

                    }
                    Status.LOADING -> {
                       // showProgress(true, "Uploading Image...")
                        MainActivity.binding.loader.visibility= View.VISIBLE
                    }
                    Status.ERROR -> {
                       // showProgress(false, "")
                        MainActivity.binding.loader.visibility= View.GONE

                    }

                }

            }
        }

    }

    @Inject
    lateinit var userDetailsMapper: UserDetailsMapper
    /*override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout for this fragment
        binding= SettingsFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MainActivity.activity.finish()
        dialog = Dialog(this)
        loading = Dialog(this)
        binding.menuItemList.layoutManager = LinearLayoutManager(applicationContext)

        binding.menuItemList.itemAnimator = DefaultItemAnimator()

        viewmodel= ViewModelProvider(this).get(SettingsViewModel::class.java)


        binding.image.setOnClickListener {

            pickImage()

        }

//        setObservers()
        setOnclickListeners()
        binding.editProfile.setOnClickListener {

            sendOtp()

        }

        //return binding.root
      //  hideLoader()
    }

    override fun onResume() {
        super.onResume()


        recreate()
    }

    private fun pickImage() {

        Dexter.withContext(applicationContext)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {

                    ImagePicker.with(this@SettingsActivity)
//                            .compress(1024)         //Final image size will be less than 1 MB(Optional)
//                            .maxResultSize(1080, 1080)  //Final image resolution will be less than 1080 x 1080(Optional)
                        .galleryOnly()
                        .createIntent { intent ->
                            startForProfileImageResult.launch(intent)
                        }

                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                }

            }).check()

    }


    private fun manageSettingsData() {
        when(SharedPrefEnc.getPref(applicationContext,"user_type")){
            Constant.USER_TYPE->
                observeUserSettingsData()
                
            Constant.COMPANY_TYPE->observeCompanySettingsData()
            Constant.CUSTOMER_TYPE->observeCustomerSettingsData()
        }
    }


    private fun observeCustomerSettingsData() {
        lifecycleScope.launch {

            viewmodel.customerSettingsData.collect {

                when(it.status){

                    Status.SUCCESS -> {
                        userId=it.data!!.data.user_details.user_id!!
                        influencerCode=it.data!!.data.user_details.influencer_code!!
                        binding.userName.text = it.data!!.data.user_details.name
                        binding.email.text = it.data!!.data.user_details.email
                        customerDetails = it.data.data.user_details
                        Log.e("userData", it.data.data.menu_details.toString())
                        val menuAdapter =  CustomerMenuAdapter(it.data.data.menu_details,applicationContext,customerDetails)

                        binding.menuItemList.adapter = menuAdapter


                        setProfileData(userDetailsMapper.mapToEntity(it.data.data.user_details))

                        //showProgress(false,"")
                        MainActivity.binding.loader.visibility= View.GONE

                    }
                    Status.LOADING -> {
                        //showProgress(true,"Fetching Profile Data...")
                        MainActivity.binding.loader.visibility= View.VISIBLE
                    }
                    Status.ERROR -> {
                        //showProgress(false,"")
                        MainActivity.binding.loader.visibility= View.GONE

                    }

                }

            }

        }
    }


    private fun observeCompanySettingsData() {
        lifecycleScope.launch {

            viewmodel.companySettingsData.collect {

                when(it.status){

                    Status.SUCCESS -> {
                        userId=it.data!!.data.user_details.user_id!!
                        influencerCode=it.data!!.data.user_details.influencer_code!!
                        binding.userName.text = it.data!!.data.user_details.name
                        binding.email.text = it.data!!.data.user_details.email
                        companyDetails = it.data.data.user_details
                        Log.e("userData", it.data.data.menu_details.toString())
                        val menuAdapter =  CompanyMenuAdapter(it.data.data.menu_details,applicationContext,companyDetails)

                        binding.menuItemList.adapter = menuAdapter

                        setProfileCompanyData(it.data.data.user_details)
                     //   showProgress(false,"")
                        MainActivity.binding.loader.visibility= View.GONE

                    }
                    Status.LOADING -> {
                      //  showProgress(true,"Fetching Profile Data...")
                        MainActivity.binding.loader.visibility= View.VISIBLE

                        Log.i("editButton", "Loading")

                    }
                    Status.ERROR -> {
                        MainActivity.binding.loader.visibility= View.GONE
                        //showProgress(false,"")

                        Log.i("editButton", it.message.toString())
                    }

                }

            }

        }
    }



    private fun observeUserSettingsData() {

        lifecycleScope.launch {

            viewmodel.userSettingsData.collect {

                when(it.status){

                    Status.SUCCESS -> {
                        userId=it.data!!.data.user_details.user_id
                        influencerCode=it.data!!.data.user_details.influencer_code
                        binding.userName.text = it.data!!.data.user_details.name
                        binding.email.text = it.data!!.data.user_details.email
                        userDetails = it.data.data.user_details
                        val menuAdapter =  UserMenuAdapter(it.data!!.data.menu_details,application,userDetails)

                        binding.menuItemList.adapter = menuAdapter

                        setUserProfileData(it.data.data.user_details)
                        //showProgress(false,"")
                        MainActivity.binding.loader.visibility= View.GONE

                    }
                    Status.LOADING -> {
                        Log.i("editButton", "Loading")
                        MainActivity.binding.loader.visibility= View.VISIBLE

                       // showProgress(true,"Fetching Profile Data...")

                    }
                    Status.ERROR -> {
                        Log.i("editButton", it.message.toString())
                      //  showProgress(false,"")
                        MainActivity.binding.loader.visibility= View.GONE

                    }

                }

            }

        }

    }

    fun setObservers(){


        viewmodel.respsettings.observe(this) { respsettings ->
            var ud = respsettings.data.user_details
            val menuDetails = respsettings.data.menu_details

            var docs = ud.documents
            for (fl in docs)
                if (fl.doc_name.equals("photo")) {
                    Glide.with(this)
                        .load(fl.doc_location)
                        .into(binding.image)
                }
//            binding.userName.text = ud.name
//            binding.email.text = ud.email


//            setUserProfileData(ud)


//            binding.phoneText.text = Utils.toEditable(ud.pan_no)

//            var menu_deails = respsettings.data.menu_details
//            if (menu_deails.approvals.my_post_reels_approvals_pending > 0) {
//                binding.myPostReelsApprovalpnding.text =
//                    "My Post/Reels Approval Pending (" + menu_deails.approvals.my_post_reels_approvals_pending.toString() + ")"
//            }

            //Log.i("looo9",docs.get(0).doc_location)

        }


        viewmodel.getSettings()

    }



    fun setOnclickListeners(){

        binding.linearMyPage.setOnClickListener {

            val intent = Intent(this, OtherProfileWithFeedActivity::class.java)
            intent.putExtra("user_id",userId)
            intent.putExtra("inf_code",influencerCode)
            startActivity(intent)

        }

        binding.viewActivity.setOnClickListener {
            var intent=Intent(applicationContext,ViewActivity::class.java)
            startActivity(intent)
        }

        binding.notification.setOnClickListener {
            var intent=Intent(applicationContext,ActivityNotifications::class.java)
            startActivity(intent)
        }

        binding.back.setOnClickListener {
            var intent=Intent(applicationContext,MainActivity::class.java)
            startActivity(intent)
        }


        binding.myPostReelsApprovalpnding.setOnClickListener(View.OnClickListener {

            val intent=Intent(applicationContext,ApprovalBaseActivity::class.java)
            startActivity(intent)

        })
        binding.home.setOnClickListener {
            var intent=Intent(applicationContext,MainActivity::class.java)
            startActivity(intent)
        }


    }

    fun sendOtp(){


        viewmodel.sendOtp(ReqSendOtp(
            phone = SharedPrefEnc.getPref(applicationContext,"mobile"),
            otp = ""
        ),false)

        lifecycleScope.launch {

            viewmodel.otpResponse.collect {

                when(it.status){

                    Status.SUCCESS -> {

//                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        dialog.setContentView(R.layout.layout_otp_dialouge)
                        dialog.setCancelable(true)
                        val text = dialog.findViewById<TextView>(R.id.otpresponse)
                        val edt_otp = dialog.findViewById<EditText>(R.id.edt_otp)
                        val verify = dialog.findViewById<Button>(R.id.verify)
                        text.text = it.data!!.message

                        if (viewmodel.isFinal){
                            Log.e("isFinal",""+viewmodel.isFinal)

                            if (it.data.status == "true"){

                                if (dialog.isShowing){

                                    dialog.dismiss()

                                    val intent = Intent(applicationContext, EditProfileActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                    when(SharedPrefEnc.getPref(applicationContext,"user_type")){
                                        Constant.USER_TYPE->
                                            intent.putExtra("profile",userDetails)

                                        Constant.COMPANY_TYPE->
                                            intent.putExtra("profile",companyDetails)
                                        Constant.CUSTOMER_TYPE->
                                            intent.putExtra("profile",customerDetails)

                                    }

                                    startActivity(intent)
//                                    makeEditables(binding.profileLayout.editProfileLayout,true)


                                }

                            }else{
                                text.text = it.data.message

                            }


                        }else{
                            Log.e("isFinal",""+viewmodel.isFinal)


                            verify.setOnClickListener {
                                if (edt_otp.text.isNotEmpty()){
                                    viewmodel.sendOtp(
                                        ReqSendOtp(
                                            phone = SharedPrefEnc.getPref(applicationContext,"mobile"),
                                            otp = edt_otp.text.toString()
                                        ),true
                                    )
                                }
                            }
                            dialog.show()

                        }
                        //showProgress(false,"")
                        MainActivity.binding.loader.visibility= View.GONE

//                        Log.e("Message", it.data!!.message)
                    }
                    Status.LOADING -> {
                        //showProgress(true,"Sending OTP....")
                        MainActivity.binding.loader.visibility= View.VISIBLE

                        Log.i("editButton", "Loading")
                    }
                    Status.ERROR -> {

                        Log.i("editButton", it.message.toString())
                       // showProgress(false,"")
                        MainActivity.binding.loader.visibility= View.GONE

                    }

                }

            }
        }

    }

    private fun makeEditables(layout : ViewGroup,enable : Boolean){
//        val views: MutableList<EditText> = ArrayList()
        if (enable){
            binding.profileLayout.editButton.visibility = View.GONE
            binding.profileLayout.saveButton.visibility = View.VISIBLE
        }else{
            binding.profileLayout.editButton.visibility = View.VISIBLE
            binding.profileLayout.saveButton.visibility = View.GONE
        }
        for (i in 0 until layout.childCount) {
            val v: View = layout.getChildAt(i)
            if (v is LinearLayout) {
                for (j in 0 until v.childCount) {
                    val e : View = v.getChildAt(j)
                    if (e is EditText) {
                        e.isEnabled = enable
                    }
                    if (e is RadioGroup) {
                        for (x in 0 until e.childCount) {
                            val r : View = e.getChildAt(x)
                            if (r is RadioButton) {
                                r.isEnabled = enable
                            }
                        }


                    }
                }

            }
        }
    }

    private fun setUserProfileData(ud: com.locatocam.app.data.responses.user_model.UserDetails) {

        binding.userProfileLayout.editProfileLayout.visibility = View.VISIBLE
        binding.linearShareLink.visibility=View.VISIBLE
        binding.linearMyPage.visibility=View.VISIBLE

        val docs = ud.documents
        if (!docs.isNullOrEmpty()){

            for (fl in docs)
                if ( fl.doc_name.equals("photo")) {

                    if (!fl.doc_location.isNullOrBlank() && !fl.doc_location.isNullOrEmpty()){

                        Glide.with(this)
                            .load(fl.doc_location)
                            .into(binding.image)
                    }

                }

        }
        if(ud.is_admin.equals("1") && ud.user_type.equals("user")){
            binding.userType.text="Admin"
        }
        else if (ud.is_admin.equals("0") && ud.user_type.equals("user")){
            binding.userType.text="Influncer"
        }
        else{
            binding.userType.visibility=View.GONE
        }
        binding.shareLink.setOnClickListener {
            val message: String = "https://loca-toca.com/Login/index?si="+ud.influencer_code
            val share = Intent(Intent.ACTION_SEND)
            share.type = "text/plain"
            share.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(Intent.createChooser(share, "Share"))
        }
        if (!ud.name.isNullOrEmpty()){

            binding.userProfileLayout.nameText.text = Utils.toEditable(ud.name)
            changeBackground(binding.userProfileLayout.nameText)
        }

        if (!ud.phone.isNullOrEmpty()){

            binding.userProfileLayout.phoneText.text = Utils.toEditable(ud.phone)
            changeBackground(binding.userProfileLayout.phoneText)

        }

        if (!ud.email.isNullOrEmpty()){

            binding.userProfileLayout.emailText.text = Utils.toEditable(ud.email)
            changeBackground(binding.userProfileLayout.emailText)

        }

        if (!ud.dob.isNullOrEmpty()){

            binding.userProfileLayout.dobText.text = Utils.toEditable(ud.dob)
            changeBackground(binding.userProfileLayout.dobText)

        }

        if (!ud.gender.isNullOrEmpty()){

            Log.e("gender",ud.gender)
            if (ud.gender == "male"){
                Log.e("male",ud.gender)

                binding.userProfileLayout.genderGroup.check(R.id.male)
            }else{
                binding.userProfileLayout.genderGroup.check(R.id.female)


            }

        }

        if (!ud.state_name.isNullOrEmpty()){

            binding.userProfileLayout.stateText.text = Utils.toEditable(ud.state_name)
            changeBackground(binding.userProfileLayout.stateText)

        }

        if (!ud.city_name.isNullOrEmpty()){

            binding.userProfileLayout.cityText.text = Utils.toEditable(ud.city_name)
            changeBackground(binding.userProfileLayout.cityText)

        }

        if (!ud.address.isNullOrEmpty()){

            binding.userProfileLayout.addressText.text = Utils.toEditable(ud.address)
            changeBackground(binding.userProfileLayout.addressText)

        }

        if (!ud.pincode.isNullOrEmpty()){

            binding.userProfileLayout.pinText.text = Utils.toEditable(ud.pincode)
            changeBackground(binding.userProfileLayout.pinText)

        }

        if (!ud.adhaar_no.isNullOrEmpty()){

            binding.userProfileLayout.adharText.text = Utils.toEditable(ud.adhaar_no)
            changeBackground(binding.userProfileLayout.adharText)

        }

        if (!ud.pan_no.isNullOrEmpty()){

            binding.userProfileLayout.panText.text = Utils.toEditable(ud.pan_no)
            changeBackground(binding.userProfileLayout.panText)

        }

        if (!ud.bank_name.isNullOrEmpty()){

            binding.userProfileLayout.bankNameText.text = Utils.toEditable(ud.bank_name)
            changeBackground(binding.userProfileLayout.bankNameText)

        }

        if (!ud.bank_ac_name.isNullOrEmpty()){

            binding.userProfileLayout.bankAccNameText.text = Utils.toEditable(ud.bank_ac_name)
            changeBackground(binding.userProfileLayout.bankAccNameText)

        }

        if (!ud.bank_ac_no.isNullOrEmpty()){

            binding.userProfileLayout.bankAccText.text = Utils.toEditable(ud.bank_ac_no)
            changeBackground(binding.userProfileLayout.bankAccText)

        }

        if (!ud.bank_ifsc.isNullOrEmpty()){

            binding.userProfileLayout.bankAccIfscText.text = Utils.toEditable(ud.bank_ifsc)
            changeBackground(binding.userProfileLayout.bankAccIfscText)

        }

        binding.userProfileLayout.editButton.setOnClickListener {

            sendOtp()

        }

    }
    private fun setProfileCompanyData(ud: com.locatocam.app.data.responses.company.UserDetails) {

        binding.profileLayout.editProfileLayout.visibility = View.VISIBLE
        binding.linearShareLink.visibility=View.VISIBLE
        binding.linearMyPage.visibility=View.VISIBLE
        binding.image.visibility=View.GONE
        binding.imgEdit.visibility=View.GONE

        val docs = ud.documents
        if (!docs.isNullOrEmpty()){

            for (fl in docs)
                if ( fl.doc_name.equals("photo")) {

                    if (!fl.doc_location.isNullOrBlank() && !fl.doc_location.isNullOrEmpty()){

                        Glide.with(this)
                            .load(fl.doc_location)
                            .into(binding.image)
                    }

                }

        }
        if(ud.is_admin.equals("1") && ud.user_type.equals("user")){
            binding.userType.text="Admin"
        }
        else if (ud.is_admin.equals("0") && ud.user_type.equals("user")){
            binding.userType.text="Influncer"
        }
        else{
            binding.userType.visibility=View.GONE
        }

        binding.shareLink.setOnClickListener {
            val message: String = "https://loca-toca.com/Login/index?si="+ud.influencer_code
            val share = Intent(Intent.ACTION_SEND)
            share.type = "text/plain"
            share.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(Intent.createChooser(share, "Share"))
        }
        if (!ud.name.isNullOrEmpty()){

            binding.profileLayout.nameText.text = Utils.toEditable(ud.name)
            changeBackground(binding.profileLayout.nameText)
        }

        if (!ud.phone.isNullOrEmpty()){

            binding.profileLayout.phoneText.text = Utils.toEditable(ud.phone)
            changeBackground(binding.profileLayout.phoneText)

        }

        if (!ud.email.isNullOrEmpty()){

            binding.profileLayout.emailText.text = Utils.toEditable(ud.email)
            changeBackground(binding.profileLayout.emailText)

        }

        if (!ud.dob.isNullOrEmpty()){

            binding.profileLayout.dobText.text = Utils.toEditable(ud.dob)
            changeBackground(binding.profileLayout.dobText)

        }

        if (!ud.gender.isNullOrEmpty()){

            Log.e("gender",ud.gender)
            if (ud.gender == "male"){
                Log.e("male",ud.gender)

                binding.profileLayout.genderGroup.check(R.id.male)
            }else{
                binding.profileLayout.genderGroup.check(R.id.female)


            }

        }

        if (!ud.state_name.isNullOrEmpty()){

            binding.profileLayout.stateText.text = Utils.toEditable(ud.state_name)
            changeBackground(binding.profileLayout.stateText)

        }

        if (!ud.city_name.isNullOrEmpty()){

            binding.profileLayout.cityText.text = Utils.toEditable(ud.city_name)
            changeBackground(binding.profileLayout.cityText)

        }

        if (!ud.address.isNullOrEmpty()){

            binding.profileLayout.addressText.text = Utils.toEditable(ud.address)
            changeBackground(binding.profileLayout.addressText)

        }

        if (!ud.pincode.isNullOrEmpty()){

            binding.profileLayout.pinText.text = Utils.toEditable(ud.pincode)
            changeBackground(binding.profileLayout.pinText)

        }
        binding.profileLayout.editButton.setOnClickListener {

            sendOtp()

        }

    }

    private fun setProfileData(ud: com.locatocam.app.data.responses.company.UserDetails) {

        binding.profileLayout.editProfileLayout.visibility = View.VISIBLE
        binding.userType.visibility=View.GONE
        binding.linearMyPage.visibility=View.GONE
        binding.linearShareLink.visibility=View.GONE

        val docs = ud.documents
        if (!docs.isNullOrEmpty()){

            for (fl in docs)
                if ( fl.doc_name.equals("photo")) {

                    if (!fl.doc_location.isNullOrBlank() && !fl.doc_location.isNullOrEmpty()){

                        Glide.with(this)
                            .load(fl.doc_location)
                            .into(binding.image)
                    }

                }

        }
        if(ud.is_admin.equals("1") && ud.user_type.equals("user")){
            binding.userType.text="Admin"
        }
        else if (ud.is_admin.equals("0") && ud.user_type.equals("user")){
            binding.userType.text="Influncer"
        }
        else{
            binding.userType.visibility=View.GONE
        }
        binding.shareLink.setOnClickListener {
            val message: String = "https://loca-toca.com/Login/index?si="+ud.influencer_code
            val share = Intent(Intent.ACTION_SEND)
            share.type = "text/plain"
            share.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(Intent.createChooser(share, "Share"))
        }

        if (!ud.name.isNullOrEmpty()){

            binding.profileLayout.nameText.text = Utils.toEditable(ud.name)
            changeBackground(binding.profileLayout.nameText)
        }

        if (!ud.phone.isNullOrEmpty()){

            binding.profileLayout.phoneText.text = Utils.toEditable(ud.phone)
            changeBackground(binding.profileLayout.phoneText)

        }

        if (!ud.email.isNullOrEmpty()){

            binding.profileLayout.emailText.text = Utils.toEditable(ud.email)
            changeBackground(binding.profileLayout.emailText)

        }

        if (!ud.dob.isNullOrEmpty()){

            binding.profileLayout.dobText.text = Utils.toEditable(ud.dob)
            changeBackground(binding.profileLayout.dobText)

        }

        if (!ud.gender.isNullOrEmpty()){

            Log.e("gender",ud.gender)
            if (ud.gender == "male"){
                Log.e("male",ud.gender)

                binding.profileLayout.genderGroup.check(R.id.male)
            }else{
                binding.profileLayout.genderGroup.check(R.id.female)


            }

        }

        if (!ud.state_name.isNullOrEmpty()){

            binding.profileLayout.stateText.text = Utils.toEditable(ud.state_name)
            changeBackground(binding.profileLayout.stateText)

        }

        if (!ud.city_name.isNullOrEmpty()){

            binding.profileLayout.cityText.text = Utils.toEditable(ud.city_name)
            changeBackground(binding.profileLayout.cityText)

        }

        if (!ud.address.isNullOrEmpty()){

            binding.profileLayout.addressText.text = Utils.toEditable(ud.address)
            changeBackground(binding.profileLayout.addressText)

        }

        if (!ud.pincode.isNullOrEmpty()){

            binding.profileLayout.pinText.text = Utils.toEditable(ud.pincode)
            changeBackground(binding.profileLayout.pinText)

        }
        binding.profileLayout.editButton.setOnClickListener {

            sendOtp()

        }

    }

    fun changeBackground(editText: EditText) =  editText.setBackgroundResource(R.drawable.default_rect_bg)


//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        when (resultCode) {
//            Activity.RESULT_OK -> {
//                //Image Uri will not be null for RESULT_OK
//                val fileUri = data?.data
//                binding.image.setImageURI(fileUri)
//                //You can get File object from intent
//
//
//                val file: File = ImagePicker.getFile(data)!!
//
//                //You can also get File Path from intent
//    //            mainActivityViewModel.sendImageFile(file)
//
//
//            }
//            ImagePicker.RESULT_ERROR -> {
//                Log.e("ImagePicker",ImagePicker.getError(data))
//            }
//            else -> {
//                Log.e("Cancelled","Task Cancelled")
//
//            }
//        }
//
//    }
//
//    private val startForResult =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            when(result.resultCode){
//                RESULT_OK -> {
//                    val fileUri = result.data!!.data
//                    binding.image!!.setImageURI(fileUri)
//                    //You can get File object from intent
//
//
////                    val file: File = ImagePicker.getFile(data)!!
//                }
//                RESULT_CANCELED -> {
//
//                } else -> {
//            } }
//        }

    private fun showProgress(shouldShow : Boolean,message : String){

        if (shouldShow){

            loading.setContentView(R.layout.loading_layout)
            loading.setCancelable(false)
            val title = loading.findViewById<TextView>(R.id.title)
            title.text = message
            loading.show()

        }else{
            if (loading.isShowing){
                loading.dismiss()
            }
        }


    }

    override fun recreate() {
        viewmodel.getSettingsData(SharedPrefEnc.getPref(applicationContext,"user_type"))
        manageSettingsData()
    }

    public fun hideLoader(){
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            Handler().postDelayed({
                MainActivity.binding.loader.visibility= View.GONE
            },3000)

            MainActivity.binding.bttmNav.visibility=View.VISIBLE
//            binding.orderOnline.visibility=View.VISIBLE
        }


    }

    override fun onBackPressed() {
        super.onBackPressed()
        Log.e("TAG", "hideLoader: "+MainActivity.instances.viewModel.lat+","+MainActivity.instances.viewModel.lng)
        var intent=Intent(applicationContext,MainActivity::class.java)
        intent.putExtra("lat",MainActivity.instances.viewModel.lat)
        intent.putExtra("lng",MainActivity.instances.viewModel.lng)
        intent.putExtra("address",MainActivity.instances.viewModel.add)
        startActivity(intent)
    }

}