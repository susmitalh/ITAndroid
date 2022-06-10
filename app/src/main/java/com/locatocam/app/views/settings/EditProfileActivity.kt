package com.locatocam.app.views.settings

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.datepicker.MaterialDatePicker
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.locatocam.app.R
import com.locatocam.app.data.requests.ReqCity
import com.locatocam.app.data.requests.reqUserProfile.ReqProfileData
import com.locatocam.app.data.requests.reqUserProfile.SocialDetail
import com.locatocam.app.data.responses.DataXX
import com.locatocam.app.data.responses.DataXXX
import com.locatocam.app.data.responses.user_model.Document
import com.locatocam.app.databinding.ActivityEditProfileBinding
import com.locatocam.app.network.DocumentMapper
import com.locatocam.app.network.Status
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.utils.Constant
import com.locatocam.app.utils.Utils
import com.locatocam.app.viewmodels.SettingsViewModel
import com.locatocam.app.views.settings.adapters.DocumentAdapter
import com.locatocam.app.views.settings.adapters.SocialDetailsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


@AndroidEntryPoint
class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityEditProfileBinding
    @Inject
    lateinit var documentMapper: DocumentMapper
    lateinit var viewmodel: SettingsViewModel
    var statesList: List<DataXX> = arrayListOf()
    var cityList: List<DataXXX> = arrayListOf()
    var socialDetails: MutableList<SocialDetail> = ArrayList()
    lateinit var dialog : Dialog

//    var states : Array<String> = arrayOf()

    val states: MutableList<String> = ArrayList()
    private val cities: MutableList<String> = ArrayList()
    var stateID : String = ""
    var citiID : String = ""
    var gender : String = ""
    var docName : String = ""
    lateinit var fileUri : Uri
    val startForImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode

            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                fileUri = data?.data!!

                viewmodel.uploadDocument(SharedPrefEnc.getPref(this,"user_id"),docName, File(
                    Utils.getActualPath(fileUri,this)
                )
                )


            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Log.e("ImagePicker", ImagePicker.getError(data))
            } else {
                Log.e("ImagePicker","cancelled")
            }
        }
//    var stateArray : List<String> = arrayListOf("ka","kl","tn","ts","pb","dl")


   private fun uploadDocument(document: Document, documentView: ImageView){

       this.docName = document.doc_name!!

       Dexter.withContext(this)
           .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
           .withListener(object : PermissionListener {
               override fun onPermissionGranted(p0: PermissionGrantedResponse?) {

                   ImagePicker.with(this@EditProfileActivity)

//                            .compress(1024)         //Final image size will be less than 1 MB(Optional)
//                            .maxResultSize(1080, 1080)  //Final image resolution will be less than 1080 x 1080(Optional)
                       .galleryOnly()
                       .createIntent { intent ->

                           startForImageResult.launch(intent)
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

       observeImageUpload(document,documentView)

   }


    private fun observeImageUpload(document: Document, documentView: ImageView) {

        lifecycleScope.launch {

            viewmodel.docUploadResponse.collect {

                when (it.status) {

                    Status.SUCCESS -> {

                        showProgress(false, "")

                        Log.e("image", it.data!!.data)
                        document.doc_location = it.data?.data
//                        Glide.with(this@EditProfileActivity)
//                            .load(Uri.parse(it.data?.data))
//                            .into(documentView)

                      documentView.setImageURI(fileUri)

                    }
                    Status.LOADING -> {
                        showProgress(true, "Uploading Image...")
                    }
                    Status.ERROR -> {
                        showProgress(false, "")

                    }

                }

            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewmodel= ViewModelProvider(this).get(SettingsViewModel::class.java)

        dialog = Dialog(this)

        when(SharedPrefEnc.getPref(this,"user_type")){

            Constant.USER_TYPE->
                loadProfileData(intent.extras!!.get("profile") as com.locatocam.app.data.responses.user_model.UserDetails)
            Constant.COMPANY_TYPE->
                loadProfileData(intent.extras!!.get("profile") as com.locatocam.app.data.responses.company.UserDetails)
            Constant.CUSTOMER_TYPE->
                loadProfileData(intent.extras!!.get("profile") as com.locatocam.app.data.responses.customer_model.UserDetails)


        }

        viewmodel.getStates()

        if (stateID.isNotEmpty()){
            getCities(stateID)
        }

        lifecycleScope.launch {

            viewmodel.stateList.collect {

                when(it.status){

                    Status.SUCCESS -> {
                        showProgress(false,"")


                        Log.e("stateList", it.data!!.data[0].name)
                        statesList = it.data.data

                        for (state in statesList){

                            states.add(state.name)

                        }
                    }
                    Status.LOADING -> {
                        showProgress(true,"Fetching Data")


                        Log.e("stateList", "Loading")
                    }
                    Status.ERROR -> {
                        showProgress(false,"")


                        Log.e("stateList", it.message.toString())
                    }

                }

            }
        }

        viewmodel.launchDocPicker.observe(this, Observer {

                uploadDocument(it.document,it.documentAdapter)

        })

    }


    private fun loadProfileData(ud: com.locatocam.app.data.responses.user_model.UserDetails){

        binding.editUserProfile.editProfileLayout.visibility = View.VISIBLE
        binding.editUserProfile.saveButton.visibility = View.VISIBLE
        stateID = ud.state_id
        citiID = ud.city_id!!

        if (!ud.name.isNullOrEmpty()){

            binding.editUserProfile.nameText.text = Utils.toEditable(ud.name)
//            changeBackground(binding.editUserProfile.nameText)
        }



        if (!ud.phone.isNullOrEmpty()){

            binding.editUserProfile.phoneText.text = Utils.toEditable(ud.phone)
//            changeBackground(binding.editUserProfile.phoneText)

        }

        binding.editUserProfile.phoneText.setOnFocusChangeListener { view, b ->

            if (!b){
                if ( !Utils.isPhoneLengthMatches(binding.editUserProfile.phoneText.text.toString())){
                    binding.editUserProfile.phoneText.error = "Enter 10 digit phone number"
                }
            }

        }



        if (!ud.email.isNullOrEmpty()){

            binding.editUserProfile.emailText.text = Utils.toEditable(ud.email)
//            changeBackground(binding.userProfileLayout.emailText)

        }

        binding.editUserProfile.emailText.setOnFocusChangeListener { view, b ->

            if (!b){
                if ( !Utils.isEmailValid(binding.editUserProfile.emailText.text.toString())){
                    binding.editUserProfile.emailText.error = "Enter a valid Email address"
                }
            }

        }

        if (!ud.dob.isNullOrEmpty()){

            binding.editUserProfile.dobText.text = Utils.toEditable(ud.dob)
//            changeBackground(binding.userProfileLayout.dobText)

        }

        binding.editUserProfile.dobText.setOnClickListener {

            val datePicker: MaterialDatePicker<Long> = MaterialDatePicker
                .Builder
                .datePicker()
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setTitleText("Select date of birth")
                .build()
            datePicker.show(supportFragmentManager, "DATE_PICKER")

            datePicker.addOnPositiveButtonClickListener {
                val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                val date = sdf.format(it)

                binding.editUserProfile.dobText.text = Utils.toEditable(date)
            }



        }


        if (!ud.gender.isNullOrEmpty()){

            Log.e("gender",ud.gender)
            if (ud.gender == "male"){
                Log.e("male",ud.gender)

                binding.editUserProfile.genderGroup.check(R.id.male)
            }else{
                binding.editUserProfile.genderGroup.check(R.id.female)


            }

        }
        binding.editUserProfile.genderGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->

            when(checkedId){

                R.id.male-> gender = "male"
                R.id.female-> gender = "female"

            }

        })

        if (!ud.state_name.isNullOrEmpty()){

            binding.editUserProfile.stateText.text = Utils.toEditable(ud.state_name)
//            changeBackground(binding.userProfileLayout.stateText)

        }

        if (!ud.city_name.isNullOrEmpty()){

            binding.editUserProfile.cityText.text = Utils.toEditable(ud.city_name)
//            changeBackground(binding.userProfileLayout.cityText)

        }


        val adapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, states)


        binding.editUserProfile.stateText.setAdapter(adapter)

        binding.editUserProfile.stateText.setOnClickListener {
            binding.editUserProfile.stateText.threshold = 1
            binding.editUserProfile.stateText.showDropDown()

        }



        binding.editUserProfile.stateText.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->


            val state = parent.getItemAtPosition(position).toString()
            Log.e("state",state)

            stateID = statesList.find { it.name == state }!!.id
            Log.e("stateName",states[position])
            Log.e("position",position.toString())
            cities.clear()
            cityList = emptyList()
            getCities(stateID)

            binding.editUserProfile.cityText.text = null

            val cityAdapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_1, cities)


            binding.editUserProfile.cityText.setAdapter(cityAdapter)

            binding.editUserProfile.cityText.setOnClickListener {
                binding.editUserProfile.cityText.threshold = 1
                binding.editUserProfile.cityText.showDropDown()

            }


        })


        binding.editUserProfile.cityText.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->

            val city = parent.getItemAtPosition(position).toString()

            citiID = cityList.find { it.name == city }!!.id


        })

//        binding.editUserProfile.cityText.setOnItemClickListener { adapterView, view, i, l ->
//
//            Log.e("cities",cities[i])
//            citiID = cityList[i].id
//
//        }


        if (!ud.address.isNullOrEmpty()){

            binding.editUserProfile.addressText.text = Utils.toEditable(ud.address)
//            changeBackground(binding.userProfileLayout.addressText)

        }

        if (!ud.pincode.isNullOrEmpty()){

            binding.editUserProfile.pinText.text = Utils.toEditable(ud.pincode)
//            changeBackground(binding.userProfileLayout.pinText)

        }

        binding.editUserProfile.pinText.setOnFocusChangeListener { view, b ->

            if (!b){
                if ( !Utils.isPinLengthMatches(binding.editUserProfile.pinText.text.toString())){
                    binding.editUserProfile.pinText.error = "Enter a valid Pincode"
                }
            }

        }

        if (!ud.adhaar_no.isNullOrEmpty()){

            binding.editUserProfile.adharText.text = Utils.toEditable(ud.adhaar_no)
//            changeBackground(binding.userProfileLayout.adharText)

        }

        if (!ud.pan_no.isNullOrEmpty()){

            binding.editUserProfile.panText.text = Utils.toEditable(ud.pan_no)
//            changeBackground(binding.userProfileLayout.panText)

        }

        if (!ud.bank_name.isNullOrEmpty()){

            binding.editUserProfile.bankNameText.text = Utils.toEditable(ud.bank_name)
//            changeBackground(binding.userProfileLayout.bankNameText)

        }

        if (!ud.bank_ac_name.isNullOrEmpty()){

            binding.editUserProfile.bankAccNameText.text = Utils.toEditable(ud.bank_ac_name)
//            changeBackground(binding.userProfileLayout.bankAccNameText)

        }

        if (!ud.bank_ac_no.isNullOrEmpty()){

            binding.editUserProfile.bankAccText.text = Utils.toEditable(ud.bank_ac_no)
//            changeBackground(binding.userProfileLayout.bankAccText)

        }

        if (!ud.bank_ifsc.isNullOrEmpty()){

            binding.editUserProfile.bankAccIfscText.text = Utils.toEditable(ud.bank_ifsc)
//            changeBackground(binding.userProfileLayout.bankAccIfscText)

        }

        binding.editUserProfile.socialList.layoutManager = LinearLayoutManager(this)
        binding.editUserProfile.socialList.itemAnimator = DefaultItemAnimator()
        val socialDataadapter =  SocialDetailsAdapter(ud.social_details,this,viewmodel)
        binding.editUserProfile.socialList.adapter = socialDataadapter


        binding.editUserProfile.docList.layoutManager = LinearLayoutManager(this)
        binding.editUserProfile.docList.itemAnimator = DefaultItemAnimator()
        val docAdapter =  DocumentAdapter(ud.documents,this,viewmodel)
        binding.editUserProfile.docList.adapter = docAdapter



        viewmodel.socialDetails.observe(this, Observer {
            socialDetails.clear()
            socialDetails.addAll(it)
        })

        binding.editUserProfile.saveButton.setOnClickListener {
            showSaveAlert(ud.bank_branch)
        }


    }

    private fun getCities(id: String) {

        viewmodel.getCities(ReqCity(
            state_id = id
        ))

        Log.e("stateID",stateID)

        lifecycleScope.launch {

            viewmodel.cityList.collect {

                when(it.status){

                    Status.SUCCESS -> {

                        showProgress(false,"")


                        cityList = it.data!!.data

                        if (cities.isNotEmpty()){
                            cities.clear()
                        }

                        for (city in cityList){

                            cities.add(city.name)

                        }
                    }
                    Status.LOADING -> {
                        showProgress(true,"Fetching Data")

                        Log.e("cityList", "Loading")
                    }
                    Status.ERROR -> {
                        showProgress(false,"")


                        Log.e("cityList", it.message.toString())
                    }

                }

            }

        }


    }


    private fun loadProfileData(ud: com.locatocam.app.data.responses.company.UserDetails){

        binding.editCustomerProfile.editProfileLayout.visibility = View.VISIBLE
        binding.editCustomerProfile.saveButton.visibility = View.VISIBLE
        stateID = ud.state_id!!
        citiID = ud.city_id!!
        if (!ud.name.isNullOrEmpty()){

            binding.editCustomerProfile.nameText.text = Utils.toEditable(ud.name)
//            changeBackground(binding.editUserProfile.nameText)
        }

        if (!ud.phone.isNullOrEmpty()){

            binding.editCustomerProfile.phoneText.text = Utils.toEditable(ud.phone)
//            changeBackground(binding.editUserProfile.phoneText)

        }


        binding.editCustomerProfile.phoneText.setOnFocusChangeListener { view, b ->

            if (!b){
                if ( !Utils.isPhoneLengthMatches(binding.editCustomerProfile.phoneText.text.toString())){
                    binding.editCustomerProfile.phoneText.error = "Enter 10 digit phone number"
                }
            }

        }



        if (!ud.email.isNullOrEmpty()){

            binding.editCustomerProfile.emailText.text = Utils.toEditable(ud.email)
//            changeBackground(binding.userProfileLayout.emailText)

        }

        binding.editCustomerProfile.emailText.setOnFocusChangeListener { view, b ->

            if (!b){
                if ( !Utils.isEmailValid(binding.editCustomerProfile.emailText.text.toString())){
                    binding.editCustomerProfile.emailText.error = "Enter a valid Email address"
                }
            }

        }


        if (!ud.dob.isNullOrEmpty()){

            binding.editCustomerProfile.dobText.text = Utils.toEditable(ud.dob)
//            changeBackground(binding.userProfileLayout.dobText)

        }

        binding.editCustomerProfile.dobText.setOnClickListener {

            val datePicker: MaterialDatePicker<Long> = MaterialDatePicker
                .Builder
                .datePicker()
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setTitleText("Select date of birth")
                .build()
            datePicker.show(supportFragmentManager, "DATE_PICKER")

            datePicker.addOnPositiveButtonClickListener {
                val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                val date = sdf.format(it)

                binding.editCustomerProfile.dobText.text = Utils.toEditable(date)
            }



        }


        if (!ud.gender.isNullOrEmpty()){

            Log.e("gender",ud.gender)
            if (ud.gender == "male"){
                Log.e("male",ud.gender)

                binding.editCustomerProfile.genderGroup.check(R.id.male)
            }else{
                binding.editCustomerProfile.genderGroup.check(R.id.female)


            }

        }

        binding.editCustomerProfile.genderGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->

            when(checkedId){

                R.id.male-> gender = "male"
                R.id.female-> gender = "female"

            }

        })

        if (!ud.state_name.isNullOrEmpty()){

            binding.editCustomerProfile.stateText.text = Utils.toEditable(ud.state_name)
//            changeBackground(binding.userProfileLayout.stateText)

        }

        if (!ud.city_name.isNullOrEmpty()){

            binding.editCustomerProfile.cityText.text = Utils.toEditable(ud.city_name)
//            changeBackground(binding.userProfileLayout.cityText)

        }

        if (!ud.address.isNullOrEmpty()){

            binding.editCustomerProfile.addressText.text = Utils.toEditable(ud.address)
//            changeBackground(binding.userProfileLayout.addressText)

        }

        if (!ud.pincode.isNullOrEmpty()){

            binding.editCustomerProfile.pinText.text = Utils.toEditable(ud.pincode)
//            changeBackground(binding.userProfileLayout.pinText)

        }

        binding.editCustomerProfile.pinText.setOnFocusChangeListener { view, b ->

            if (!b){
                if ( !Utils.isPinLengthMatches(binding.editCustomerProfile.pinText.text.toString())){
                    binding.editCustomerProfile.pinText.error = "Enter a valid Pincode"
                }
            }

        }

        setStateListListener()

        binding.editCustomerProfile.saveButton.setOnClickListener {
            showSaveAlert("BLR")
        }


    }

    private fun setStateListListener() {

        Log.e("stateID",stateID)
        val adapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, states)


        binding.editCustomerProfile.stateText.setAdapter(adapter)

        binding.editCustomerProfile.stateText.setOnClickListener {
            binding.editCustomerProfile.stateText.threshold = 1
            binding.editCustomerProfile.stateText.showDropDown()

        }

        binding.editCustomerProfile.stateText.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->


            val state = parent.getItemAtPosition(position).toString()
            Log.e("state",state)

            stateID = statesList.find { it.name == state }!!.id
            Log.e("stateName",states[position])
            Log.e("position",position.toString())
            cities.clear()
            cityList = emptyList()
            getCities(stateID)

            binding.editCustomerProfile.cityText.text = null

            val cityAdapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_1, cities)


            binding.editCustomerProfile.cityText.setAdapter(cityAdapter)

            binding.editCustomerProfile.cityText.setOnClickListener {
                binding.editCustomerProfile.cityText.threshold = 1
                binding.editCustomerProfile.cityText.showDropDown()

            }


        })



        binding.editCustomerProfile.cityText.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->

            val city = parent.getItemAtPosition(position).toString()

            citiID = cityList.find { it.name == city }!!.id


        })

//        binding.editCustomerProfile.cityText.setOnItemClickListener { adapterView, view, i, l ->
//
//            Log.e("cities",cities[adapterView.selectedItemPosition])
//            citiID = cityList[i].id
//
//        }

    }


    private fun loadProfileData(ud: com.locatocam.app.data.responses.customer_model.UserDetails){

        binding.editCustomerProfile.editProfileLayout.visibility = View.VISIBLE
        binding.editCustomerProfile.saveButton.visibility = View.VISIBLE
        stateID = ud.state_id!!
        citiID = ud.city_id!!




        if (!ud.name.isNullOrEmpty()){

            binding.editCustomerProfile.nameText.text = Utils.toEditable(ud.name)
//            changeBackground(binding.editUserProfile.nameText)
        }

        if (!ud.phone.isNullOrEmpty()){

            binding.editCustomerProfile.phoneText.text = Utils.toEditable(ud.phone)
//            changeBackground(binding.editUserProfile.phoneText)

        }

        binding.editCustomerProfile.phoneText.setOnFocusChangeListener { view, b ->

            if (!b){
                if ( !Utils.isPhoneLengthMatches(binding.editCustomerProfile.phoneText.text.toString())){
                    binding.editCustomerProfile.phoneText.error = "Enter 10 digit phone number"
                }
            }

        }



        if (!ud.email.isNullOrEmpty()){

            binding.editCustomerProfile.emailText.text = Utils.toEditable(ud.email)
//            changeBackground(binding.userProfileLayout.emailText)

        }

        binding.editCustomerProfile.emailText.setOnFocusChangeListener { view, b ->

            if (!b){
                if ( !Utils.isEmailValid(binding.editCustomerProfile.emailText.text.toString())){
                    binding.editCustomerProfile.emailText.error = "Enter a valid Email address"
                }
            }

        }


        if (!ud.dob.isNullOrEmpty()){

            binding.editCustomerProfile.dobText.text = Utils.toEditable(ud.dob)
//            changeBackground(binding.userProfileLayout.dobText)

        }

        binding.editCustomerProfile.dobText.setOnClickListener {

            val datePicker: MaterialDatePicker<Long> = MaterialDatePicker
                .Builder
                .datePicker()
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setTitleText("Select date of birth")
                .build()

            datePicker.show(supportFragmentManager, "DATE_PICKER")
            datePicker.addOnPositiveButtonClickListener {
                val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                val date = sdf.format(it)

                binding.editCustomerProfile.dobText.text = Utils.toEditable(date)
            }



        }

        if (!ud.gender.isNullOrEmpty()){

            Log.e("gender",ud.gender)
            if (ud.gender == "male"){
                Log.e("male",ud.gender)

                binding.editCustomerProfile.male.isChecked = true
            }else{
                binding.editCustomerProfile.female.isChecked = true


            }

        }

        binding.editCustomerProfile.genderGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->

            when(checkedId){

                R.id.male-> gender = "male"
                R.id.female-> gender = "female"

            }

        })

        if (!ud.state_name.isNullOrEmpty()){

            binding.editCustomerProfile.stateText.text = Utils.toEditable(ud.state_name)
//            changeBackground(binding.userProfileLayout.stateText)

        }

        if (!ud.city_name.isNullOrEmpty()){

            binding.editCustomerProfile.cityText.text = Utils.toEditable(ud.city_name)
//            changeBackground(binding.userProfileLayout.cityText)

        }

        if (!ud.address.isNullOrEmpty()){

            binding.editCustomerProfile.addressText.text = Utils.toEditable(ud.address)
//            changeBackground(binding.userProfileLayout.addressText)

        }

        if (!ud.pincode.isNullOrEmpty()){

            binding.editCustomerProfile.pinText.text = Utils.toEditable(ud.pincode)
//            changeBackground(binding.userProfileLayout.pinText)

        }

        binding.editCustomerProfile.pinText.setOnFocusChangeListener { view, b ->

            if (!b){
                if ( !Utils.isPinLengthMatches(binding.editCustomerProfile.pinText.text.toString())){
                    binding.editCustomerProfile.pinText.error = "Enter a valid Pincode"
                }
            }

        }

        setStateListListener()

        binding.editCustomerProfile.saveButton.setOnClickListener {
            showSaveAlert("BLR")
        }

    }

    private fun saveUserProfile(bank_branch : String) {



        when(SharedPrefEnc.getPref(this,"user_type")){

            Constant.USER_TYPE->
                viewmodel.editProfile(getRequestData(bank_branch))
            Constant.COMPANY_TYPE->
                viewmodel.editProfile(getRequestCustomerData(bank_branch))
            Constant.CUSTOMER_TYPE->
                viewmodel.editProfile(getRequestCustomerData(bank_branch))

//            Log.e("profileData",getRequestCustomerData(bank_branch).toString())
//

        }

        lifecycleScope.launch {

            viewmodel.editProfileResponse.collect {

                when(it.status){

                    Status.SUCCESS -> {

                        showProgress(false,"")


//                       Toast.makeText(applicationContext,it.message,Toast.LENGTH_LONG).show()
//                        val intent = Intent(applicationContext,MainActivity::class.java)

                        finish()

//                        startActivity(intent)

                    }
                    Status.LOADING -> {
                        showProgress(true,"Updating YourProfile...")

                        Log.e("cityList", "Loading")
                    }
                    Status.ERROR -> {
                        showProgress(false,"")
                        Log.e("cityList", it.message.toString())
                        when(SharedPrefEnc.getPref(applicationContext,"user_type")){

                            Constant.USER_TYPE->
                                binding.editUserProfile.messageText.text = it.message
                            Constant.COMPANY_TYPE->
                                binding.editCustomerProfile.messageText.text = it.message
                            Constant.CUSTOMER_TYPE->
                                binding.editCustomerProfile.messageText.text = it.message
                        }
                    }


                }
            }

        }





    }

    private fun getRequestData(bank_branch : String) : ReqProfileData{

        return ReqProfileData(

            name = binding.editUserProfile.nameText.text.toString(),
            address = binding.editUserProfile.addressText.text.toString(),
            adhaar_no = binding.editUserProfile.adharText.text.toString(),
            bank_ac_name = binding.editUserProfile.bankAccNameText.text.toString(),
            bank_ac_no = binding.editUserProfile.bankAccText.text.toString(),
            bank_branch = bank_branch,
            bank_ifsc = binding.editUserProfile.bankAccIfscText.text.toString(),
            bank_name = binding.editUserProfile.bankNameText.text.toString(),
            dob = binding.editUserProfile.dobText.text.toString(),
            city = citiID,
            email = binding.editUserProfile.emailText.text.toString(),
            gender = gender,
            pan_no = binding.editUserProfile.panText.text.toString(),
            phone = binding.editUserProfile.phoneText.text.toString(),
            pincode = binding.editUserProfile.pinText.text.toString(),
            social_details = socialDetails,
            state = stateID,
            user_id = SharedPrefEnc.getPref(this,"user_id").toInt(),


            )

    }

    private fun getRequestCustomerData(bank_branch : String) : ReqProfileData{

        return ReqProfileData(

            name = binding.editCustomerProfile.nameText.text.toString(),
            address = binding.editCustomerProfile.addressText.text.toString(),
            adhaar_no = "",
            bank_ac_name = "",
            bank_ac_no = "",
            bank_branch = bank_branch,
            bank_ifsc = "",
            bank_name = "",
            dob = binding.editCustomerProfile.dobText.text.toString(),
            city = citiID,
            email = binding.editCustomerProfile.emailText.text.toString(),
            gender = gender,
            pan_no = "",
            phone = binding.editCustomerProfile.phoneText.text.toString(),
            pincode = binding.editCustomerProfile.pinText.text.toString(),
            social_details = socialDetails,
            state = stateID,
            user_id = SharedPrefEnc.getPref(this,"user_id").toInt(),


            )

    }

    private fun showProgress(shouldShow : Boolean,message : String){

        if (shouldShow){

            dialog.setContentView(R.layout.loading_layout)
            dialog.setCancelable(false)
            val title = dialog.findViewById<TextView>(R.id.title)
            title.text = message
            dialog.show()

        }else{
            if (dialog.isShowing){
                dialog.dismiss()
            }
        }


    }


    private fun showSaveAlert(bankBranch: String) {

        val alertDialogBuilder = AlertDialog.Builder(this)

        alertDialogBuilder.setTitle("Do you want to save the changes?")
        alertDialogBuilder.setPositiveButton("Save", DialogInterface.OnClickListener { dialogInterface, i ->

            saveUserProfile(bankBranch)

        })
        alertDialogBuilder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->
            dialogInterface.dismiss()
        })
        alertDialogBuilder.show()

    }


}