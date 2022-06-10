package com.locatocam.app.viewmodels

import android.app.Dialog
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.locatocam.app.R
import com.locatocam.app.repositories.LoginRepository
import com.locatocam.app.views.MainActivity
import com.locatocam.app.views.register.ActivityRegister
import com.locatocam.app.views.register.ApprovalPendingActivity
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import pl.droidsonroids.gif.GifImageView

class RegisterViewModel(
    val repository: LoginRepository
) : ViewModel() {
    var dialog: Dialog? = null

    var login_status = MutableLiveData<String>()
    var login_message = ""
    var isVerifyCall = false
    var name = ""
    var email = ""
    var phone = ""
    var influencer_code = ""
    var post_id = ""
    var otp = ""
    var otp_test = ""

    init {

    }

    fun register(activityRegister: ActivityRegister, otpPopup: RelativeLayout) {
        showLoader(activityRegister)
        viewModelScope.launch {
            repository.register(
                name,
                email,
                phone,
                influencer_code,
                post_id,
                otp
            )
                .catch {
                    hideLoader()
                    Log.i("uname", it.message.toString())
                    Log.e("TAG", "registerfail: " + it.message.toString())
                    login_status.value = "error"
                }
                .collect {
                    hideLoader()
                    var a = it.approval_pending
                    login_message = it.msg.toString()
                    otp_test = it.otp.toString()
                    Log.e("TAG", "registerotp: " + it.otp.toString())
                    Log.e("TAG", "registerotp: " + a)
                    Log.i("uname", it.otp.toString())
                    /*repository.setUserPrefs(
                        it.post?.phone.toString(),
                        "0",
                        "0",
                        it.user_details?.user_type.toString()
                    )*/
                    if (it.approval_pending == "1") {
                        var intent = Intent(activityRegister, ApprovalPendingActivity::class.java)
                        activityRegister.startActivity(intent)
                    } else {
                        if (it.status.toString() == "success") {
                            /*showDialog(
                            activityRegister,
                            it.post?.phone.toString(),
                            it.user_details?.user_type.toString()

                        )*/
                            otpPopup.visibility = View.VISIBLE
                        } else {
                            login_status.value = it.status
                        }
                    }

                    Log.i("uname", it.status.toString())
                }
        }
    }

    /* private fun showDialog(
         activityRegister: ActivityRegister,
         phone: String,
         userType: String
     ) {
         val dialog = Dialog(activityRegister)
         dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
         dialog.setCancelable(false)
         dialog.setContentView(R.layout.layout_otp_dialouge)
         val edt_otp = dialog.findViewById(R.id.edt_otp) as EditText

         val verify = dialog.findViewById(R.id.verify) as Button
         val resend_otp = dialog.findViewById(R.id.resend_otp) as TextView
         val img_close = dialog.findViewById(R.id.img_close) as ImageButton

         resend_otp.setOnClickListener {

         }
         img_close.setOnClickListener {
             dialog.dismiss()
         }
         verify.setOnClickListener {

             var otp_verify = edt_otp.text.toString()

 //          verify(activityRegister,otp_verify,phone,userType)

         }
         resend_otp.setOnClickListener {
             var otp_verify = edt_otp.text.toString()


         }
         dialog.show()

     }*/
    fun reSend(activityRegister: ActivityRegister, otpVerify: String, otpPopup: RelativeLayout) {
        showLoader(activityRegister)
        viewModelScope.launch {
            repository.register(
                name,
                email,
                phone,
                influencer_code,
                post_id,
                otp
            )
                .catch {
                    hideLoader()
                    Log.i("uname", it.message.toString())
                    login_status.value = "error"
                }
                .collect {
                    hideLoader()
                    login_message = it.msg.toString()
//                    login_status.value = it.status
                    otp_test = it.otp.toString()


                    if (it.status.equals("success")) {
                        verify(activityRegister, otpVerify, otpPopup)
                        Toast.makeText(activityRegister, "Code Resend", Toast.LENGTH_SHORT).show()
                    } else {
                        login_status.value = it.status
                    }

                    Log.i("uname", it.status.toString())
                }
        }
    }

    fun callregisterApi(activityRegister: ActivityRegister) {
        showLoader(activityRegister)
        viewModelScope.launch {
            repository.register(
                name,
                email,
                phone,
                influencer_code,
                post_id,
                otp_test
            )
                .catch {
                    hideLoader()
                    Log.i("uname", it.message.toString())
                    login_status.value = "error"
                }
                .collect {
                    hideLoader()


                    login_message = it.msg.toString()
//                        login_status.value = "success"

                    if (it.status == "success") {
                        login_status.value = it.status
                        repository.setUserPrefs(
                            "0",
                            it.post?.phone.toString(),
                            it.post?.email.toString(),
                            it.post?.name.toString(),
                            "0",
                            "0",
                            "",
                            "0",
                            "",
                            "0",
                            "0"
                        )

                        var intent = Intent(activityRegister, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        activityRegister.startActivity(intent)
                    } else {
                        login_status.value = it.status
                    }

                }
        }
    }

    fun verify(
        activityRegister: ActivityRegister,
        otpVerify: String,
        otpPopup: RelativeLayout,
    ) {
        if (otpVerify.equals("", ignoreCase = true)) {
//            Toast.makeText(activityRegister, "Enter OTP", Toast.LENGTH_SHORT).show()
            val snackbar = Snackbar.make(
                activityRegister.binding.root, "Enter OTP",
                Snackbar.LENGTH_LONG
            )
            snackbar.show()

        } else {

            if (this.otp_test.equals(otpVerify)) {
//                isVerifyCall = true
                /* repository.setUserPrefs(
                     phone,
                     "0",
                     "0",
                     userType
                 )*/
                /*   var intent = Intent(activityRegister, MainActivity::class.java)
                 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                 activityRegister.startActivity(intent)*/
                callregisterApi(activityRegister)
                otpPopup.visibility == View.GONE
                Log.i("uname", "versuc")
            } else {
//                login_status.value = "error"
//                Toast.makeText(activityRegister, "Please enter valid OTP", Toast.LENGTH_SHORT).show()
                val snackbar = Snackbar.make(
                    activityRegister.binding.root, "Please enter valid OTP",
                    Snackbar.LENGTH_LONG
                )
                snackbar.show()
            }
        }


        /* Log.i("uname", "ver")
         Log.i("uname", otp_test)
         Log.i("uname", otp)
         isVerifyCall = true
         if (otp.equals(otp_test)) {
             login_status.value = "success"
             Log.i("uname", "versuc")
         }*/

        /* viewModelScope.launch {
             repository.register(password,
                 referral,
                 phone,
                 name,
                 otp,
                 email)
                 .catch {
                     Log.i("uname",it.message.toString())
                     login_status.value="error"
                 }
                 .collect {
                     login_message= it.msg.toString()
                     login_status.value= it.status
                     repository.setUserPrefs(it.post?.phone.toString())
                     Log.i("uname",it.status.toString())
                 }
         }*/
    }

    fun showLoader(activity: ActivityRegister) {
        dialog = Dialog(activity, R.style.AppTheme_Dialog)
        val view = View.inflate(activity, R.layout.progressdialog_item, null)
        dialog?.setContentView(view)
        dialog?.setCancelable(true)
        val progressbar: GifImageView = dialog?.findViewById(R.id.img_loader)!!
        dialog?.show()
    }

    fun hideLoader() {
        if (dialog != null) {
            dialog?.dismiss()
        }
    }

}