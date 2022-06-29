package com.locatocam.app.views.order_online

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.locatocam.app.data.responses.address.Data
import com.locatocam.app.databinding.ActivityOrderOnlineBinding
import com.locatocam.app.repositories.HomeRepository
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.viewmodels.ActivityMainViewModel
import com.locatocam.app.viewmodels.HomeViewModel
import com.locatocam.app.views.home.HomeViewModelFactory
import com.locatocam.app.views.location.MapsActivity
import com.locatocam.app.views.search.AdddressAdapter
import com.locatocam.app.views.search.AutoCompleteAdapter
import com.locatocam.app.views.search.ClickEvents
import com.locatocam.app.views.search.Locationitem
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
@AndroidEntryPoint
class ActivityOrderOnline : OrderOnlineBaseActivity(), ClickEvents {
    lateinit var binding:ActivityOrderOnlineBinding
    lateinit var viewModel: ActivityMainViewModel
    lateinit var homeViewModel: HomeViewModel


    lateinit var placesClient: PlacesClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityOrderOnlineBinding.inflate(layoutInflater,baseBinding.container,true)
        viewModel=ViewModelProvider(this).get(ActivityMainViewModel::class.java)

        var repository= HomeRepository(this.application,"")
        var factory= HomeViewModelFactory(repository)

        homeViewModel=ViewModelProvider(this,factory).get(HomeViewModel::class.java)
        baseBinding.myLocation.text = SharedPrefEnc.getPref(this,"selected_address")

        var layoutManager = LinearLayoutManager(this)
        binding.recyclerviewSearch.layoutManager= LinearLayoutManager(this)
        binding.savedAddress.layoutManager= LinearLayoutManager(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.close.setOnClickListener {
            binding.locationView.visibility=View.GONE
        }

        homeViewModel.addressresp.observe(this,{
            Log.e("address", it.message!!)
            Log.e("address", it.data.toString())
            val adapter = AdddressAdapter(it.data!!,this)
            binding.savedAddress.adapter = adapter
        })
        homeViewModel.getAddress(SharedPrefEnc.getPref(this,"mobile"))

        binding.searchLocation.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if(!binding.searchLocation.text.toString().equals("")){
                    performSearch(binding.searchLocation.text.toString())
                }
            }


        })

        binding.useCurrentLocation.setOnClickListener {
            initLocation()
        }

        viewModel.address_text.observe(this,{
            baseBinding.myLocation.text =it.toString()
            SharedPrefEnc.setPref("selected_address",it,this)


        })
        baseBinding.myLocation.setOnClickListener {
           showLocation()

        }
        initAutoComplete()
//        setContentView(binding.root)
    }

    override fun onClickLocationItem(locationitem: Locationitem) {
        binding.recyclerviewSearch.visibility=View.GONE
        var intent= Intent(this, MapsActivity::class.java)
        intent.putExtra("address_text",locationitem.name)
        intent.putExtra("place_id",locationitem.placeid)
        startForResult.launch(intent)
    }

    override fun onClickAddress(data: Data) {
        Log.e("lat1",data.latitude.toString())
        Log.e("lat1",data.longitude.toString())
        viewModel.address_text.value=data.customer_address
        viewModel.lat= data.latitude!!.toDouble()
        viewModel.lng= data.longitude!!.toDouble()
        SharedPrefEnc.setPref("selected_lat",data.latitude,this)
        SharedPrefEnc.setPref("selected_lng",data.longitude,this)

        binding.locationView.visibility=View.GONE
        val intent = Intent(this,ActivityOrderOnline::class.java)
        finish()
        startActivity(intent)



    }

    override fun showPopup(v: View, item: Data, position: Int) {
        TODO("Not yet implemented")
    }
    fun showLocation(){
        binding.locationView.visibility=View.VISIBLE
    }

    val startForResult = this.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val inte = result.data
            Log.i("tr4444", inte?.getStringExtra("result_text").toString())
            Log.i("tr4444", inte?.getDoubleExtra("result_lat",0.0).toString())
            Log.i("tr4444", inte?.getDoubleExtra("result_lng",0.0).toString())

            viewModel.address_text.value=inte?.getStringExtra("result_text").toString()
            viewModel.lat= inte?.getDoubleExtra("result_lat",0.0)!!
            viewModel.lng= inte?.getDoubleExtra("result_lng",0.0)
            SharedPrefEnc.setPref("selected_lat",
                inte?.getDoubleExtra("result_lat",0.0)!!.toString(),this)
            SharedPrefEnc.setPref("selected_lng",
                inte?.getDoubleExtra("result_lng",0.0)!!.toString(),this)



        }
    }

    fun initLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val geocoder: Geocoder
                    val addresses: List<Address>
                    geocoder = Geocoder(this, Locale.getDefault())

                    addresses = geocoder.getFromLocation(
                        location.latitude,
                        location.longitude,
                        1
                    ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5


                    val address: String =
                        addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                    /* val city: String = addresses[0].getLocality()
                     val state: String = addresses[0].getAdminArea()
                     val country: String = addresses[0].getCountryName()
                     val postalCode: String = addresses[0].getPostalCode()
                     val knownName: String = addresses[0].getFeatureName()*/

                    viewModel.address_text.value = address
                    viewModel.lat = location.altitude
                    viewModel.lng = location.latitude
                    binding.locationView.visibility = View.GONE

                } else {
                    initLocation()
                }
            }
    }


    fun initAutoComplete(){
        val apiKey ="AIzaSyD5-MyENcRZHlFaCYAUvb0gsyrCv5wVaV4"

        if (!Places.isInitialized()) {
            Places.initialize(this, apiKey)
        }
        // Create a new Places client instance.
        // Create a new Places client instance.
        placesClient = Places.createClient(this)

    }

    fun performSearch(query:String){
        val token = AutocompleteSessionToken.newInstance()
        val request = FindAutocompletePredictionsRequest.builder()
            .setCountry("IND")
            //.setTypeFilter(TypeFilter.ADDRESS,TypeFilter.)
            .setSessionToken(token)
            .setQuery(query)
            .build()
        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
                val locations = mutableListOf<Locationitem>()

                for (prediction in response.autocompletePredictions) {
                    Log.i("REEWthQ9998", prediction.placeId)
                    Log.i("REEWQ9998", prediction.getPrimaryText(null).toString())
                    locations.add(Locationitem(prediction.getFullText(null).toString(),prediction.placeId))
                }
                var adapter3 = AutoCompleteAdapter(locations,this)
                binding.recyclerviewSearch.setAdapter(adapter3)
                if(locations.size>0){
                    binding.recyclerviewSearch.visibility=View.VISIBLE
                }else{
                    binding.recyclerviewSearch.visibility=View.GONE
                }
            }.addOnFailureListener { exception: Exception? ->
                if (exception is ApiException) {
                    Log.e("REEWQ9998", "Place not found: " + exception.statusCode)
                }
            }



    }
}