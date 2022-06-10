package com.locatocam.app.viewmodels

import android.app.Dialog
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.AccessToken
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.locatocam.app.repositories.LoginRepository
import com.locatocam.app.views.MainActivity
import com.locatocam.app.views.login.ActivityLogin
import com.locatocam.app.views.register.ApprovalPendingActivity
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LoginViewModel(
    val repository: LoginRepository
) : ViewModel() {

    var login_status = MutableLiveData<String>()
    var login_message = ""
    var isFinalCall = false
    var dialog: Dialog? = null
    var phone: String
    var otp: String

    var otpNumber:String?=null


    private var mAuth: FirebaseAuth? = null


    init {
        phone = ""
        otp = ""
    }

    fun login(activityLogin: ActivityLogin) {
        Log.i("uname", phone)
        activityLogin.showLoader()
        viewModelScope.launch {
            repository.login(phone, otp)
                .catch {
                    activityLogin.hideLoader()
                    Log.i("uname", it.message.toString())
                    login_status.value = "error"
                }
                .collect {
                    activityLogin.hideLoader()
                    login_message = it.msg.toString()
                    login_status.value = it.status
                    Log.i("uname", it.status.toString())

                }
        }
    }
    fun addNumber(
        activity: ActivityLogin
        ) {
        viewModelScope.launch {
                repository.addNumberVerify(activity.binding.edtPhone.text.toString()).catch {
                    activity.hideLoader()
                    val snackbar = Snackbar.make(
                        activity.binding.root, ""+it.message,
                        Snackbar.LENGTH_LONG
                    )
                    snackbar.show()

                }.collect {
                    activity.hideLoader()

                    if (it.status.equals("success")) {

                        Log.e("TAG", "addNumber: "+it.otp )
                        phone= it.post.phone.toString()


                        otpNumber=it.otp.toString()

                        activity.binding.otpLayout.visibility = View.VISIBLE
                        activity.binding.addNumber.visibility = View.GONE
                        activity.binding.verifyNumber.visibility = View.VISIBLE
                        activity.binding.edtPhone.setFocusable(false)
                        activity.binding.edtPhone.isClickable=false
                    }
                    val snackbar = Snackbar.make(
                        activity.binding.root, ""+it.msg,
                        Snackbar.LENGTH_LONG
                    )
                    snackbar.show()
                }
        }
    }

    fun AddNumberverify(
        activity: ActivityLogin,
        GToken: String?,
        socialName: String?,
        socialEmail: String?,
        FToken: String?
    ) {
       /* if (phone.equals("", ignoreCase = true)) {
//            Toast.makeText(activityRegister, "Enter OTP", Toast.LENGTH_SHORT).show()
            val snackbar = Snackbar.make(
                activity.binding.root, "Enter OTP",
                Snackbar.LENGTH_LONG
            )
            snackbar.show()

        } else {*/

            if (this.otpNumber.equals(activity.binding.addNumberVerify.text.toString())) {
                activity.hideLoader()
                if (activity.addNumberVerify==true) {
                    firebaseAuthWithGoogle(activity, GToken, socialName, socialEmail)
                }
                else{
                    handleFacebookAccessToken(activity, FToken!!, socialName!!, socialEmail!!)
                }

                activity.binding.numberPopup.visibility=View.GONE
                activity.showLoader()
                Log.i("uname", "versuc")
            } else {
                val snackbar = Snackbar.make(
                    activity.binding.root, "Please enter valid OTP",
                    Snackbar.LENGTH_LONG
                )
                snackbar.show()
            }

    }



    fun verifyOtp(activityLogin: ActivityLogin) {
        activityLogin.showLoader()
        isFinalCall = true
        Log.i("uname", phone + "--" + otp)
        viewModelScope.launch {
            repository.verifyOtp(phone, otp)
                .catch {
                    activityLogin.hideLoader()
                    Log.i("uname", it.message.toString())
                    login_message = it.message.toString()
                    login_status.value = "error"
                }
                .collect {
                    activityLogin.hideLoader()
                    if (it.status.equals("success")) {
                        Log.i("uname", it.post?.phone.toString() + "----")
                        Log.e("userTtpe", it.user_details.toString())
                        repository.setUserPrefs(
                            it.post?.user_id.toString(),
                            it.post?.phone.toString(),
                            it.post?.email.toString(),
                            it.post?.name.toString(),
                            it.post?.login_type.toString(),
                            it.post?.influencer_code.toString(),
                            it.post?.is_admin.toString(),
                            it.post?.influencer_id.toString(),
                            it.post?.influencer_company.toString(),
                            it.post?.influencer_user_id.toString(),
                            it.post?.customer_id.toString()


                            )
                        login_message = it.msg.toString()
                        login_status.value = it.status
                    } else {
                        login_message = it.msg.toString()
                        login_status.value = it.status
                    }

                }
        }
    }



    fun socialLogin(name: String?, email: String?, phone: String?, activity: ActivityLogin) {
        viewModelScope.launch {
            repository.socialLogin(name!!, email!!, phone!!)
                .catch {
                    activity.hideLoader()
                    Log.i("uname", it.message.toString())
                    login_status.value = "error"
                    Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show()
                }
                .collect {
                    activity.hideLoader()
                    login_message = it.msg.toString()

                    if (it.approval_pending == "1") {
                        var intent = Intent(activity, ApprovalPendingActivity::class.java)
                        activity.startActivity(intent)
                    } else {

                        if (it.status.equals("success")) {
                            Log.i("uname", it.post?.phone.toString() + "----")


                            login_status.value = it.status
                            repository.setUserPrefs(
                                it.post?.user_id.toString(),
                                it.post?.phone.toString(),
                                it.post?.email.toString(),
                                it.post?.name.toString(),
                                it.post?.login_type.toString(),
                                it.post?.influencer_code.toString(),
                                it.post?.is_admin.toString(),
                                it.post?.influencer_id.toString(),
                                it.post?.influencer_company.toString(),
                                it.post?.influencer_user_id.toString(),
                                it.post?.customer_id.toString()
                            )
                            var intent = Intent(activity, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            activity.startActivity(intent)

                        } else {
                            login_status.value = it.status
                        }
                    }

                }
        }
    }

    fun firebaseAuthWithGoogle(
        activity: ActivityLogin,
        idToken: String?,
        name: String?,
        email: String?
    ) {

       /* if (phone.equals("", ignoreCase = true)) {
            activity.hideLoader()
            activity.binding.numberPopup.visibility = View.VISIBLE
            activity.binding.addNumber.setOnClickListener {
                phone = activity.binding.edtPhone.text.toString()
                if (phone.equals("", ignoreCase = true)) {
                    val snackbar = Snackbar.make(
                        activity.binding.root, "Enter Number",
                        Snackbar.LENGTH_LONG
                    )
                    snackbar.show()
                } else {
//                    activity.binding.numberPopup.visibility = View.GONE
                    activity.showLoader()
                    addNumberVerify=true
                    GidToken=idToken
                    Gname=name
                    Gemail=email
                    addNumber(activity)


                }

            }

        } else {*/
            mAuth = FirebaseAuth.getInstance()
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            mAuth?.signInWithCredential(credential)?.addOnCompleteListener {


                try {
                    if (it.isSuccessful()) {
                        socialLogin(name, email, phone, activity)
                    } else {
                        activity.hideLoader()
                        val snackbar = Snackbar.make(
                            activity.binding.root, "Login Fail",
                            Snackbar.LENGTH_LONG
                        )
                        snackbar.show()
                    }
                } catch (e: Exception) {
                    activity.hideLoader()
                    val snackbar = Snackbar.make(
                        activity.binding.root, "" + e.message.toString(),
                        Snackbar.LENGTH_LONG
                    )
                    snackbar.show()
                }
            }
//        }
    }

    fun handleFacebookAccessToken(
        activity: ActivityLogin,
        token: String,
        name: String,
        email: String

    ) {

       /* if (phone.equals("", ignoreCase = true)) {
            activity.hideLoader()
            activity.binding.numberPopup.visibility = View.VISIBLE
            activity.binding.addNumber.setOnClickListener {
                phone = activity.binding.edtPhone.text.toString()
                if (phone.equals("", ignoreCase = true)) {
                    val snackbar = Snackbar.make(
                        activity.binding.root, "Enter Number",
                        Snackbar.LENGTH_LONG
                    )
                    snackbar.show()
                } else {
                    activity.showLoader()
                    addNumberVerify=false
                    fToken=token
                    Gemail=email
                    Gname=name
//                    handleFacebookAccessToken(activity, token, email, name)
                    addNumber(activity)
                *//*    activity.binding.numberPopup.visibility = View.GONE
                    activity.showLoader()*//*
                }
            }

        } else {*/
            mAuth = FirebaseAuth.getInstance()
            val credential = FacebookAuthProvider.getCredential(token)
            mAuth?.signInWithCredential(credential)?.addOnCompleteListener {


                try {
                    if (it.isSuccessful()) {
                        /* val snackbar = Snackbar.make(activity.binding.root, "Login Success",
                             Snackbar.LENGTH_LONG)
                         snackbar.show()*/
                        socialLogin(name, email, phone, activity)
                    } else {
                        activity.hideLoader()
                        val snackbar = Snackbar.make(
                            activity.binding.root, "Login Fail",
                            Snackbar.LENGTH_LONG
                        )
                        snackbar.show()
                    }
                } catch (e: Exception) {
                    activity.hideLoader()
                    val snackbar = Snackbar.make(
                        activity.binding.root, "" + e.message.toString(),
                        Snackbar.LENGTH_LONG
                    )
                    snackbar.show()
                }
            }

    }
}