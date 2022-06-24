package com.locatocam.app.views.home.header

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.CompoundButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.locatocam.app.adapter.InfluencerProfileBannerAdapter
import com.locatocam.app.adapter.OtherUserTitleAdapter
import com.locatocam.app.adapter.SearchAdapter
import com.locatocam.app.data.responses.SearchModal.DataSeach
import com.locatocam.app.databinding.FragmentHeaderBinding
import com.locatocam.app.repositories.HeaderRepository
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.utility.OnViewPagerListener
import com.locatocam.app.utility.ViewPagerLayoutManager
import com.locatocam.app.utils.Utils.Companion.hasPermissions
import com.locatocam.app.viewmodels.HeaderViewModel
import com.locatocam.app.views.MainActivity
import com.locatocam.app.views.createrolls.VideoRecorder
import com.locatocam.app.views.home.HomeFragment
import com.locatocam.app.views.home.test.SimpleAdapter
import com.locatocam.app.views.rollsexp.RollsExoplayerActivity


class HeaderFragment : Fragment(), IHeaderEvents {
    companion object {

        lateinit var loginType: String
        lateinit var userid: String
        lateinit var binding: FragmentHeaderBinding
        var infcode=""
        var userType="influencer"

    }


    lateinit var viewModel: HeaderViewModel
    lateinit var dialog: Dialog
    var searchAdapter: SearchAdapter? = null
    var dataList: ArrayList<DataSeach>? = ArrayList()
    var searchBrandList: List<DataSeach?>? = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        /*if (binding == null) {
            val parent = view?.parent as ViewGroup
            parent?.removeView(view)
        }*/
        binding = FragmentHeaderBinding.inflate(layoutInflater)

        binding.myLocation.text = HomeFragment.add.toString()

        Log.e("TAG", "onCreateView: ")

        var repository = HeaderRepository("", requireActivity().application)
        var factory = HeaderViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory).get(HeaderViewModel::class.java)
        userid = SharedPrefEnc.getPref(requireActivity().application, "user_id")
        loginType = SharedPrefEnc.getPref(requireActivity().application, "user_type")



        setObsevers()
        setClickListeners()
        refreshAll()
        showButton()
        return binding.root
    }

    private fun showButton() {

        if (HomeFragment.orderType == true) {
            binding.orderText.visibility = View.VISIBLE
            binding.orderList.visibility = View.VISIBLE
        } else {
            binding.orderText.visibility = View.GONE
            binding.orderList.visibility = View.GONE
        }
        if (HomeFragment.order_visiblity == true) {
            binding.orderDelivery.visibility = View.VISIBLE
            binding.orderDineIn.visibility = View.VISIBLE
            binding.orderPickup.visibility = View.VISIBLE
        } else {
            binding.orderDelivery.visibility = View.GONE
            binding.orderDineIn.visibility = View.GONE
            binding.orderPickup.visibility = View.GONE
        }

        if (SharedPrefEnc.getPref(context,"user_type").equals("company")){
            binding.layoutCompany.visibility = View.VISIBLE
            binding.cardCompanyBrand.visibility = View.VISIBLE
            binding.layoutSearch.visibility = View.GONE
            binding.orderList.visibility = View.GONE
            binding.layoutInfluencer.visibility = View.GONE
            binding.cardMostPopular.visibility = View.GONE
            binding.cardRollsAndShort.visibility = View.GONE
        } else {
            binding.layoutCompany.visibility = View.GONE

        }
    }


    fun setObsevers() {

        viewModel.userDetails.observe(viewLifecycleOwner, {
            Log.e("TAG", "setObseversUser: " + it.data?.user_type)

            var layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            binding.rcCompanyBrand.setLayoutManager(layoutManager)
            var adapter =
                TopBrandsAdapter(it.data?.top_or_our_brands?.brand_details!!, this, it.data.logo)
            binding.rcCompanyBrand.setAdapter(adapter)


            var maxposition = it.data?.logo?.size
            var layoutManagerProfile = ViewPagerLayoutManager(requireActivity(), 0)
            var pos = 0
            layoutManagerProfile.mOnViewPagerListener = object : OnViewPagerListener {
                override fun onInitComplete() {
                }

                override fun onPageRelease(z: Boolean, i: Int) {
                }

                override fun onPageSelected(i: Int, z: Boolean) {
                    pos = i
                }

            }



            binding.imgLeftCompany.setOnClickListener {
                if (pos > 0) {
                    binding.rcCompanyImg.smoothScrollToPosition(pos - 1)
//                    binding.titleRecycler.smoothScrollToPosition(pos - 1)
                    pos--
                }
            }
            binding.imgRightCompany.setOnClickListener {
                if (pos < maxposition?.minus(1)!!) {
                    binding.rcCompanyImg.smoothScrollToPosition(pos + 1)
//                    binding.titleRecycler.smoothScrollToPosition(pos + 1)
                    pos++
                }
            }


            var layoutManagerTitle = ViewPagerLayoutManager(requireActivity(), 0)
            binding.rcCompanyImg.setLayoutManager(layoutManagerTitle)
            var profileAdapter = InfluencerProfileBannerAdapter(it.data?.logo)
            binding.rcCompanyImg.adapter = profileAdapter


            HomeFragment.binding.rcTitleCompany.layoutManager = layoutManagerProfile
            var otherUserAdapter = OtherUserTitleAdapter(it.data?.top_or_our_brands?.brand_details)
            HomeFragment.binding.rcTitleCompany.adapter = otherUserAdapter

            binding.txtPhoneCompanyDetails.text = "  " + it.data?.phone
            binding.txtEmailCompanyDetails.text = "  " + it.data?.email
            binding.txtAddressCompanyDetails.text = "  " + it.data?.address
            binding.txtPostCompanyDetails.text = "  " + it.data?.post
            binding.txtLikeCompanyDetails.text = "  " + it.data?.likes
            binding.txtViewCompanyDetails.text = "  " + it.data?.views
            binding.txtCommentCompanyDetails.text = "  " + it.data?.comments
            binding.txtJoinDateCompanyDetails.text = "  " + it.data?.created
            binding.txtStatusCompanyDetails.text = "  " + it.data?.about
            binding.txtCompanyName.text = it.data?.name

            binding.txtCompanyShare.tag = it.data?.influencer_code
        })

        viewModel.topInfluencer.observe(viewLifecycleOwner, {


            var layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            binding.topInfluencers.setLayoutManager(layoutManager)

            var adapter = TopIfluencerAdapter(it, this)
            binding.topInfluencers.setAdapter(adapter)
        })



        viewModel.mostPopularVideos.observe(viewLifecycleOwner, {

            var layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            binding.mostPopVideos.setLayoutManager(layoutManager)

            var adapter = MostPopularVideosAdapter(it, this)
            binding.mostPopVideos.setAdapter(adapter)
        })


        viewModel.rollsAndShortvdos.observe(viewLifecycleOwner, {

            var layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            binding.rollsVideos.setLayoutManager(layoutManager)

            var adapter = RollsAndShortVideosAdapter(it, this)
            binding.rollsVideos.setAdapter(adapter)



        })
        (requireActivity() as MainActivity).viewModel.address_text.observe(viewLifecycleOwner, {
            refreshAll()
        })
    }

    fun refreshAll() {
        viewModel.getUserDetails(userid)
        viewModel.getTopInfluencersV(userid, "top")
        viewModel.getMostPopularVideos()
        viewModel.getRollsAndShortVideos("")
    }


    fun setClickListeners() {
        viewModel.searchApi(userid, dataList)

//        Toast.makeText(context, ""+SharedPrefEnc.getPref(context?.applicationContext,"darkMode"), Toast.LENGTH_SHORT).show()

        if (SharedPrefEnc.getPref(context?.applicationContext, "darkMode").equals("on")) {
            binding.darkMode.isChecked = true
        } else {
            binding.darkMode.isChecked = false
        }
        binding.txtCompanyShare.setOnClickListener {
            val message: String =
                "https://loca-toca.com/Main/index?si=" + binding.txtCompanyShare.tag
            val share = Intent(Intent.ACTION_SEND)
            share.type = "text/plain"
            share.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(Intent.createChooser(share, "Share"))
        }
        binding.txtShowHide.setOnClickListener {
            if (binding.layoutCompanyMoreDetails.visibility == View.VISIBLE) {
                binding.txtShowHide.text = " Show more info"
                binding.layoutCompanyMoreDetails.visibility = View.GONE
            } else {
                binding.layoutCompanyMoreDetails.visibility = View.VISIBLE
                binding.txtShowHide.text = " Hide more info"
            }
        }


        binding.darkMode.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            if (b == true) {
                SharedPrefEnc.setPref("darkMode", "on", context?.applicationContext)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                SharedPrefEnc.setPref("darkMode", "off", context?.applicationContext)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        })

        binding.allShortVideo.setOnClickListener {
            var intent = Intent(context, RollsExoplayerActivity::class.java)
            context?.startActivity(intent)
        }

        binding.searchCancleImg.setOnClickListener {
            HomeFragment.binding.searchPopup.visibility = View.GONE
            binding.searchBrand.isFocusableInTouchMode = false
            val imm =
                requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(requireView().windowToken, 0)
            MainActivity.binding.bttmNav.visibility = View.VISIBLE
            MainActivity.binding.orderOnline.visibility = View.VISIBLE
            binding.searchCancleImg.visibility = View.GONE
        }


        binding.searchBrand.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                viewModel.filter = binding.searchBrand.getText().toString()

                searchBrandList = viewModel.filter(dataList!!)
                if (searchAdapter != null) {
                    searchAdapter!!.updateList(searchBrandList as ArrayList<DataSeach>?)
                    searchAdapter!!.notifyDataSetChanged()
                }
//                (HomeFragment.binding.searchRecyclerView.adapter as SearchAdapter).updateList(searchBrandList as ArrayList<DataSeach>?)
//                (HomeFragment.binding.searchRecyclerView.adapter as SearchAdapter).notifyDataSetChanged()


            }

            override fun afterTextChanged(s: Editable) {


            }
        })

        binding.searchBrand.setOnClickListener {
            binding.searchBrand.isFocusableInTouchMode = true
            MainActivity.binding.bttmNav.visibility = View.GONE
            MainActivity.binding.orderOnline.visibility = View.GONE
            HomeFragment.binding.searchPopup.visibility = View.VISIBLE
            binding.searchCancleImg.visibility = View.VISIBLE


            HomeFragment.binding.searchRecyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            searchAdapter = SearchAdapter(dataList,this)
            HomeFragment.binding.searchRecyclerView.adapter = searchAdapter


        }
        binding.myLocation.setOnClickListener {
            //Navigation.findNavController(binding.myLocation).navigate(R.id.action_homeFragment_to_locationSearchFragment)
            var parnt = requireActivity() as MainActivity
            parnt.showLocationPopup()

        }

        binding.tiViewAll.setOnClickListener {
            var intent = Intent(requireActivity(), TopInfluencers::class.java)
            //startActivity(intent)
            resultLauncher.launch(intent)
        }

        binding.createRolls.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                check_permissions()
            } else {
                val intent = Intent(requireActivity(), VideoRecorder::class.java)
                startActivity(intent)
            }
        }
    }

    fun check_permissions(): Boolean {
        val PERMISSIONS = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
        )
        if (!hasPermissions(requireContext(), PERMISSIONS)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(PERMISSIONS, 2)
            }
        } else {
            val intent = Intent(requireActivity(), VideoRecorder::class.java)
            startActivity(intent)
            return true
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        Log.e("TAG", "onResumeback: ")

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 2) {
            if (grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(requireContext(), VideoRecorder::class.java)
                startActivity(intent)
            }
        }
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                onItemClick(
                    data?.getStringExtra("user_id").toString(),
                    data?.getStringExtra("inf_code").toString()
                )
            }
        }

    override fun onItemClick(userid: String, inf_code: String) {
        Log.i("kl99999", inf_code + "--" + userid)
        /*  val bundle = bundleOf("user_id" to userid, "inf_code" to inf_code)
          Navigation
              .findNavController(binding.root)
              .navigate(R.id.action_homeFragment_to_otherProfileWithFeedFragment, bundle)*/
        //Navigation.findNavController(binding.root).navigate(R.id.action_homeFragment_to_otherProfileWithFeedFragment)

    }

    override fun onItemMostPopularVideos(user_id: String, inf_code: String) {
        /*  val bundle = bundleOf("user_id" to user_id, "inf_code" to inf_code)
          Navigation
              .findNavController(binding.root)
              .navigate(R.id.action_homeFragment_to_otherProfileWithFeedFragment, bundle)*/
    }

    override fun onItemRollsAndShortVideos(firstid: String) {
        Log.e("TAG", "onItemRollsAndShortVideos: " + firstid)
        var intent = Intent(requireActivity(), RollsExoplayerActivity::class.java)
        intent.putExtra("firstid", firstid)
        startActivity(intent)
    }

    override fun onBrandSearchClick(searchId: String?, influencerCode: String?) {



        HomeFragment.viewModel.searchType=searchId!!
        (HomeFragment.binding.playerContainer.adapter as SimpleAdapter).mediaList.clear()
        HomeFragment.viewModel.offset=0
        HomeFragment.viewModel.lastid=0
        HomeFragment.influencerCode=influencerCode!!
        HomeFragment.viewModel.getAllFeeds(influencerCode, (activity as MainActivity).viewModel.lat,  (activity as MainActivity).viewModel.lng)

        userType=searchId!!
        infcode=influencerCode!!
        viewModel.getMostPopularVideos()
        searchAdapter?.notifyDataSetChanged()
        viewModel.getRollsAndShortVideos(influencerCode!!)
        (binding.rollsVideos.adapter as RollsAndShortVideosAdapter).notifyDataSetChanged()


        HomeFragment.binding.searchPopup.visibility = View.GONE
        binding.searchBrand.isFocusableInTouchMode = false
        val imm =
            requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
        MainActivity.binding.bttmNav.visibility = View.VISIBLE
        MainActivity.binding.orderOnline.visibility = View.VISIBLE
        binding.searchCancleImg.visibility = View.GONE
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity is MainActivity) {
            var act = activity as MainActivity
            act.viewModel.address_text.observe(requireActivity(), {
                binding.myLocation.text = it

            })
        }

    }
}