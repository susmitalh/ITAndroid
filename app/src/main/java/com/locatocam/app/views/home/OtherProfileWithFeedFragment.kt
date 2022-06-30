package com.locatocam.app.views.home


import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
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
import com.app.kardder.util.RecyclerViewScrollListener
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
import com.locatocam.app.Activity.OtherProfileWithFeedActivity
import com.locatocam.app.ModalClass.AddView
import com.locatocam.app.R
import com.locatocam.app.databinding.FragmentOtherUserFeedBinding
import com.locatocam.app.di.module.NetworkModule
import com.locatocam.app.network.WebApi
import com.locatocam.app.repositories.HomeRepository
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.services.PreCachingService
import com.locatocam.app.utility.Loader
import com.locatocam.app.utils.Constants
import com.locatocam.app.viewmodels.HomeViewModel
import com.locatocam.app.views.MainActivity
import com.locatocam.app.views.home.header.HeaderFragmentOtherUser
import com.locatocam.app.views.home.test.*
import com.locatocam.app.views.location.MapsActivity
import com.locatocam.app.views.search.AdddressAdapter
import com.locatocam.app.views.search.AutoCompleteAdapter
import com.locatocam.app.views.search.ClickEvents
import com.locatocam.app.views.search.Locationitem
import com.locatocam.app.views.settings.SettingsActivity
import kotlinx.android.synthetic.main.activity_other_profile_with_feed.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.minidev.json.JSONObject
import pl.droidsonroids.gif.GifImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class OtherProfileWithFeedFragment() : Fragment(), FeedEvents, ClickEvents, SimpleEvents {


    lateinit var viewModel: HomeViewModel
    var lastCount: Int = -1
    lateinit var placesClient: PlacesClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var dialog: Dialog

    companion object {
        var postCountData: PostCountData? = null
        var follow: Follow? = null
        lateinit var instants:OtherProfileWithFeedFragment
        lateinit var inf_code: String
        lateinit var binding: FragmentOtherUserFeedBinding
    }

    lateinit var userid: String
    var _isheader_added = false
    var firstCall: Boolean = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
     Loader(context!!).showLoader()
        binding = FragmentOtherUserFeedBinding.inflate(layoutInflater)
        var repository = HomeRepository(requireActivity().application, "")
        var factory = HomeViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)


        userid = arguments?.getString("user_id").toString()
        inf_code = arguments?.getString("inf_code").toString()

            instants=this
            viewModel.get_post_id = arguments?.getString("post_id").toString()

        Log.i("trfggg", userid + "," + inf_code)

        var layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerviewSearch.layoutManager = LinearLayoutManager(requireActivity())
        binding.savedAddress.layoutManager = LinearLayoutManager(requireActivity())

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())


        viewModel.feed_items.observe(viewLifecycleOwner, {
            Log.e("paggination", "onCrdsceateView: "+viewModel.get_post_id)
            viewModel.loading = false

            CoroutineScope(Dispatchers.Main).launch {
                delay(2500)
                try {
                 Loader(context!!).hideLoader()

                    if (!viewModel.get_post_id.equals("null")) {
                        binding.playerContainer.smoothScrollToPosition(1)
                    }



                } catch (e: Exception) {
                }
            }

            binding.playerContainer.setLayoutManager(layoutManager)

            postCountData = object : PostCountData {
                override fun commentCount(commentCount: String, position: Int) {
//                    it.get(position).comments_count = commentCount
                    Toast.makeText(context, ""+commentCount, Toast.LENGTH_SHORT).show()
                    (binding.playerContainer.adapter as SimpleAdapterOtherprofile).mediaList.get(position).comments_count =
                        commentCount
                    (binding.playerContainer.adapter as SimpleAdapterOtherprofile)!!.notifyDataSetChanged()

//                    (HomeFragment.binding.playerContainer.adapter as SimpleAdapter)!!.notifyDataSetChanged()
                }

                override fun viewCount(viewCount: String?, pisition: Int) {
//                    it.get(pisition).views_count = viewCount
//                    (binding.playerContainer.adapter as SimpleAdapterOtherprofile)!!.notifyDataSetChanged()
                }

            }
            follow = object : Follow {
                override fun follow(position: Int, followStatus: String) {
//                    it.get(position).profile_follow = followStatus


                    for (item in  (binding.playerContainer.adapter as SimpleAdapterOtherprofile).mediaList) {
                        item.profile_follow = followStatus
                    }
                    (binding.playerContainer.adapter as SimpleAdapterOtherprofile)!!.notifyDataSetChanged()

                    if (followStatus.equals("1")) {
                        var count: Int = (it.get(position).profile_follow_count?.toInt()) as Int + 1
                        it.get(position).profile_follow_count = count.toString()
                    } else {
                        var count: Int = (it.get(position).profile_follow_count?.toInt()) as Int - 1
                        it.get(position).profile_follow_count = count.toString()
                    }
                    (binding.playerContainer.adapter as SimpleAdapterOtherprofile)!!.notifyDataSetChanged()
                }

                override fun brandFollow(position: Int, followStatus: String) {
                    it.get(position).brand_follow = followStatus
                    (binding.playerContainer.adapter as SimpleAdapterOtherprofile).notifyDataSetChanged()
                }

            }

            if (viewModel.offset == 0) {
                var adapter =
                    SimpleAdapterOtherprofile(context, it, userid, this, postCountData, follow)
                binding.playerContainer.setAdapter(adapter)
            } else {
                (binding.playerContainer.adapter as SimpleAdapterOtherprofile).addAll(it)
                (binding.playerContainer.adapter as SimpleAdapterOtherprofile)!!.notifyDataSetChanged()
            }





            binding.playerContainer.addOnScrollListener(object : RecyclerViewScrollListener() {
                override fun onItemIsFirstVisibleItem(index: Int) {
                    val apiInterface = NetworkModule.getClient()!!.create(
                        WebApi::class.java
                    )
                    Log.e("TAGScroll", "onItemIsFissrstVisibleItem: $index")

                    val userid = SharedPrefEnc.getPref(context, "user_id")

                    if (index > -1) {
                        if (lastCount != index) {
                            lastCount = index
                            Log.e("TAGScroll", "onItemIsFisssssrstVisibleItem: $lastCount")
                            val jsonObject = JSONObject()
                            jsonObject["id"] = (binding.playerContainer.adapter as SimpleAdapterOtherprofile).mediaList.get(index).post_id
                            jsonObject["view_type"] = (binding.playerContainer.adapter as SimpleAdapterOtherprofile).mediaList.get(index).type
                            jsonObject["user_id"] = userid
                            apiInterface.getAddView(jsonObject)!!.enqueue(object : Callback<AddView> {
                                override fun onResponse(call: Call<AddView>, response: Response<AddView>) {
                                        /*  commet.viewCount(
                                    response.body()!!.data!!.viewsCount,
                                    index
                                )*/
//                                    it.get(index).views_count= response.body()?.data?.viewsCount
                                        (binding.playerContainer.adapter as SimpleAdapterOtherprofile).mediaList.get(index).views_count = response.body()?.data?.viewsCount
                                        (binding.playerContainer.adapter as SimpleAdapterOtherprofile).notifyDataSetChanged()
                                    }


                                    override fun onFailure(call: Call<AddView>, t: Throwable) {}
                                })
                        }
                    }



                }



            })


            startPreCaching(it)
            preloadImages(it)



        })
        //viewModel.getAllFeeds(inf_code,0.00,0.00)

        viewModel.addressresp.observe(viewLifecycleOwner, {
            var adapter = AdddressAdapter(it.data!!, this)
            binding.savedAddress.setAdapter(adapter)
        })
        viewModel.getAddress(SharedPrefEnc.getPref(requireContext(), "mobile"))

        binding.imgUserInfo?.setOnClickListener {
            Toast.makeText(context, "ddd", Toast.LENGTH_SHORT).show()
            var intent=Intent(context,OnlineOrderingHelpActivity::class.java)
            context?.startActivity(intent)
        }

        binding.home.setOnClickListener {

            activity?.finish()

        }
        binding.userImg.setOnClickListener {
            //(activity as OtherProfileWithFeedActivity).openSettingFragment()
            var intent=Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }
        if (MainActivity.firstLoca==true){
            viewModel.getApprovalCounts(MainActivity.lat.toString(), MainActivity.lng.toString())
        }else{
            viewModel.getApprovalCounts(SharedPrefEnc.getPref(context,"selected_lat"),  SharedPrefEnc.getPref(context,"selected_lng"))
        }

        binding.playerContainer.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) { //check for scroll down
                    var visibleItemCount = layoutManager.getChildCount()
                    var totalItemCount = layoutManager.getItemCount()
                    var pastVisiblesItems = layoutManager.findFirstVisibleItemPosition()
                    Log.e("paggination", "onScrolled: "+ viewModel.loading)
                    if (!viewModel.loading) {
                        Log.e("paggination", "onScrolled: counts "+visibleItemCount+","+ totalItemCount+","+pastVisiblesItems)
                        Log.e("paggination", "onScrolled: sum "+(visibleItemCount + pastVisiblesItems) )
                        Log.e("paggination", "onScrolled: total "+totalItemCount.minus(2) )
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount - 2) {
                            Log.v("gt66666", "Last Item Wow !")
                            var act = (requireActivity() as OtherProfileWithFeedActivity)
                            Log.e("TAG location", "onClickAddressNat: " + act.viewModel.lat + "," + act.viewModel.lng

                            )
                            Log.e("TAG", "onClickAddressNat: "+SharedPrefEnc.getPref(context,"selected_lat")+" lng "+SharedPrefEnc.getPref(context,"selected_lng") )

                            var latLong:Double=SharedPrefEnc.getPref(context,"selected_lat").toDouble()
                            var latlng:Double=SharedPrefEnc.getPref(context,"selected_lng").toDouble()

//                            viewModel.getAllFeeds(inf_code, act.viewModel.lat, act.viewModel.lng)
                            viewModel.getAllFeeds(inf_code, latLong, latlng)

                        }
                    }
                }
            }
        })
        binding.close.setOnClickListener {
            binding.locationView.visibility = View.GONE
            OtherProfileWithFeedActivity.layoutOtherBNavigation.visibility=View.VISIBLE
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



        initAutoComplete()
        observe()
        return binding.root
    }


    fun observe() {
        viewModel.approvalCounts.observe(viewLifecycleOwner, {
//            Glide.with(this).load(it.data.profile_pic).into(binding.userImg)
            Glide.with(this).load(it.data.profile_pic)
                .apply(RequestOptions().override(200, 200)).into(binding.userImg)
            MainActivity.binding.orderOnline.visibility = View.VISIBLE
            OtherProfileWithFeedActivity.order_online_otherUser.visibility=View.VISIBLE

            if (it.data.approval_count.toInt() > 0) {
                binding.approvalCounts.visibility = View.VISIBLE
                binding.approvalCounts.setText(it.data.approval_count)
                Log.e("TAG", "getApprovalCountsget: " + it.data.approval_count)
            }

            if (it.data.order_status == "off") {

                HomeFragment.order_visiblity = true
                Handler().postDelayed(Runnable {
                    MainActivity.binding.orderOnline.visibility = View.GONE
                    OtherProfileWithFeedActivity.order_online_otherUser.visibility=View.GONE

                }, 3000)
            } else {
                HomeFragment.order_visiblity = false
            }

            if (it.data.order_type == "show") {
                HomeFragment.orderType = true
            } else {
                HomeFragment.orderType = false
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


                    val address: String =
                        addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                    val city: String = addresses[0].getLocality()
                    val state: String = addresses[0].getAdminArea()
                    val country: String = addresses[0].getCountryName()
                    val postalCode: String = addresses[0].getPostalCode()
                    val knownName: String = addresses[0].getFeatureName()

                    var intent = Intent(requireActivity(), MapsActivity::class.java)
                    intent.putExtra("lat", location.latitude.toString())
                    intent.putExtra("lng", location.longitude.toString())
                    intent.putExtra("address", address)
                    intent.putExtra("addressId", "")
                    startForResult.launch(intent)

                    /*if (activity is OtherProfileWithFeedActivity) {
                        var act = activity as OtherProfileWithFeedActivity
                        act.viewModel.address_text.value = address
                        act.viewModel.lat = location.altitude
                        act.viewModel.lng = location.latitude
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
        Log.e("TAG", "onDestroy: ")
        super.onDestroy()
    }


    fun preloadImages(dataList: List<com.locatocam.app.data.responses.feed.Data>) {
        lifecycleScope.launchWhenCreated {
            for (x in dataList) {
                Glide.with(this@OtherProfileWithFeedFragment)
                    .load(x.screenshot)
                    .fitCenter() // Or whatever transformation you want
                    .preload(200, 200); // Or whatever width and height you want

                if (!x.banner_image.equals("")) {
                    Glide.with(this@OtherProfileWithFeedFragment)
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
        OtherProfileWithFeedActivity.layoutOtherBNavigation.visibility=View.VISIBLE
        firstCall = false
        if (activity is OtherProfileWithFeedActivity) {
          Loader(context!!).showLoader()
            var act = activity as OtherProfileWithFeedActivity

            act.viewModel.address_text.value = data.customer_address
            act.viewModel.lat = data.latitude!!.toDouble()
            act.viewModel.lng = data.longitude!!.toDouble()
            Log.e("TAG", "onClickAddress: "+data.customer_address )

            binding.locationView.visibility = View.GONE

            SharedPrefEnc.setPref("selected_lat", data.latitude, context)
            SharedPrefEnc.setPref("selected_lng", data.longitude, context)
            viewModel.getApprovalCounts(data.latitude, data.longitude)
            HomeFragment.binding.locationView.visibility = View.GONE
            act.viewModel.add = data.customer_address.toString()
            HomeFragment.add = data.customer_address.toString()
            Log.e("TAG", "onClickAddressNewLat: "+SharedPrefEnc.getPref(context,"selected_lat")+" lng "+SharedPrefEnc.getPref(context,"selected_lng") )
            viewModel.getAllFeeds(inf_code, act.viewModel.lat, act.viewModel.lng)
            firstCall = true
            MainActivity.firstLoca=false

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
                if (activity is OtherProfileWithFeedActivity) {
                    var act = activity as OtherProfileWithFeedActivity
                    act.viewModel.address_text.value =
                        inte?.getStringExtra("result_text").toString()
                    act.viewModel.lat = inte?.getDoubleExtra("result_lat", 0.0)!!
                    act.viewModel.lng = inte?.getDoubleExtra("result_text", 0.0)!!
                    HomeFragment.add= inte?.getStringExtra("result_text").toString()
//                    HeaderFragmentOtherUser.binding.myLocation.setText( inte?.getStringExtra("result_text").toString())

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

    fun locationShow(){
        binding.locationView.visibility = View.VISIBLE
        viewModel.getAddress(SharedPrefEnc.getPref(requireContext(), "mobile"))
        OtherProfileWithFeedActivity.layoutOtherBNavigation.visibility=View.GONE

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity is OtherProfileWithFeedActivity) {
            var act = activity as OtherProfileWithFeedActivity
            act.viewModel.address_text.observe(requireActivity(), {

                binding.myLocation.text = " "+it

                binding.locationView.visibility = View.GONE
                viewModel.offset = 0
                viewModel._isheader_added = false
//                viewModel.getAllFeeds(inf_code, act.viewModel.lat, act.viewModel.lng)
            })

        }

        if (activity is OtherProfileWithFeedActivity) {
            var act = activity as OtherProfileWithFeedActivity
            act.viewModel.address_text.observe(requireActivity(), {
                binding.myLocation.text = " "+it
                try {
                    SharedPrefEnc.setPref("selected_address", it, context)


                } catch (ex: Exception) {

                }
                binding.locationView.visibility = View.GONE
                viewModel.offset = 0
                viewModel._isheader_added = false
                if (firstCall) {

                    var latLong: Double = SharedPrefEnc.getPref(context, "selected_lat").toDouble()
                    var latlng: Double = SharedPrefEnc.getPref(context, "selected_lng").toDouble()
//                    viewModel.getAllFeeds(inf_code, MainActivity.lat, MainActivity.lng)
                    viewModel.getAllFeeds(inf_code,latLong, latlng)

                }


            })


        }

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
                    val dialogBuilder = AlertDialog.Builder(activity!!)
                    dialogBuilder.setMessage("Are you sure to delete this address?")

                        .setCancelable(false)
                        .setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->
                            (binding.savedAddress.adapter as AdddressAdapter).items.removeAt(position)
                            (binding.savedAddress.adapter as AdddressAdapter).notifyDataSetChanged()
                            viewModel.deleteAddress(data.c_address_id!!)
                            dialogInterface.dismiss()
                        }).setNegativeButton("Cancel",DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.dismiss()
                        })

                    val alert = dialogBuilder.create()
                    alert.setTitle("Delete Address")
                    alert.show()
                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context!!, R.color.black))
                    alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context!!, R.color.black))


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
        (binding.playerContainer.adapter as SimpleAdapterOtherprofile).mediaList.removeAt(position)
        (binding.playerContainer.adapter as SimpleAdapterOtherprofile).notifyDataSetChanged()
        viewModel.trash.observe(viewLifecycleOwner, {
            Toast.makeText(context, "" + it.message, Toast.LENGTH_SHORT).show()
        })
    }

    override fun isHeaderAdded(): Boolean {
        return _isheader_added
    }

    override fun addHeader() {
        _isheader_added = false
    }


}