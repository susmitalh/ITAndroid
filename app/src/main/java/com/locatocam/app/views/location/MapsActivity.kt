package com.locatocam.app.views.location

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.common.api.ApiException

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.locatocam.app.R
import com.locatocam.app.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdate
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import android.content.Intent
import android.view.View
import android.widget.RadioButton
import androidx.lifecycle.ViewModelProvider
import com.locatocam.app.repositories.LoginRepository
import com.locatocam.app.repositories.MapRepository
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.viewmodels.LoginViewModel
import com.locatocam.app.viewmodels.MapsViewModel
import com.locatocam.app.views.login.LoginViewModelFactory
import dagger.internal.MapFactory


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    lateinit var placesClient: PlacesClient
    lateinit var viewModel: MapsViewModel

    var result_lat=0.0
    var result_lng=0.0
    var result_text=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var repository= MapRepository(application)
        var factory= MapsModelFactory(repository)

        viewModel= ViewModelProvider(this,factory).get(MapsViewModel::class.java)
        val apiKey ="AIzaSyD5-MyENcRZHlFaCYAUvb0gsyrCv5wVaV4"

        setclickListeners()
        setObservers()
        if (!Places.isInitialized()) {
            Places.initialize(this, apiKey)
        }

        placesClient = Places.createClient(this)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    fun setObservers(){
        viewModel.save_address_status.observe(this,{
            if (it){
                val data = Intent()
                data.putExtra("result_lat",result_lat)
                data.putExtra("result_lng",result_lng)
                data.putExtra("result_text",result_text)
                setResult(RESULT_OK,data)
                finish()
            }
        })
    }
    fun setclickListeners(){
        binding.continueSave.setOnClickListener {
          binding.saveAddress.visibility= View.VISIBLE
          binding.mainListing.visibility= View.GONE
        }

        binding.change.setOnClickListener {
            binding.saveAddress.visibility= View.GONE
            binding.mainListing.visibility= View.VISIBLE
        }

        binding.saveAdress.setOnClickListener {

            viewModel.address=binding.address.text.toString()
            viewModel.landmark=binding.landmark.text.toString()
            var checkedrb=findViewById<RadioButton>(binding.saveas.checkedRadioButtonId)
            viewModel.add_save_as=checkedrb.tag.toString()
            viewModel.phone=SharedPrefEnc.getPref(application,"mobile")

            viewModel.saveAddress()
        }

        binding.justExploreThis.setOnClickListener {
            val data = Intent()
            data.putExtra("result_lat",result_lat)
            data.putExtra("result_lng",result_lng)
            data.putExtra("result_text",result_text)
            setResult(RESULT_OK,data)
            finish()
        }
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        var address_text=intent.getStringExtra("address_text")
        var place_id=intent.getStringExtra("place_id")
        binding.location.setText(address_text)
        // Specify the fields to return.
        val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
        val request = FetchPlaceRequest.newInstance(place_id.toString(), placeFields)

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response: FetchPlaceResponse ->
                val place = response.place
                Log.i("kl99000", "Place found: ${place.latLng?.latitude}")
                result_lat= place.latLng?.latitude!!
                result_lng= place.latLng?.longitude!!
                result_text= address_text.toString()

                binding.address.setText(address_text.toString())
                binding.houseFlat.setText("")
                binding.landmark.setText("")

                viewModel.lat=place.latLng?.latitude!!.toString()
                viewModel.lng=place.latLng?.longitude!!.toString()
                viewModel.place=place.address.toString()
                viewModel.phone=place.phoneNumber.toString()

                val sydney = LatLng(place.latLng?.latitude!!, place.latLng?.longitude!!)
                mMap.addMarker(MarkerOptions().position(sydney).title(address_text))
                val center = CameraUpdateFactory.newLatLng(sydney)
                val zoom = CameraUpdateFactory.zoomTo(18f)
                mMap.moveCamera(center);
                mMap.animateCamera(zoom);
            }.addOnFailureListener { exception: Exception ->
                if (exception is ApiException) {
                    Log.e("kl99000", "Place not found: ${exception.message}")
                    val statusCode = exception.statusCode
                    TODO("Handle error with given status code")
                }
            }
        // Add a marker in Sydney and move the camera

    }


}