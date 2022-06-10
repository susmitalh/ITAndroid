package com.locatocam.app.views.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.locatocam.app.databinding.ActivityRegisterBinding
import com.locatocam.app.repositories.LoginRepository
import com.locatocam.app.viewmodels.RegisterViewModel
import com.locatocam.app.views.MainActivity
import com.locatocam.app.views.login.ActivityLogin

class ActivityRegister : AppCompatActivity() {
    lateinit var binding: ActivityRegisterBinding
    lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var repository= LoginRepository(application)
        var factory= RegisterViewModelFactory(repository)

        viewModel= ViewModelProvider(this,factory).get(RegisterViewModel::class.java)

        setObservers()
        setOnclickListers()
    }

    fun setObservers(){
        viewModel.login_status.observe(this,{
            if (it.equals("failed")){
                val snackbar = Snackbar.make(binding.root, viewModel.login_message,
                    Snackbar.LENGTH_LONG)
                snackbar.show()
            }else if(it.equals("success")){
                if (viewModel.isVerifyCall){
                    var intent= Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    binding.otpPopup.visibility = View.VISIBLE
                    binding.edtOtp.requestFocus()
                }
            }
            else if(it.equals("error")){
                val snackbar = Snackbar.make(binding.root, "Unable to connect to server",
                    Snackbar.LENGTH_LONG)
                snackbar.show()
            }
        })
    }

    fun setOnclickListers(){
        binding.signIn.setOnClickListener {
            var intent=Intent(this,ActivityLogin::class.java)
            startActivity(intent)
            finish()
        }
        binding.signup.setOnClickListener {
            if (binding.name.text.toString().equals("", ignoreCase = true)) {
                Toast.makeText(this, "Enter Full Name", Toast.LENGTH_SHORT).show()
            } else if (binding.emailId.getText().toString().equals("", ignoreCase = true)) {
                Toast.makeText(this, "Enter Email Id", Toast.LENGTH_SHORT).show()
            } else if (binding.mobileNo.getText().toString().equals("", ignoreCase = true)) {
                Toast.makeText(this, "Enter Mobile No.", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.email=binding.emailId.text.toString()
                viewModel.name=binding.name.text.toString()
                viewModel.phone=binding.mobileNo.text.toString()
                viewModel.register(this, binding.otpPopup)
            }
        }

        binding.imgClose.setOnClickListener {
            binding.otpPopup.visibility=View.GONE
            viewModel.isVerifyCall=false
        }
        binding.verify.setOnClickListener {
            if (binding.name.text.toString().equals("", ignoreCase = true)) {
                Toast.makeText(this, "Enter Full Name", Toast.LENGTH_SHORT).show()
            } else if (binding.emailId.getText().toString().equals("", ignoreCase = true)) {
                Toast.makeText(this, "Enter Email Id", Toast.LENGTH_SHORT).show()
            } else if (binding.mobileNo.getText().toString().equals("", ignoreCase = true)) {
                Toast.makeText(this, "Enter Mobile No.", Toast.LENGTH_SHORT).show()
            }
            else if (binding.edtOtp.getText().toString().equals("", ignoreCase = true)) {
                val snackbar = Snackbar.make(binding.root, "Enter Otp",
                    Snackbar.LENGTH_LONG)
                snackbar.show()
            }else {
                viewModel.email=binding.emailId.text.toString()
                viewModel.name=binding.name.text.toString()
                viewModel.phone=binding.mobileNo.text.toString()
                viewModel.otp=binding.edtOtp.text.toString()
                viewModel.verify(this,binding.edtOtp.text.toString(),binding.otpPopup)

              /*  var intent= Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()*/
            }
        }

        binding.resendOtp.setOnClickListener {
            if(!binding.mobileNo.text.toString().equals("")){
                viewModel.phone=binding.mobileNo.text.toString()
                viewModel.reSend(this,binding.edtOtp.text.toString(),binding.otpPopup)
            }else{
                val snackbar = Snackbar.make(binding.root, "Enter phone",
                    Snackbar.LENGTH_LONG)
                snackbar.show()
            }
        }
    }
}