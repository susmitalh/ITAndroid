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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.locatocam.app.R
import com.locatocam.app.adapter.SearchAdapter
import com.locatocam.app.data.responses.SearchModal.DataSeach
import com.locatocam.app.databinding.FragmentHeaderBinding
import com.locatocam.app.repositories.HeaderRepository
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.utils.Utils.Companion.hasPermissions
import com.locatocam.app.viewmodels.HeaderViewModel
import com.locatocam.app.views.MainActivity
import com.locatocam.app.views.createrolls.VideoRecorder
import com.locatocam.app.views.home.HomeFragment
import com.locatocam.app.views.home.HomeFragment.Companion.binding
import com.locatocam.app.views.rollsexp.RollsExoplayerActivity


class HeaderFragment : Fragment(), IHeaderEvents {
    companion object{

        lateinit var loginType:String
        lateinit var userid:String

        fun onItemClick(userid: String, inf_code: String) {
            Log.i("kl99999", inf_code + "--" + userid)
            val bundle = bundleOf("user_id" to userid, "inf_code" to inf_code)
            Navigation
                .findNavController(binding.root)
                .navigate(R.id.action_homeFragment_to_otherProfileWithFeedFragment, bundle)
            //Navigation.findNavController(binding.root).navigate(R.id.action_homeFragment_to_otherProfileWithFeedFragment)

        }

    }
    lateinit var binding: FragmentHeaderBinding

    lateinit var viewModel: HeaderViewModel
    lateinit var dialog: Dialog

    lateinit var searchAdapter: SearchAdapter


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

       binding.myLocation.text=HomeFragment.add.toString()

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

        if (HomeFragment.orderType==true){
            binding.orderText.visibility = View.VISIBLE
            binding.orderList.visibility = View.VISIBLE
        }else{
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
    }


    fun setObsevers() {

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
        viewModel.getTopInfluencersV(userid,"top")
        viewModel.getMostPopularVideos("")
        viewModel.getRollsAndShortVideos("")
    }


    fun setClickListeners() {
        viewModel.searchApi(userid, dataList)

        binding.allShortVideo.setOnClickListener {
            var intent=Intent(context,RollsExoplayerActivity::class.java)
            context?.startActivity(intent)
        }

        binding.searchCancleImg.setOnClickListener {
            HomeFragment.binding.searchPopup.visibility = View.GONE
            binding.searchBrand.isFocusableInTouchMode = false
            val imm =
                requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(requireView().windowToken, 0)
            MainActivity.binding.bttmNav.visibility=View.VISIBLE
            MainActivity.binding.orderOnline.visibility=View.VISIBLE
            binding.searchCancleImg.visibility=View.GONE
        }


        binding.searchBrand.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                viewModel.filter = binding.searchBrand.getText().toString()

                searchBrandList = viewModel.filter(dataList!!)
                searchAdapter.updateList(searchBrandList as ArrayList<DataSeach>?)
                searchAdapter.notifyDataSetChanged()

            }

            override fun afterTextChanged(s: Editable) {


            }
        })

        binding.searchBrand.setOnClickListener {
            binding.searchBrand.isFocusableInTouchMode = true
            MainActivity.binding.bttmNav.visibility=View.GONE
            MainActivity.binding.orderOnline.visibility=View.GONE
            HomeFragment.binding.searchPopup.visibility = View.VISIBLE
            binding.searchCancleImg.visibility=View.VISIBLE


            HomeFragment.binding.searchRecyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            searchAdapter = SearchAdapter(dataList)
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
        Log.e("TAG", "onResumeback: ", )

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
        val bundle = bundleOf("user_id" to userid, "inf_code" to inf_code)
        Navigation
            .findNavController(binding.root)
            .navigate(R.id.action_homeFragment_to_otherProfileWithFeedFragment, bundle)
     //Navigation.findNavController(binding.root).navigate(R.id.action_homeFragment_to_otherProfileWithFeedFragment)

    }

    override fun onItemMostPopularVideos(user_id: String, inf_code: String) {
        val bundle = bundleOf("user_id" to user_id, "inf_code" to inf_code)
        Navigation
            .findNavController(binding.root)
            .navigate(R.id.action_homeFragment_to_otherProfileWithFeedFragment, bundle)
    }

    override fun onItemRollsAndShortVideos(firstid: String) {
        Log.e("TAG", "onItemRollsAndShortVideos: "+firstid )
        var intent = Intent(requireActivity(), RollsExoplayerActivity::class.java)
        intent.putExtra("firstid", firstid)
        startActivity(intent)
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