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
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.Marker
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.*
import com.locatocam.app.Activity.OtherProfileWithFeedActivity
import com.locatocam.app.data.responses.address.Data
import com.locatocam.app.repositories.MapRepository
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.utility.Loader
import com.locatocam.app.viewmodels.MapsViewModel
import com.locatocam.app.views.MainActivity
import com.locatocam.app.views.home.HomeFragment
import com.locatocam.app.views.home.header.HeaderFragment
import com.locatocam.app.views.search.AdddressAdapter
import com.locatocam.app.views.search.AutoCompleteAdapter
import com.locatocam.app.views.search.ClickEvents
import com.locatocam.app.views.search.Locationitem
import java.io.IOException


class MapsActivity : AppCompatActivity(), OnMapReadyCallback , GoogleMap.OnMapClickListener,
    GoogleMap.OnMarkerDragListener, ClickEvents {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    lateinit var placesClient: PlacesClient
    lateinit var viewModel: MapsViewModel
    lateinit var geocoder: Geocoder
    var result_lat=0.0
    var result_lng=0.0
    var result_text=""
    var addressTag=false
    var Bhide=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var repository= MapRepository(application)
        var factory= MapsModelFactory(repository)
        geocoder = Geocoder(this)

        viewModel= ViewModelProvider(this,factory).get(MapsViewModel::class.java)
        val apiKey ="AIzaSyD5-MyENcRZHlFaCYAUvb0gsyrCv5wVaV4"

        Bhide= intent.getStringExtra("Bhide").toString()

        binding.recyclerviewSearchLoca.layoutManager = LinearLayoutManager(this)
        binding.savedAddress.layoutManager = LinearLayoutManager(this)
        MainActivity.binding.layoutBNavigation.visibility=View.VISIBLE
        if (Bhide.equals("0"))
            OtherProfileWithFeedActivity.layoutOtherBNavigation.visibility = View.VISIBLE



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

    override fun onBackPressed() {
        super.onBackPressed()
        MainActivity.binding.layoutBNavigation.visibility=View.GONE
        if (Bhide.equals("")) {
            OtherProfileWithFeedActivity.layoutOtherBNavigation.visibility = View.GONE
        }

    }

    fun setObservers(){

        viewModel.addressresp.observe(this, {
            Log.e("address", it.message!!)
            Log.e("address", it.data.toString())
            val adapter = AdddressAdapter(it.data!!, this)
            binding.savedAddress.adapter = adapter
        })
        viewModel.getAddress(SharedPrefEnc.getPref(this, "mobile"))
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
        viewModel.edtAddress.observe(this,{
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

    fun performSearch(query: String) {
        val token = AutocompleteSessionToken.newInstance()
        val request = FindAutocompletePredictionsRequest.builder()
            .setCountry("IND")
            .setSessionToken(token)
            .setQuery(query)
            .build()
        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
                val locations = mutableListOf<Locationitem>()

                for (prediction in response.autocompletePredictions) {
                    Log.i("REEWthQ9998", prediction.placeId)
                    Log.i("REEWQ9998", prediction.getPrimaryText(null).toString())
                    locations.add(
                        Locationitem(
                            prediction.getFullText(null).toString(),
                            prediction.placeId
                        )
                    )
                }
                var adapter3 = AutoCompleteAdapter(locations, this)
                binding.recyclerviewSearchLoca.setAdapter(adapter3)
                if (locations.size > 0) {
                    binding.recyclerviewSearchLoca.visibility = View.VISIBLE
                } else {
                    binding.recyclerviewSearchLoca.visibility = View.GONE
                }
            }.addOnFailureListener { exception: Exception? ->
                if (exception is ApiException) {
                    Log.e("REEWQ9998", "Place not found: " + exception.statusCode)
                }
            }


    }




    fun setclickListeners(){


        binding.imgSearchLocCancel.setOnClickListener {
            binding.layoutSearchLoc.visibility=View.GONE
        }

        binding.edtSearchLoca.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if (!binding.edtSearchLoca.text.toString().equals("")) {
                    performSearch(binding.edtSearchLoca.text.toString())
                }
            }

        })

        binding.txtChangeLocation.setOnClickListener {

            binding.layoutSearchLoc.visibility=View.VISIBLE
        }
        binding.continueSave.setOnClickListener {
          binding.saveAddress.visibility= View.VISIBLE
          binding.mainListing.visibility= View.GONE
        }

        binding.change.setOnClickListener {
            binding.saveAddress.visibility= View.GONE
            binding.mainListing.visibility= View.VISIBLE
        }

        binding.other.setOnClickListener {
           binding.txtAddressTag.visibility=View.VISIBLE
            binding.edtAddressTag.visibility=View.VISIBLE
            addressTag=true
        }
        binding.home.setOnClickListener {
            binding.txtAddressTag.visibility=View.GONE
            binding.edtAddressTag.visibility=View.GONE
            addressTag=false
        }
        binding.office.setOnClickListener {
            binding.txtAddressTag.visibility=View.GONE
            binding.edtAddressTag.visibility=View.GONE
            addressTag=false
        }

        binding.saveAdress.setOnClickListener {


            viewModel.address=binding.address.text.toString()
            viewModel.landmark=binding.landmark.text.toString()
            viewModel.flat_no=binding.houseFlat.text.toString()
            var checkedrb=findViewById<RadioButton>(binding.saveas.checkedRadioButtonId)
            viewModel.phone=SharedPrefEnc.getPref(application,"mobile")
            if (addressTag){
                if (binding.edtAddressTag.text.toString().equals("")){
                    Toast.makeText(this, "Enter Address tag", Toast.LENGTH_SHORT).show()
                }else{
                    viewModel.add_save_as=binding.edtAddressTag.text.toString()
                    addressTag=false
                    if (viewModel.address_id.equals("")||viewModel.address_id.equals(null)) {
                        viewModel.saveAddress()
                    }else{
                        viewModel.edtAddress()
                    }
                }
            }else {
                viewModel.add_save_as = checkedrb.tag.toString()
                if (viewModel.address_id.equals("")||viewModel.address_id.equals(null)) {
                    viewModel.saveAddress()
                }else{
                    viewModel.edtAddress()
                }
            }




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
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.setOnMapClickListener(this)
        mMap.setOnMarkerDragListener(this)

        var address_text=intent.getStringExtra("address_text")
        var place_id=intent.getStringExtra("place_id")

        if (!place_id.equals(null)) {
            Log.e("TAG", "onMapReady: ", )
            binding.location.setText(address_text)
            binding.txtSearchLoc.setText(address_text)
            // Specify the fields to return.
            val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
            val request = FetchPlaceRequest.newInstance(place_id.toString(), placeFields)

            placesClient.fetchPlace(request)
                .addOnSuccessListener { response: FetchPlaceResponse ->
                    val place = response.place
                    Log.i("kl99000", "Place found: ${place.latLng?.latitude}")

                    var addresses = geocoder.getFromLocation(place.getLatLng()!!.latitude, place.getLatLng()!!.longitude, 1)
                    viewModel.pin_code = addresses.get(0).postalCode
                    result_lat = place.latLng?.latitude!!
                    result_lng = place.latLng?.longitude!!
                    result_text = address_text.toString()
                    binding.address.setText(address_text.toString())
                   /* binding.houseFlat.setText("")
                    binding.landmark.setText("")*/


                    viewModel.lat = place.latLng?.latitude!!.toString()
                    viewModel.lng = place.latLng?.longitude!!.toString()
//                viewModel.place=place.address.toString()
                    viewModel.phone = place.phoneNumber.toString()

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
                    }
                }
        }else{
            val lat=intent.getStringExtra("lat")
            val lng=intent.getStringExtra("lng")
            val address=intent.getStringExtra("address")
            val flateNo=intent.getStringExtra("flateNo")
            val landmark=intent.getStringExtra("landmark")
            viewModel.address_id= intent.getStringExtra("addressId").toString()
            val address_save_as= intent.getStringExtra("address_save_as").toString()



            if (!address_save_as.equals("null")) {
                if (address_save_as.equals("home")) {
                    binding.home.isChecked = true
                    addressTag = false
                } else if (address_save_as.equals("office")) {
                    binding.office.isChecked = true
                    addressTag = false
                } else {
                    binding.other.isChecked = true
                    binding.edtAddressTag.setText(address_save_as)
                    binding.txtAddressTag.visibility = View.VISIBLE
                    binding.edtAddressTag.visibility = View.VISIBLE
                    addressTag = true
                }
            }

          /*  var addresses = geocoder.getFromLocation(lat!!.toDouble(), lng!!.toDouble(), 1)
            viewModel.pin_code = addresses.get(0).postalCode*/
            result_lat = lat!!.toDouble()
            result_lng = lng!!.toDouble()
            result_text = address.toString()
            binding.address.setText(address)
            binding.houseFlat.setText(flateNo)
            binding.landmark.setText(landmark)

            binding.location.text=address

            viewModel.lat = lat
            viewModel.lng = lng
//                viewModel.place=place.address.toString()

            val sydney = LatLng(lat.toDouble(), lng.toDouble())
            mMap.addMarker(MarkerOptions().position(sydney).title(address_text))
            val center = CameraUpdateFactory.newLatLng(sydney)
            val zoom = CameraUpdateFactory.zoomTo(18f)
            mMap.moveCamera(center);
            mMap.animateCamera(zoom);

        }
        // Add a marker in Sydney and move the camera

    }

    override fun onMapClick(p0: LatLng) {
        try {
            mMap.clear()
            var addresses: List<Address> = geocoder.getFromLocation(p0.latitude, p0.longitude, 1)
            if (addresses.size > 0) {
                var address = addresses.get(0)
                var streetAddress = address.getAddressLine(0)

                result_lat= address.latitude
                result_lng= address.longitude
                result_text= streetAddress.toString()

                binding.address.setText(streetAddress.toString())
               /* binding.houseFlat.setText("")
                binding.landmark.setText("")*/

                viewModel.lat=address.latitude.toString()
                viewModel.lng=address.longitude.toString()
//                viewModel.place=streetAddress.toString()
//                viewModel.phone=address.phone.toString()
                viewModel.pin_code=address.postalCode.toString()


                Log.e("TAG", "onMapClicksx: "+address.postalCode )

                binding.location.setText(streetAddress.toString())
                binding.txtSearchLoc.setText(streetAddress.toString())
                mMap.addMarker(MarkerOptions()
                    .position(p0)
                    .title(streetAddress)
                    .draggable(true)
                )
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onMarkerDrag(p0: Marker) {
        TODO("Not yet implemented")
    }

    override fun onMarkerDragEnd(p0: Marker) {
        var latLng = p0.position
        try {

            var addresses: List<Address> =
                geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (addresses.size > 0) {
                var address = addresses.get(0)
                var streetAddress = address.getAddressLine(0)
                p0.title = streetAddress
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onMarkerDragStart(p0: Marker) {
        TODO("Not yet implemented")
    }

    override fun onClickLocationItem(locationitem: Locationitem) {
       binding.layoutSearchLoc.visibility=View.GONE
    }

    override fun onClickAddress(data: Data) {

        HomeFragment.binding.locationView.visibility=View.GONE

            result_lat=data.latitude!!.toDouble()
            result_lng=data.longitude!!.toDouble()
        binding.layoutSearchLoc.visibility = View.GONE

        result_text= data.customer_address!!

       HeaderFragment.binding.myLocation.setText(data.customer_address)
       HomeFragment.binding.myLocation.setText(data.customer_address)
        /*binding.houseFlat.setText("")
        binding.landmark.setText("")*/

        viewModel.lat=data.latitude.toString()
        viewModel.lng=data.longitude.toString()
        viewModel.pin_code=data.pin_code.toString()
        viewModel.landmark=data.cust_landmark.toString()
        Log.e("TAG", "getAllFeedsdd: "+data.latitude+", "+data.longitude )
        val data = Intent()
        data.putExtra("result_lat",result_lat)
        data.putExtra("result_lng",result_lng)
        data.putExtra("result_text",result_text)
        setResult(RESULT_OK,data)
        finish()

//                viewModel.place=place.address.toString()

    }

    override fun showPopup(v: View, item: Data, position: Int) {

    }
}