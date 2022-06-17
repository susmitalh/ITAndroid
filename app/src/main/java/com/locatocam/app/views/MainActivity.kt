package com.locatocam.app.views

import android.Manifest
import android.R.attr.fragment
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.FragmentTransaction
import android.content.Context
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.bumptech.glide.MemoryCategory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.locatocam.app.R
import com.locatocam.app.databinding.ActivityMainBinding
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.utils.Utils
import com.locatocam.app.viewmodels.ActivityMainViewModel
import com.locatocam.app.views.home.HomeFragment
import com.locatocam.app.views.home.OtherProfileWithFeedFragment
import com.locatocam.app.views.home.test.SimpleExoPlayerViewHolder
import com.locatocam.app.views.order_online.ActivityOrderOnline
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        lateinit var binding: ActivityMainBinding
        var lat:Double = 0.0
        var lng:Double = 0.0
        var firstLoca:Boolean=true
        var isLoaded: Boolean = false

        fun onItemClick(userid: String, inf_code: String,context: Context) {
            Log.i("kl99999", inf_code + "--" + userid)
            val bundle = bundleOf("user_id" to userid, "inf_code" to inf_code)
            isLoaded = true;
            Navigation
                .findNavController(HomeFragment.binding.root)
                .navigate(R.id.action_homeFragment_to_otherProfileWithFeedFragment, bundle)
            //Navigation.findNavController(binding.root).navigate(R.id.action_homeFragment_to_otherProfileWithFeedFragment)


          /*  val fragment = OtherProfileWithFeedFragment()
            fragment.arguments = bundle;
            val fm = (context as AppCompatActivity).supportFragmentManager
            val ft = fm.beginTransaction()
            val name = OtherProfileWithFeedFragment.javaClass.name;
            ft.add(R.id.nav_host_fragment, fragment, name)
            ft.addToBackStack(null)
            ft.commit()*/

        }

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
                 binding.bttmNav.visibility=View.VISIBLE
//                 binding.orderOnline.visibility=View.VISIBLE
       },3000)
        }
    }

    public fun showLoader(){

        binding.loader.visibility= View.VISIBLE
       /* binding.bttmNav.visibility=View.GONE
        binding.orderOnline.visibility=View.GONE*/
    }
   /* public fun showLoader(){
        binding.loader.visibility= View.VISIBLE
    }*/

    fun showLocationPopup(){
        val childFragments = navHostFragment.childFragmentManager.fragments
        childFragments.forEach { fragment ->
            if(fragment is HomeFragment ){
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
                        viewModel.lat = location.latitude
                    viewModel.lng = location.altitude

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
//        SimpleExoPlayerViewHolder.volumeMute=false
//        val currentFragment = findNavController(navHostFragment)?.currentDestination

      /*  Log.e("TAG", "onBackPressssed: "+currentFragment)
        if (currentFragment is OtherProfileWithFeedFragment)*/

        super.onBackPressed()
    }




}