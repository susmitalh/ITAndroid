package com.locatocam.app.views

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.bumptech.glide.MemoryCategory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.locatocam.app.R
import com.locatocam.app.databinding.ActivityMainBinding
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.utils.Utils
import com.locatocam.app.viewmodels.ActivityMainViewModel
import com.locatocam.app.views.home.HomeFragment
import com.locatocam.app.views.home.OtherProfileWithFeedFragment
import com.locatocam.app.views.home.header.HeaderFragment
import com.locatocam.app.views.home.test.SimpleExoPlayerViewHolder
import com.locatocam.app.views.order_online.ActivityOrderOnline
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.droidsonroids.gif.GifImageView
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        lateinit var binding: ActivityMainBinding
        var lat:Double = 0.0
        var lng:Double = 0.0
        var firstLoca:Boolean=true
    }
    lateinit var dialog:Dialog
    lateinit var viewModel: ActivityMainViewModel
    lateinit var navHostFragment:NavHostFragment
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //FacebookSdk.sdkInitialize(this)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel=ViewModelProvider(this).get(ActivityMainViewModel::class.java)
        firstLoca=true

        binding.bttmNav.inflateMenu(com.locatocam.app.R.menu.nav_manu)
        setUpNavigation()
        Glide.get(this).setMemoryCategory(MemoryCategory.HIGH)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        check_permissions()
        setOnclickListeners()
        hideLoader()


    }

    fun setOnclickListeners(){

        binding.orderOnline.setOnClickListener {
            Log.i("kj8888","nmk")
            var intent=Intent(this,ActivityOrderOnline::class.java)
            startActivity(intent)
        }
       /* Handler().postDelayed({
            var intent=Intent(this,ActivityOrderOnline::class.java)
            startActivity(intent)
            finish()
        },3000)*/
    }

    fun setUpNavigation() {
         navHostFragment = (supportFragmentManager.findFragmentById(com.locatocam.app.R.id.nav_host_fragment) as NavHostFragment?)!!
        NavigationUI.setupWithNavController(
            binding.bttmNav,
            navHostFragment!!.navController
        )
    }

    public fun hideLoader(){
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
             Handler().postDelayed({
                 binding.loader.visibility= View.GONE
       },3000)

            binding.bttmNav.visibility=View.VISIBLE
//            binding.orderOnline.visibility=View.VISIBLE
        }

    }
   /* public fun showLoader(){
        binding.loader.visibility= View.VISIBLE
    }*/

    fun showLocationPopup(){
        val childFragments = navHostFragment.childFragmentManager.fragments
        childFragments.forEach { fragment ->
            if(fragment is HomeFragment ){
                fragment.showLocation()
            }else if(fragment is OtherProfileWithFeedFragment ){
                fragment.showLocation()
            }
        }
    }
   


    fun check_permissions(): Boolean {
        val PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (!Utils.hasPermissions(this, PERMISSIONS)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(PERMISSIONS, 2)
            }
        }else{
            initLocation()
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initLocation()
            }
        }
    }
    @SuppressLint("MissingPermission")
    fun initLocation(){
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                if(location!=null){
                    val geocoder: Geocoder
                    val addresses: List<Address>
                    geocoder = Geocoder(this, Locale.getDefault())



                        addresses = geocoder.getFromLocation(
                            location.latitude,
                            location.longitude,
                            1
                        ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                        lat = location.latitude
                        lng = location.longitude


                        val address: String =
                            addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()


                        viewModel.address_text.value = address
                        viewModel.lat = location.altitude
                        viewModel.lng = location.latitude

                    Log.e("TAG", "initLocationff: "+location.altitude )

                    SharedPrefEnc.setPref("selected_lat",location.latitude.toString(),this)
                    SharedPrefEnc.setPref("selected_lng",  location.altitude.toString(), this)

                }else{
                    //initLocation()
                }
            }

        /*viewModel.address_text.value="test"
        viewModel.lat= 13.0022994
        viewModel.lng= 77.6192335*/
    }

    override fun onBackPressed() {
        super.onBackPressed()
        SimpleExoPlayerViewHolder.volumeMute=false
    }



}