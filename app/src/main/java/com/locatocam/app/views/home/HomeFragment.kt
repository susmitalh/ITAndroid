package com.locatocam.app.views.home


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.app.kardder.util.RecyclerViewScrollListener
import com.bumptech.glide.Glide
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.locatocam.app.Activity.PlayPostActivity
import com.locatocam.app.ModalClass.AddView
import com.locatocam.app.R
import com.locatocam.app.databinding.FragmentHomeBinding
import com.locatocam.app.di.module.NetworkModule.Companion.getClient
import com.locatocam.app.network.WebApi
import com.locatocam.app.repositories.HomeRepository
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.services.PreCachingService
import com.locatocam.app.utils.Constants
import com.locatocam.app.viewmodels.HomeViewModel
import com.locatocam.app.views.MainActivity
import com.locatocam.app.views.ceratepost.UploadPostmanual
import com.locatocam.app.views.chat.ChatActivity
import com.locatocam.app.views.home.header.HeaderFragment
import com.locatocam.app.views.home.test.*
import com.locatocam.app.views.location.MapsActivity
import com.locatocam.app.views.search.AdddressAdapter
import com.locatocam.app.views.search.AutoCompleteAdapter
import com.locatocam.app.views.search.ClickEvents
import com.locatocam.app.views.search.Locationitem
import net.minidev.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


public class HomeFragment : Fragment(), FeedEvents, ClickEvents, SimpleEvents {

    companion object {
        lateinit var instance: HomeFragment
        lateinit var binding: FragmentHomeBinding
        var add: String = ""
        var order_visiblity: Boolean = false
        var orderType: Boolean = false
        var commet: PostCountData? = null
        var follow: Follow? = null
        var firstCall: Boolean = true
    }


    lateinit var viewModel: HomeViewModel
    var lastCount: Int = -1
    lateinit var placesClient: PlacesClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var layoutManager = LinearLayoutManager(requireActivity())
//        if (!MainActivity.isLoaded) {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        var repository = HomeRepository(requireActivity().application, "")
        var factory = HomeViewModelFactory(repository)
        instance = HomeFragment()
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        binding.recyclerviewSearch.layoutManager = LinearLayoutManager(requireActivity())
        binding.savedAddress.layoutManager = LinearLayoutManager(requireActivity())

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())


        /* var it = arrayListOf<com.locatocam.app.data.responses.feed.Data>();
         it.add(com.locatocam.app.data.responses.feed.Data())
         if (!MainActivity.isLoaded || binding.playerContainer.adapter == null) {
             var adapter = SimpleAdapter(context, it, this, commet, follow)
             binding.playerContainer.setAdapter(adapter)
         }*/

        if (SharedPrefEnc.getPref(requireActivity().application, "user_type") == "poc" ||
            SharedPrefEnc.getPref(requireActivity().application, "user_type") == "customer"
        ) {
            binding.createpost.visibility = View.GONE
            binding.shareHeader.visibility = View.GONE
        } else {
            binding.shareHeader.visibility = View.VISIBLE
            binding.createpost.visibility = View.VISIBLE

        }


        viewModel.feed_items.observe(viewLifecycleOwner, {
            viewModel.loading = false
            /*   Handler().postDelayed(Runnable {
                   (activity as MainActivity).hideLoaderimg()
               }, 2500)*/

            binding.playerContainer.setLayoutManager(layoutManager)
            follow = object : Follow {
                override fun follow(position: Int, followStatus: String) {
                    it.get(position).profile_follow = followStatus


                    if (followStatus.equals("1")) {
                        var count: Int =
                            (it.get(position).profile_follow_count?.toInt()) as Int + 1
                        it.get(position).profile_follow_count = count.toString()
                    } else {
                        var count: Int =
                            (it.get(position).profile_follow_count?.toInt()) as Int - 1
                        it.get(position).profile_follow_count = count.toString()
                    }
                    (binding.playerContainer.adapter as SimpleAdapter)!!.notifyDataSetChanged()
                }

                override fun brandFollow(position: Int, followStatus: String) {
                    it.get(position).brand_follow = followStatus
                    (binding.playerContainer.adapter as SimpleAdapter)!!.notifyDataSetChanged()
                }

            }

//            if (viewModel.offset == 0 && binding.playerContainer.adapter == null) {
            if (viewModel.offset == 0) {

                var adapter = SimpleAdapter(context, it, this, commet, follow)
                binding.playerContainer.setAdapter(adapter)
            } else {
                    if(binding.playerContainer.adapter!=null) {
                        (binding.playerContainer.adapter as SimpleAdapter).addAll(it)
                        (binding.playerContainer.adapter as SimpleAdapter)!!.notifyDataSetChanged()
                    }
            }
            commet = object : PostCountData {
                override fun commentCount(commentCount: String, position: Int) {
                    (binding.playerContainer.adapter as SimpleAdapter).mediaList.get(position).comments_count =
                        commentCount
                    (binding.playerContainer.adapter as SimpleAdapter)!!.notifyDataSetChanged()
//                    Log.e("TAG", "commentCount: "+it.get(position).comments_count )
                }

                override fun viewCount(viewCount: String?, pisition: Int) {
//                    it.get(pisition).views_count = viewCount
//                    (binding.playerContainer.adapter as SimpleAdapter)!!.notifyDataSetChanged()
                }
            }


            binding.playerContainer.addOnScrollListener(object : RecyclerViewScrollListener() {
                override fun onItemIsFirstVisibleItem(index: Int) {
                    val apiInterface = getClient()!!.create(
                        WebApi::class.java
                    )
                    Log.e("TAGScroll", "onItemIsFissrstVisibleItem: $index")

                    val userid = SharedPrefEnc.getPref(context, "user_id")

                    if (index > -1) {
                        if (lastCount != index) {
                            lastCount = index
                            Log.e("TAGScroll", "onItemIsFisssssrstVisibleItem: $lastCount")
                            val jsonObject = JSONObject()
                            jsonObject["id"] =
                                (binding.playerContainer.adapter as SimpleAdapter).mediaList.get(
                                    index
                                ).post_id
                            jsonObject["view_type"] =
                                (binding.playerContainer.adapter as SimpleAdapter).mediaList.get(
                                    index
                                ).type
                            jsonObject["user_id"] = userid
                            apiInterface.getAddView(jsonObject)!!
                                .enqueue(object : Callback<AddView> {
                                    override fun onResponse(
                                        call: Call<AddView>,
                                        response: Response<AddView>
                                    ) {
                                        /*  commet.viewCount(
                                    response.body()!!.data!!.viewsCount,
                                    index
                                )*/
                                        if (binding.playerContainer.adapter!=null) {
                                            (binding.playerContainer.adapter as SimpleAdapter).mediaList.get(
                                                index
                                            ).views_count = response.body()?.data?.viewsCount
                                            (binding.playerContainer.adapter as SimpleAdapter).notifyDataChanged()
                                        }
                                    }


                                    override fun onFailure(call: Call<AddView>, t: Throwable) {}
                                })
                        }
                    }
                }

            })


            startPreCaching(it)
            preloadImages(it)
//            (activity as MainActivity).showLoader()
            (activity as MainActivity).hideLoader()

        })
        // viewModel.getAllFeeds("")


        viewModel.addressresp.observe(viewLifecycleOwner, {
            Log.e("address", it.message!!)
            Log.e("address", it.data.toString())
            val adapter = AdddressAdapter(it.data!!, this)
            binding.savedAddress.adapter = adapter
        })
        viewModel.getAddress(SharedPrefEnc.getPref(requireContext(), "mobile"))


        viewModel.approvalCounts.observe(viewLifecycleOwner, {
            Glide.with(this).load(it.data.profile_pic).into(binding.profile)
            MainActivity.binding.orderOnline.visibility = View.VISIBLE

            if (it.data.approval_count.toInt() > 0) {
                binding.approvalCounts.visibility = View.VISIBLE
                binding.approvalCounts.setText(it.data.approval_count)
                Log.e("TAG", "getApprovalCountsget: " + it.data.approval_count)
            }

            if (it.data.message_count.toInt() > 0) {
                binding.messageCounts.visibility = View.VISIBLE
                binding.messageCounts.setText(it.data.message_count)
            }

            if (it.data.order_status == "off") {

                order_visiblity = true
                Handler().postDelayed(Runnable {
                    MainActivity.binding.orderOnline.visibility = View.GONE

                }, 3000)
            } else {
                order_visiblity = false
            }

            if (it.data.order_type == "show") {
                orderType = true
            } else {
                orderType = false
            }

            if (it.data.approval_count.toInt() > 0) {
                /*  binding.approvalCounts.visibility = View.VISIBLE
                  binding.approvalCounts.setText(apprs.data.approval_count.toString())*/
            }
            if (it.data.message_count.toInt() > 0) {
                /* binding.messageCounts.visibility = View.VISIBLE
                 binding.messageCounts.setText(apprs.data.message_count)*/
            }


        })

        Log.e("TAG", "onCreatedddView: " + SharedPrefEnc.getPref(context, "selected_lng"))
        Log.e("TAG", "onCreatedddView: " + MainActivity.lat)

        if (MainActivity.firstLoca == true) {
            viewModel.getApprovalCounts(
                MainActivity.lat.toString(),
                MainActivity.lng.toString()
            )
        } else {
            viewModel.getApprovalCounts(
                SharedPrefEnc.getPref(context, "selected_lat"),
                SharedPrefEnc.getPref(context, "selected_lng")
            )
        }

        binding.messages.setOnClickListener {
            var intent = Intent(requireActivity(), ChatActivity::class.java)
            startActivity(intent)
        }

        binding.playerContainer.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) { //check for scroll down
                    var visibleItemCount = layoutManager.getChildCount()
                    var totalItemCount = layoutManager.getItemCount()
                    var pastVisiblesItems = layoutManager.findFirstVisibleItemPosition()
                    if (!viewModel.loading) {
                        Log.e(
                            "paggination",
                            "onScrolled: counts " + visibleItemCount + "," + totalItemCount + "," + pastVisiblesItems
                        )
                        Log.e(
                            "paggination",
                            "onScrolled: sum " + (visibleItemCount + pastVisiblesItems)
                        )
                        Log.e("paggination", "onScrolled: total " + totalItemCount.minus(2))
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount - 2) {
                            Log.v("gt66666", "Last Item Wow !")
                            var act = (requireActivity() as MainActivity)
                            Log.e(
                                "TAG location",
                                "onScrolled: " + act.viewModel.lat + "," + act.viewModel.lng
                            )
                            Log.e(
                                "TAG",
                                "onClickAddressNewLaat: " + SharedPrefEnc.getPref(
                                    context,
                                    "selected_lat"
                                ) + " lng " + SharedPrefEnc.getPref(context, "selected_lng")
                            )

                            viewModel.getAllFeeds("", act.viewModel.lat, act.viewModel.lng)

                        }
                    }
                }
            }
        })
        binding.shareHeader.setOnClickListener {
            val message =
                "https://loca-toca.com/Login/index?si=" + SharedPrefEnc.getPref(
                    requireActivity().application,
                    "influencer_code"
                )
            val share = Intent(Intent.ACTION_SEND)
            share.type = "text/plain"
            share.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(Intent.createChooser(share, "Share"))
        }


        binding.close.setOnClickListener {
            binding.locationView.visibility = View.GONE
        }

        binding.searchLocation.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if (!binding.searchLocation.text.toString().equals("")) {
                    performSearch(binding.searchLocation.text.toString())
                }
            }

        })

        binding.useCurrentLocation.setOnClickListener {
            initLocation()
        }

        binding.createpost.setOnClickListener {
            var intent = Intent(requireActivity(), UploadPostmanual::class.java)
            startActivity(intent)
        }
        initAutoComplete()
//        }
        /*else {
                Handler().postDelayed(Runnable {
                    if (binding.playerContainer.adapter != null) {
                        (binding.playerContainer.adapter as SimpleAdapter).addAgain()
                    }
                    MainActivity.isLoaded = false
                }, 800);

            }*/
        return binding.root
    }

    @SuppressLint("MissingPermission")
    fun initLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val geocoder: Geocoder
                    val addresses: List<Address>
                    geocoder = Geocoder(requireActivity(), Locale.getDefault())

                    addresses = geocoder.getFromLocation(
                        location.latitude,
                        location.longitude,
                        1
                    ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    Log.e("TAG", "initLocationloca: " + location.latitude)


                    val address: String =
                        addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                    /* val city: String = addresses[0].getLocality()
                     val state: String = addresses[0].getAdminArea()
                     val country: String = addresses[0].getCountryName()
                     val postalCode: String = addresses[0].getPostalCode()
                     val knownName: String = addresses[0].getFeatureName()*/

                    if (activity is MainActivity) {
                        var act = activity as MainActivity
                        act.viewModel.address_text.value = address
                        act.viewModel.lat = location.latitude
                        act.viewModel.lng = location.altitude
                        binding.locationView.visibility = View.GONE


                    }
                } else {
                    initLocation()
                }
            }
    }

    fun initAutoComplete() {
        val apiKey = "AIzaSyD5-MyENcRZHlFaCYAUvb0gsyrCv5wVaV4"

        if (!Places.isInitialized()) {
            Places.initialize(requireActivity(), apiKey)
        }
        // Create a new Places client instance.
        // Create a new Places client instance.
        placesClient = Places.createClient(requireActivity())

    }

    fun performSearch(query: String) {
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
                    locations.add(
                        Locationitem(
                            prediction.getFullText(null).toString(),
                            prediction.placeId
                        )
                    )
                }
                var adapter3 = AutoCompleteAdapter(locations, this)
                binding.recyclerviewSearch.setAdapter(adapter3)
                if (locations.size > 0) {
                    binding.recyclerviewSearch.visibility = View.VISIBLE
                } else {
                    binding.recyclerviewSearch.visibility = View.GONE
                }
            }.addOnFailureListener { exception: Exception? ->
                if (exception is ApiException) {
                    Log.e("REEWQ9998", "Place not found: " + exception.statusCode)
                }
            }


    }

    fun showLocation() {
        binding.locationView.visibility = View.VISIBLE
    }

    private fun startPreCaching(dataList: List<com.locatocam.app.data.responses.feed.Data>) {
        val urlList = mutableListOf<String>()
        for (x in dataList) {
            if (x.file_extension_type.equals("video")) {
                urlList.add(x.file.toString())
            }
        }
        val inputData =
            Data.Builder().putStringArray(Constants.KEY_STORIES_LIST_DATA, urlList.toTypedArray())
                .build()
        val preCachingWork = OneTimeWorkRequestBuilder<PreCachingService>().setInputData(inputData)
            .build()
        WorkManager.getInstance(requireContext())
            .enqueue(preCachingWork)
    }

    override fun onDestroy() {
        // if (binding.recMain != null) binding.recMain.releasePlayer()
        super.onDestroy()
    }


    fun preloadImages(dataList: List<com.locatocam.app.data.responses.feed.Data>) {
        lifecycleScope.launchWhenCreated {
            for (x in dataList) {
                Glide.with(this@HomeFragment)
                    .load(x.screenshot)
                    .fitCenter() // Or whatever transformation you want
                    .preload(200, 200); // Or whatever width and height you want

                if (!x.banner_image.equals("")) {
                    Glide.with(this@HomeFragment)
                        .load(x.banner_image)
                        .fitCenter() // Or whatever transformation you want
                        .preload(200, 100); // Or whatever width and height you want
                }
            }
        }
    }

    override fun onClickLocationItem(locationitem: Locationitem) {

        binding.recyclerviewSearch.visibility = View.GONE
        var intent = Intent(requireActivity(), MapsActivity::class.java)
        intent.putExtra("address_text", locationitem.name)
        intent.putExtra("place_id", locationitem.placeid)
        startForResult.launch(intent)
        // startActivity(intent)
    }

    override fun onClickAddress(data: com.locatocam.app.data.responses.address.Data) {

//        (activity as MainActivity).showLoader()
        firstCall = false
//        Log.e("TAG location", "getAllFeedsdd: data setted")
        if (activity is MainActivity) {
            var act = activity as MainActivity
//            Log.e(
//                "TAG location",
//                "getAllFeedsdd: data setted" + act.viewModel.lat + "," + act.viewModel.lng
//            )
            act.viewModel.address_text.value = data.customer_address
            act.viewModel.lat = data.latitude!!.toDouble()
            act.viewModel.lng = data.longitude!!.toDouble()
//            Log.e(
//                "TAG location",
//                "getAllFeedsdd: data setted" + act.viewModel.lat + "," + act.viewModel.lng
//            )
            SharedPrefEnc.setPref("selected_lat", data.latitude, context)
            SharedPrefEnc.setPref("selected_lng", data.longitude, context)
            viewModel.getApprovalCounts(data.latitude, data.longitude)
            binding.locationView.visibility = View.GONE
            act.viewModel.add = data.customer_address.toString()
            add = data.customer_address.toString()
            viewModel.getAllFeeds("", act.viewModel.lat, act.viewModel.lng)
            firstCall = true
            MainActivity.firstLoca = false


        }
    }

    val startForResult =
        this.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val inte = result.data
                Log.i("tr4444", inte?.getStringExtra("result_text").toString())
                Log.i("tr4444", inte?.getDoubleExtra("result_lat", 0.0).toString())
                Log.i("tr4444", inte?.getDoubleExtra("result_lng", 0.0).toString())
                if (activity is MainActivity) {
                    var act = activity as MainActivity
                    act.viewModel.address_text.value =
                        inte?.getStringExtra("result_text").toString()
                    act.viewModel.lat = inte?.getDoubleExtra("result_lat", 0.0)!!
                    act.viewModel.lng = inte?.getDoubleExtra("result_lng", 0.0)!!
                    SharedPrefEnc.setPref(
                        "selected_lat",
                        inte?.getDoubleExtra("result_lat", 0.0)!!.toString(), context
                    )
                    SharedPrefEnc.setPref(
                        "selected_lng",
                        inte?.getDoubleExtra("result_lng", 0.0)!!.toString(), context
                    )


                }
            }
        }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity is MainActivity) {
            var act = activity as MainActivity
            act.viewModel.address_text.observe(requireActivity(), {
                binding.myLocation.text = it
                add = it


                try {
                    SharedPrefEnc.setPref("selected_address", it, context)


                } catch (ex: Exception) {

                }
                binding.locationView.visibility = View.GONE
                viewModel.offset = 0
                viewModel._isheader_added = false
                if (firstCall) {
                    viewModel.getAllFeeds("", act.viewModel.lat, act.viewModel.lng)
                }
            })


        }

    }

    override fun onResume() {
        Log.e("TAG", "onResume: ")

//        viewModel._isheader_added = false
        /* Handler().postDelayed(Runnable {
             if (binding.playerContainer.adapter != null) {
                 (binding.playerContainer.adapter as SimpleAdapter).addAgain()
             }
         }, 1000);*/

        super.onResume()
    }

    override
    fun showPopup(v: View) {
        val popup = PopupMenu(requireActivity(), v)
        val inflater: MenuInflater = popup.getMenuInflater()
        inflater.inflate(R.menu.action_manu, popup.getMenu())
        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

            when (item!!.itemId) {
                R.id.edit -> {
                    //Toast.makeText(requireActivity(), item.title, Toast.LENGTH_SHORT).show()
                }
                R.id.delete -> {
                    //Toast.makeText(requireActivity(), item.title, Toast.LENGTH_SHORT).show()
                }
            }

            true
        })
        popup.show()
    }

    override fun like(process: String, post_id: String) {
        viewModel.like(process, post_id.toInt())
    }

    override fun trash(post_id: String, position: Int) {
        viewModel.trash(post_id.toInt())
        (binding.playerContainer.adapter as SimpleAdapter).mediaList.removeAt(position)
        (binding.playerContainer.adapter as SimpleAdapter).notifyDataChanged()
        viewModel.trash.observe(viewLifecycleOwner, {
            Toast.makeText(context, "" + it.message, Toast.LENGTH_SHORT).show()
        })

    }

    override fun isHeaderAdded(): Boolean {
        return viewModel._isheader_added

    }

    override fun addHeader() {
        viewModel._isheader_added = false
        Log.e("TAG", "isHeaderAddedc: ")
    }

    fun openFragment() {

    }

}