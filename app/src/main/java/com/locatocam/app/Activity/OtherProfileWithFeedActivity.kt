package com.locatocam.app.Activity

import android.Manifest
import android.R.attr.fragment
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.bumptech.glide.MemoryCategory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.locatocam.app.R
import com.locatocam.app.databinding.ActivityMainBinding
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.utils.Utils
import com.locatocam.app.viewmodels.ActivityMainViewModel
import com.locatocam.app.viewmodels.OtherProfileWithFeedViewModel
import com.locatocam.app.views.MainActivity
import com.locatocam.app.views.followers.FollowersActivity
import com.locatocam.app.views.home.HomeFragment
import com.locatocam.app.views.home.OtherProfileWithFeedFragment
import com.locatocam.app.views.rolls.RollsActivity
import com.locatocam.app.views.rollsexp.RollsExoplayerActivity
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_other_profile_with_feed.*
import pl.droidsonroids.gif.GifImageView
import java.util.*


class OtherProfileWithFeedActivity : AppCompatActivity() {
    lateinit var userid:String
    lateinit var inf_code:String
    lateinit var dialog:Dialog
    lateinit var viewModel: ActivityMainViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    companion object{
        lateinit var order_online_otherUser: RelativeLayout
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_profile_with_feed)

        userid=intent.getStringExtra("user_id").toString()
        inf_code=intent.getStringExtra("inf_code").toString()

        viewModel= ViewModelProvider(this).get(ActivityMainViewModel::class.java)
        order_online_otherUser=order_online_other_user

        bttm_nav_other_user.inflateMenu(com.locatocam.app.R.menu.nav_manu)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        Glide.get(this).setMemoryCategory(MemoryCategory.HIGH)

        showLoader()


        check_permissions()
        addView()
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

//                    lat = location.latitude
//                    lng = location.longitude


                    val address: String =
                        addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()


                    viewModel.address_text.value = address
//                    viewModel.lat = location.altitude
//                    viewModel.lng = location.latitude

                    Log.e("TAG", "initLocationff: "+location.altitude )

//                    SharedPrefEnc.setPref("selected_lat",location.latitude.toString(),this)
//                    SharedPrefEnc.setPref("selected_lng",  location.altitude.toString(), this)

                }else{
                    //initLocation()
                }
            }

        /*viewModel.address_text.value="test"
        viewModel.lat= 13.0022994
        viewModel.lng= 77.6192335*/
    }




    private fun addView() {
        bttm_nav_other_user.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> finish()
                R.id.rollsExoplayerActivity -> {
                    var intent=Intent(this,RollsExoplayerActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                R.id.folloersActivity -> {
                    var intent=Intent(this,FollowersActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                R.id.settingsFragment -> {

                }
            }
            true
        })



        val bundle = bundleOf("user_id" to userid, "inf_code" to inf_code)
        val fragment = OtherProfileWithFeedFragment()
        fragment.arguments = bundle;
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        val name = OtherProfileWithFeedFragment.javaClass.name;
        ft.add(R.id.other_user_fragment, fragment, name)
        ft.commit()
    }
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