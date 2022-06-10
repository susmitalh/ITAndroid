package com.locatocam.app.views.login

//import com.facebook.*
//import com.facebook.login.LoginManager
//import com.facebook.login.LoginResult

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.locatocam.app.R
import com.locatocam.app.databinding.ActivityLoginBinding
import com.locatocam.app.repositories.LoginRepository
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.viewmodels.LoginViewModel
import com.locatocam.app.views.MainActivity
import com.locatocam.app.views.register.ActivityRegister
import org.json.JSONObject
import pl.droidsonroids.gif.GifImageView
import java.util.*


class ActivityLogin : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var viewModel: LoginViewModel
    private var mGoogleSignInClient: GoogleSignInClient? = null
    var dialog: Dialog? = null

    var GToken: String? = null
    var socialEmail: String? = null
    var socialName: String? = null
     var FToken: String?=null
    var addNumberVerify: Boolean = false


    private var callbackManager: CallbackManager? = null
    private val EMAIL = "email"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var repository = LoginRepository(application)
        var factory = LoginViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)

        Log.i("looikkk", SharedPrefEnc.getPref(application, "mobile"))
        if (!SharedPrefEnc.getPref(application, "mobile").equals("")) {
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        setOnclickListeners()
        setObservers()
    }

    fun setOnclickListeners() {

        binding.imgCancel.setOnClickListener {
            binding.numberPopup.visibility = View.GONE
        }

        binding.login.setOnClickListener {
            hidekeyBoard()
            if (!binding.phone.text.toString().equals("")) {
                viewModel.phone = binding.phone.text.toString()
                viewModel.otp = ""
                viewModel.login(this)
            } else {
                val snackbar = Snackbar.make(
                    binding.root, "Enter phone",
                    Snackbar.LENGTH_LONG
                )
                snackbar.show()
            }
        }

        binding.verify.setOnClickListener {
            hidekeyBoard()
            viewModel.phone = binding.phone.text.toString()
            viewModel.otp = binding.edtOtp.text.toString()
            viewModel.verifyOtp(this)
        }
        binding.addNumber.setOnClickListener {

            if (binding.edtPhone.text.toString().equals("", ignoreCase = true)) {
                val snackbar = Snackbar.make(
                    binding.root, "Enter Number",
                    Snackbar.LENGTH_LONG
                )
                snackbar.show()
            } else {
//                    activity.binding.numberPopup.visibility = View.GONE
                showLoader()
                viewModel.addNumber(this)


            }
        }

        binding.verifyNumber.setOnClickListener {

//            viewModel.phone=binding.edtPhone.text.toString()
//            viewModel.otp=binding.addNumberVerify.text.toString()

            if (binding.addNumberVerify.text.toString().equals("", ignoreCase = true)) {
                val snackbar = Snackbar.make(
                    binding.root, "Enter OTP",
                    Snackbar.LENGTH_LONG
                )
                snackbar.show()
            } else {
                viewModel.AddNumberverify(
                    this,
                    GToken,
                    socialName,
                    socialEmail,
                    FToken
                )
            }
        }

        binding.resendOtp.setOnClickListener {
            if (!binding.phone.text.toString().equals("")) {
                viewModel.phone = binding.phone.text.toString()
                viewModel.login(this)
            } else {
                val snackbar = Snackbar.make(
                    binding.root, "Enter phone",
                    Snackbar.LENGTH_LONG
                )
                snackbar.show()
            }
        }

        binding.imgClose.setOnClickListener {
            binding.otpPopup.visibility = View.GONE
        }

        binding.fb.setOnClickListener {
//            onFblogin()
            facebookLogin()

        }

        binding.signupwithFoodstagram.setOnClickListener {
            val intent = Intent(this, ActivityRegister::class.java)
            startActivity(intent)
        }

        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        binding.logingoogle.setOnClickListener {
            showLoader()
            val signInIntent = mGoogleSignInClient!!.signInIntent
            startActivityForResult(signInIntent, 100)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                // Google Sign In was successful, authenticate with Firebase
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.displayName)
//                viewModel.firebaseAuthWithGoogle(this,account.idToken,account.displayName,account.email)

                GToken = account.idToken
                socialName = account.displayName
                socialEmail = account.email
                addNumberVerify = true
                hideLoader()
                binding.numberPopup.visibility = View.VISIBLE


            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                hideLoader()
                Log.w("TAG", "Google sign in failed" + e.statusCode)
                val snackbar = Snackbar.make(
                    binding.root, "" + e.message,
                    Snackbar.LENGTH_LONG
                )
                snackbar.show()

            }
        }
    }

    fun setObservers() {
        viewModel.login_status.observe(this, {
            if (it.equals("success")) {
                if (viewModel.isFinalCall) {
                    var intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    //show otp view and focus
                    binding.otpPopup.visibility = View.VISIBLE
                    binding.edtOtp.requestFocus()
                }
            } else if (it.equals("failed")) {
                val snackbar = Snackbar.make(
                    binding.root, viewModel.login_message,
                    Snackbar.LENGTH_LONG
                )
                snackbar.show()
            } else {
                val snackbar = Snackbar.make(
                    binding.root, "Unable to connect to server",
                    Snackbar.LENGTH_LONG
                )
                snackbar.show()
            }
        })
    }

    fun hidekeyBoard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun facebookLogin() {
        FacebookSdk.sdkInitialize(applicationContext)
        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().logInWithReadPermissions(
            this,
            Arrays.asList("email", "public_profile")
        );

        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onCancel() {
                    val snackbar = Snackbar.make(
                        binding.root, "Facebook Login Fail",
                        Snackbar.LENGTH_LONG
                    )
                    snackbar.show()
                }

                override fun onError(error: FacebookException) {
                    val snackbar = Snackbar.make(
                        binding.root, "" + error.message,
                        Snackbar.LENGTH_LONG
                    )
                    snackbar.show()
                }

                override fun onSuccess(result: LoginResult) {
                    showLoader()

                    var request = GraphRequest.newMeRequest(result.accessToken,
                        object : GraphRequest.GraphJSONObjectCallback {
                            override fun onCompleted(obj: JSONObject?, response: GraphResponse?) {
                                val email = response!!.getJSONObject()!!.getString("email")
                                val fastname = response!!.getJSONObject()!!.getString("first_name")
                                val lastname = response!!.getJSONObject()!!.getString("last_name")
                                val name = fastname + " " + lastname
                                Log.e("TAG", "onCompletedfb: " + name)
//                                viewModel.handleFacebookAccessToken(
//                                    this@ActivityLogin,
//                                    result.accessToken,
//                                    email,
//                                    name
//                                )
                                FToken = result.accessToken.token
                                socialName = name
                                socialEmail = email
                                addNumberVerify = false
                                binding.numberPopup.visibility = View.VISIBLE
                                hideLoader()
                            }

                        })
                    val parameters = Bundle()
                    parameters.putString("fields", "id,email,first_name,last_name,gender")
                    request.parameters = parameters
                    request.executeAsync()
                }


            })
    }

    /*private fun onFblogin() {
    callbackManager = CallbackManager.Factory.create()

    // Set permissions
    LoginManager.getInstance()
        .logInWithReadPermissions(this, Arrays.asList(EMAIL))
    LoginManager.getInstance().registerCallback(callbackManager,object:FacebookCallback<LoginResult>{
        override fun onSuccess(result: LoginResult?) {
            val request = GraphRequest.newMeRequest(
                result?.getAccessToken()
            ) { `object`, response ->
                // Application code
                try {
                    Log.i("Response", response.toString())
                    val email = response.jsonObject.getString("email")
                    val firstName = response.jsonObject.getString("first_name")
                    val lastName = response.jsonObject.getString("last_name")

                    val profileURL = ""
                    viewModel.socialLogin(email, firstName)
                    //TODO put your code here
                } catch (e: JSONException) {
                    Log.i("nnncnn", e.message!!)
                }
            }
        }

        override fun onCancel() {

        }

        override fun onError(error: FacebookException?) {

        }

    })
}*/

    fun showLoader() {
        dialog = Dialog(this, R.style.AppTheme_Dialog)
        val view = View.inflate(this, R.layout.progressdialog_item, null)
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