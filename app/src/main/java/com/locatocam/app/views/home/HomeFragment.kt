package com.locatocam.app.views.home


import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Rect
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.locatocam.app.Activity.OnlineOrderingHelpActivity
import com.locatocam.app.ModalClass.AddView
import com.locatocam.app.R
import com.locatocam.app.databinding.FragmentHomeBinding
import com.locatocam.app.di.module.NetworkModule.Companion.getClient
import com.locatocam.app.network.WebApi
import com.locatocam.app.repositories.HomeRepository
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.services.PreCachingService
import com.locatocam.app.utility.Loader
import com.locatocam.app.utility.PlayerViewAdapter
import com.locatocam.app.utils.Constants
import com.locatocam.app.viewmodels.HomeViewModel
import com.locatocam.app.views.MainActivity
import com.locatocam.app.views.ceratepost.UploadPostmanual
import com.locatocam.app.views.chat.ChatActivity
import com.locatocam.app.views.home.header.HeaderFragment
import com.locatocam.app.views.home.test.Follow
import com.locatocam.app.views.home.test.PostCountData
import com.locatocam.app.views.home.test.SimpleAdapter
import com.locatocam.app.views.home.test.SimpleEvents
import com.locatocam.app.views.location.MapsActivity
import com.locatocam.app.views.search.AdddressAdapter
import com.locatocam.app.views.search.AutoCompleteAdapter
import com.locatocam.app.views.search.ClickEvents
import com.locatocam.app.views.search.Locationitem
import com.locatocam.app.views.settings.SettingsActivity
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.minidev.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


public class HomeFragment : Fragment(), FeedEvents, ClickEvents, SimpleEvents {

    companion object {
        lateinit var binding: FragmentHomeBinding
        lateinit var instance: HomeFragment
        var add: String = ""
        var order_visiblity: Boolean = false
        var orderType: Boolean = false
        var commet: PostCountData? = null
        var follow: Follow? = null
        var firstCall: Boolean = true
        var hideView: Boolean? = false
        lateinit var viewModel: HomeViewModel
        var influencerCode = ""
        lateinit var simpleAdapter: SimpleAdapter
    }


    lateinit var recyclerView:RecyclerView
    private var videoPostIndex = -1
    var latLong: Double = 0.00
    var latlng: Double = 0.00
    var lastCount: Int = -1
    lateinit var placesClient: PlacesClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        Loader(context!!).showLoader()


//        if (!MainActivity.isLoaded) {
        var repository = HomeRepository(requireActivity().application, "")
        var factory = HomeViewModelFactory(repository)
        instance = HomeFragment()
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        var layoutManager = LinearLayoutManager(requireActivity(),LinearLayoutManager.VERTICAL,false)
        binding.recyclerviewSearch.layoutManager = LinearLayoutManager(requireActivity())
        binding.savedAddress.layoutManager = LinearLayoutManager(requireActivity())

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())


        /* var it = arrayListOf<com.locatocam.app.data.responses.feed.Data>();
         it.add(com.locatocam.app.data.responses.feed.Data())
         if (!MainActivity.isLoaded || binding.playerContainer.adapter == null) {
             var adapter = SimpleAdapter(context, it, this, commet, follow)
             binding.playerContainer.setAdapter(adapter)
         }*/
        binding.playerContainer?.adapter
        binding.createpost

        if (SharedPrefEnc.getPref(requireActivity().application, "user_type") == "poc" ||
            SharedPrefEnc.getPref(requireActivity().application, "user_type") == "customer"
        ) {
            binding.createpost.visibility = View.GONE
            binding.shareHeader.visibility = View.GONE
        } else {
            binding.shareHeader.visibility = View.VISIBLE
            binding.createpost.visibility = View.VISIBLE

        }

        if (SharedPrefEnc.getPref(context, "user_type").equals("company")) {
            Log.e("TAG", "onCreafteView: " + SharedPrefEnc.getPref(context, "influencer_code"))

            influencerCode = SharedPrefEnc.getPref(context, "influencer_code")
            binding.profile.visibility = View.GONE
            binding.imgSetting.visibility = View.VISIBLE
            binding.rcTitleCompany.visibility = View.VISIBLE
            binding.imgTitle.visibility = View.GONE
            binding.shareHeader.visibility = View.GONE
            binding.messages.visibility = View.GONE
            binding.imgCompanyInfo.visibility = View.VISIBLE
        } else {
            binding.rcTitleCompany.visibility = View.GONE
            binding.imgTitle.visibility = View.VISIBLE

            binding.messages.visibility = View.VISIBLE
            binding.imgCompanyInfo.visibility = View.GONE
        }


        viewModel.feed_items.observe(viewLifecycleOwner, {
            PlayerViewAdapter.releaseAllPlayers()
            Log.e("TAG", "onCreateViewittt: " + it.size)
            viewModel.loading = false
            CoroutineScope(Dispatchers.Main).launch {
                delay(2500)
//                hideLoader()
                Loader(context!!).hideLoader()
            }

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
                    (binding.playerContainer?.adapter as SimpleAdapter)!!.notifyDataSetChanged()
                }

            }

//            if (viewModel.offset == 0 && binding.playerContainer.adapter == null) {
            if (viewModel.offset == 0) {
                simpleAdapter = SimpleAdapter(context, it, this, commet, follow)
                binding.playerContainer!!.layoutManager=layoutManager
//                binding.playerContainer?.setAdapter(adapter)
                binding.playerContainer!!.adapter=simpleAdapter
            } else {
                if (simpleAdapter != null) {

                   simpleAdapter.addAll(it)
                   simpleAdapter.notifyDataSetChanged()
                }
            }
            commet = object : PostCountData {
                override fun commentCount(commentCount: String, position: Int) {
                    (binding.playerContainer?.adapter as SimpleAdapter).mediaList.get(position).comments_count =
                        commentCount
                    (binding.playerContainer?.adapter as SimpleAdapter)!!.notifyDataSetChanged()
//                    Log.e("TAG", "commentCount: "+it.get(position).comments_count )
                }

                override fun viewCount(viewCount: String?, pisition: Int) {
//                    it.get(pisition).views_count = viewCount
//                    (binding.playerContainer.adapter as SimpleAdapter)!!.notifyDataSetChanged()
                }
            }


/*
            binding.playerContainer.addOnScrollListener(object : RecyclerViewScrollListener() {
                override fun onItemIsFirstVisibleItem(index: Int) {
                }

                override fun onItemIsFirstCompleteVisibleItem(index: Int) {


                }

            })
*/


            startPreCaching(it)
            preloadImages(it)
            searchBrandHeader()

            (activity as MainActivity).hideLoader()

        })


        viewModel.addressresp.observe(viewLifecycleOwner, {
            Log.e("address", it.message!!)
            Log.e("address", it.data.toString())
            val adapter = AdddressAdapter(it.data!!, this)
            binding.savedAddress.adapter = adapter
        })
        viewModel.getAddress(SharedPrefEnc.getPref(requireContext(), "mobile"))


        viewModel.approvalCounts.observe(viewLifecycleOwner, {


            Glide.with(context!!).load(it.data.profile_pic)
                .apply(RequestOptions().override(200, 200)).into(binding.profile)

//            Picasso.with(context) .load(it.data.profile_pic).into(binding.profile)
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
                (activity as MainActivity).hideOrderBtn()
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
        binding.layoutProfile.setOnClickListener {
            //(activity as MainActivity).settingFragmentOpen()
            var intent = Intent(requireActivity(), SettingsActivity::class.java)
            startActivity(intent)
        }
        binding.imgCompanyInfo.setOnClickListener {
            var intent = Intent(requireActivity(), OnlineOrderingHelpActivity::class.java)
            context?.startActivity(intent)
        }

        binding.playerContainer?.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                var completeVisiblesItems = layoutManager.findFirstCompletelyVisibleItemPosition()


                var position: Int = layoutManager.findLastVisibleItemPosition()
                var positionLast: Int = layoutManager.findFirstVisibleItemPosition()
                val rect = Rect()
                layoutManager.findViewByPosition(position)?.getGlobalVisibleRect(rect)




                if (dy>0) {

                    var visibleItemCount = layoutManager.getChildCount()
                    var totalItemCount = layoutManager.getItemCount()



                    if (!viewModel.loading) {
                        if (visibleItemCount + completeVisiblesItems >= totalItemCount - 2) {
                            var act = (requireActivity() as MainActivity)
                            latlng = act.viewModel.lat
                            latLong = act.viewModel.lng
                            viewModel.getAllFeeds(
                                influencerCode,
                                act.viewModel.lat,
                                act.viewModel.lng
                            )

                        }
                    }

                    if (rect.height()>=900) {

                        if (!(binding.playerContainer?.adapter as SimpleAdapter).mediaList.isEmpty()) {

                            videoPostIndex = position
                            Log.e("TAG", "onScrodlled: "+videoPostIndex )
//                            if (!(binding.playerContainer?.adapter as SimpleAdapter).mediaList.get(videoPostIndex).file_extension_type.equals("image")) {
                                PlayerViewAdapter.playIndexThenPausePreviousPlayer(videoPostIndex)
//                            } else {
//                                PlayerViewAdapter.pauseCurrentPlayingVideo()
//                            }
                        }
                    }
                }else{


                    if (rect.height()<=400) {
                        Log.e("TAG", "onScrolledPositionb: "+position+" , "+positionLast )
                        if (!(binding.playerContainer?.adapter as SimpleAdapter).mediaList.isEmpty()) {
                            if (position!=0)
                            videoPostIndex = position-1
//                            if (!(binding.playerContainer?.adapter as SimpleAdapter).mediaList.get(videoPostIndex).file_extension_type.equals("image")) {

                                PlayerViewAdapter.playIndexThenPausePreviousPlayer(videoPostIndex)
                                Log.e("TAGScrollnew", "onScrolled = Video: ")
//                            } else {
//                                PlayerViewAdapter.pauseCurrentPlayingVideo()
//                                Log.e("TAGScrollnew", "onScrolled = Image: ")
//                            }
                        }
                    }

                }
                // play just visible item
                if (completeVisiblesItems != -1) {
/*
                    if (!(binding.playerContainer.adapter as SimpleAdapter).mediaList.isEmpty()) {
                        videoPostIndex = completeVisiblesItems
                        if (!(binding.playerContainer.adapter as SimpleAdapter).mediaList.get(
                                completeVisiblesItems
                            ).file_extension_type.equals("image")
                        ) {

                            PlayerViewAdapter.Companion.playIndexThenPausePreviousPlayer(
                                completeVisiblesItems
                            )
                            Log.e("TAGScrollnew", "onScrolled = Video: ")
                        } else {
                            PlayerViewAdapter.Companion.pauseCurrentPlayingVideo()
                            Log.e("TAGScrollnew", "onScrolled = Image: ")
                        }
                    }
*/



                    //viewCount
                    val apiInterface = getClient()!!.create(WebApi::class.java)
                    val userid = SharedPrefEnc.getPref(context, "user_id")
                    if (completeVisiblesItems > -1) {
                        if (lastCount != completeVisiblesItems) {
                            lastCount = completeVisiblesItems
                            Log.e("TAGScroll", "onItemIsFisssssrstVisibleItem: $lastCount")
                            val jsonObject = JSONObject()

                            jsonObject.put("id", (binding.playerContainer?.adapter as SimpleAdapter).mediaList.get(completeVisiblesItems).post_id)
                            jsonObject.put("view_type", (binding.playerContainer?.adapter as SimpleAdapter).mediaList.get(completeVisiblesItems).type)
                            jsonObject.put("user_id", userid)

                            apiInterface.getAddView(jsonObject)!!.enqueue(object : Callback<AddView> {
                                    override fun onResponse(call: Call<AddView>, response: Response<AddView>) {
                                        /*  commet.viewCount(
                                    response.body()!!.data!!.viewsCount,
                                    pastVisiblesItems
                                )*/
                                        if (binding.playerContainer?.adapter != null) {
                                            Log.e("TAG", "onRespfffonse: " + response.body()!!.data!!.viewsCount)
                                            (binding.playerContainer?.adapter as SimpleAdapter).mediaList.get(completeVisiblesItems).views_count = response.body()?.data?.viewsCount
//                                                (binding.playerContainer.adapter as SimpleAdapter).notifyDataChanged()
                                        }
                                    }


                                    override fun onFailure(call: Call<AddView>, t: Throwable) {}
                                })

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
            MainActivity.binding.layoutBNavigation.visibility = View.VISIBLE
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
//            startActivity(intent)
            startForCreatePostResult.launch(intent)
        }
        initAutoComplete()

        return binding.root
    }

    val startForCreatePostResult =
        this.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.getApprovalCounts(SharedPrefEnc.getPref(context, "selected_lat"), SharedPrefEnc.getPref(context, "selected_lng"))

            }
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
                    var intent = Intent(requireActivity(), MapsActivity::class.java)
                    intent.putExtra("lat", location.latitude.toString())
                    intent.putExtra("lng", location.longitude.toString())
                    intent.putExtra("address", address)
                    intent.putExtra("addressId", "`")
                    startForResult.launch(intent)


                    /* val city: String = addresses[0].getLocality()
                     val state: String = addresses[0].getAdminArea()
                     val country: String = addresses[0].getCountryName()
                     val postalCode: String = addresses[0].getPostalCode()
                     val knownName: String = addresses[0].getFeatureName()*/

                    /* if (activity is MainActivity) {
                         var act = activity as MainActivity
                         act.viewModel.address_text.value = address
                         act.viewModel.lat = location.latitude
                         act.viewModel.lng = location.altitude
                         binding.locationView.visibility = View.GONE


                     }*/


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
        placesClient = Places.createClient(requireActivity())

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
        MainActivity.binding.layoutBNavigation.visibility = View.GONE
        binding.locationView.visibility = View.VISIBLE
        viewModel.getAddress(SharedPrefEnc.getPref(requireContext(), "mobile"))
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
        binding.searchLocation.setText("")
    }

    override fun onClickAddress(data: com.locatocam.app.data.responses.address.Data) {

        firstCall = false
        MainActivity.binding.layoutBNavigation.visibility = View.VISIBLE
        if (activity is MainActivity) {
            viewModel.searchType = "influencer"
            HeaderFragment.userType = "influencer"
            HeaderFragment.infcode = ""
            var act = activity as MainActivity
            Loader(context!!).showLoader()
            act.viewModel.address_text.value = data.customer_address
            act.viewModel.lat = data.latitude!!.toDouble()
            act.viewModel.lng = data.longitude!!.toDouble()
            SharedPrefEnc.setPref("selected_lat", data.latitude, context)
            SharedPrefEnc.setPref("selected_lng", data.longitude, context)
            viewModel.getApprovalCounts(data.latitude, data.longitude)
            binding.locationView.visibility = View.GONE
            act.viewModel.add = data.customer_address.toString()
            add = data.customer_address.toString()
            latlng = data.latitude.toDouble()
            latLong = data.longitude.toDouble()
            viewModel.getAllFeeds(influencerCode, act.viewModel.lat, act.viewModel.lng)
            firstCall = true
            MainActivity.firstLoca = false


        }
    }

    val startForResult =
        this.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                Loader(context!!).showLoader()
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
            latLong = act.viewModel.lat
            latlng = act.viewModel.lng
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
                    viewModel.getAllFeeds(influencerCode, act.viewModel.lat, act.viewModel.lng)
                }
            })

        }

    }

    fun searchBrandHeader() {


/*
        viewModel.searchType=seachType
        var act = activity as MainActivity
        viewModel.getAllFeeds(influencerCode, act.viewModel.lat, act.viewModel.lng,userId)
*/

    }

    override fun onStop() {
        super.onStop()
        PlayerViewAdapter.releaseAllPlayers()
    }

    override fun onResume() {
        Log.e("TAG", "onResume: ")
        super.onResume()
        if (binding.playerContainer?.adapter != null) {
            if ((binding.playerContainer?.adapter as SimpleAdapter) != null) {
                (binding.playerContainer?.adapter as SimpleAdapter).notifyDataChanged()
                if (!(binding.playerContainer?.adapter as SimpleAdapter).mediaList
                        .isEmpty() && videoPostIndex != -1
                ) {
                    if (!(binding.playerContainer?.adapter as SimpleAdapter).mediaList
                            .get(videoPostIndex).file_extension_type
                            .equals("image")
                    ) {
                        PlayerViewAdapter.resumeCurrentPlayingVideo()
                    }
                }
            }
        }
        SimpleAdapter.userClick = true
    }

    override
    fun showPopup(v: View, data: com.locatocam.app.data.responses.address.Data, position: Int) {
        val popup = PopupMenu(requireActivity(), v)
        val inflater: MenuInflater = popup.getMenuInflater()
        inflater.inflate(R.menu.action_manu, popup.getMenu())
        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

            when (item!!.itemId) {
                R.id.edit -> {
                    var intent = Intent(requireActivity(), MapsActivity::class.java)
                    intent.putExtra("lat", data.latitude)
                    intent.putExtra("lng", data.longitude)
                    intent.putExtra("address", data.customer_address.toString())
                    intent.putExtra("flateNo", data.door_no.toString())
                    intent.putExtra("landmark", data.cust_landmark)
                    intent.putExtra("addressId", data.c_address_id)
                    intent.putExtra("address_save_as", data.address_save_as)
                    startForResult.launch(intent)
                }
                R.id.delete -> {
                    //Toast.makeText(requireActivity(), item.title, Toast.LENGTH_SHORT).show()


                    val dialogBuilder = AlertDialog.Builder(activity!!)
                    dialogBuilder.setMessage("Are you sure to delete this address?")

                        .setCancelable(false)
                        .setPositiveButton(
                            "Yes",
                            DialogInterface.OnClickListener { dialogInterface, i ->
                                (binding.savedAddress.adapter as AdddressAdapter).items.removeAt(
                                    position
                                )
                                (binding.savedAddress.adapter as AdddressAdapter).notifyDataSetChanged()
                                viewModel.deleteAddress(data.c_address_id!!)
                                dialogInterface.dismiss()
                            }).setNegativeButton("Cancel",
                            DialogInterface.OnClickListener { dialogInterface, i ->
                                dialogInterface.dismiss()
                            })

                    val alert = dialogBuilder.create()
                    alert.setTitle("Delete Address")
                    alert.show()
                    alert.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(ContextCompat.getColor(context!!, R.color.black))
                    alert.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(ContextCompat.getColor(context!!, R.color.black))


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
        (binding.playerContainer?.adapter as SimpleAdapter).mediaList.removeAt(position)
        (binding.playerContainer?.adapter as SimpleAdapter).notifyDataChanged()
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

    override fun editPost(item: com.locatocam.app.data.responses.feed.Data, position: Int) {
        val intentEdt = Intent(context, UploadPostmanual::class.java)
        intentEdt.putExtra("userId", item.user_id)
        intentEdt.putExtra("postId", item.post_id)
        intentEdt.putExtra("video", item.file)
        intentEdt.putExtra("headline", item.header)
        intentEdt.putExtra("subHeadline", item.subheader)
        intentEdt.putExtra("description", item.description)
        intentEdt.putExtra("thumbnail", item.screenshot)
        intentEdt.putExtra("position", position)
        intentEdt.putExtra("getFile_extension_type", item.file_extension_type)
        intentEdt.putExtra("otherUser", "false")

//        startActivity(intentEdt)
        startForCreatePostResult.launch(intentEdt)
    }


    /*fun showLoader() {
        dialog = Dialog(requireContext(), R.style.AppTheme_Dialog)
        val view = View.inflate(requireContext(), R.layout.progressdialog_item, null)
        dialog?.setContentView(view)
        dialog?.setCancelable(true)
        val progressbar: GifImageView = dialog?.findViewById(R.id.img_loader)!!
        dialog?.show()
    }

    fun hideLoader() {
        if (dialog != null) {
            dialog?.dismiss()
        }
    }*/
    fun feedApi() {
//        var latLong: Double = SharedPrefEnc.getPref(context, "selected_lat").toDouble()
//        var latlng: Double = SharedPrefEnc.getPref(context, "selected_lng").toDouble()
        Log.e("TAG", "feedAfffpi: " + latLong + "," + latlng)

        viewModel.getAllFeeds(influencerCode, latlng, latLong)


    }

    override fun onDestroy() {
        super.onDestroy()
        PlayerViewAdapter.releaseAllPlayers()
    }

    override fun onPause() {
        if (binding.playerContainer?.adapter != null) {
            if ((binding.playerContainer?.adapter as SimpleAdapter) != null) {
                if (!(binding.playerContainer?.adapter as SimpleAdapter).mediaList
                        .isEmpty() && videoPostIndex != -1
                ) {
                    if (!(binding.playerContainer?.adapter as SimpleAdapter).mediaList
                            .get(videoPostIndex).file_extension_type
                            .equals("image")
                    ) {
                        PlayerViewAdapter.pauseCurrentPlayingVideo()
                    }
                }
            }
        }
        super.onPause()
    }

}