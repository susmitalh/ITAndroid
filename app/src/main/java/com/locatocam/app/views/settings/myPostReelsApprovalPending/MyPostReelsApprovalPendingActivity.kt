package com.locatocam.app.views.settings.myPostReelsApprovalPending

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.libraries.places.internal.it
import com.locatocam.app.MyApp
import com.locatocam.app.R
import com.locatocam.app.data.requests.reqUserProfile.ReqViewApproval
import com.locatocam.app.data.responses.settings.Approved.PageDetails
import com.locatocam.app.data.responses.settings.companyPending.Detail
import com.locatocam.app.data.responses.settings.pendingPost.DataX
import com.locatocam.app.data.responses.settings.rejectedPost.Data
import com.locatocam.app.databinding.ActivityMyPostReelsApprovalPendingBinding
import com.locatocam.app.network.Status
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.viewmodels.SettingsViewModel
import com.locatocam.app.views.MainActivity
import com.locatocam.app.views.settings.PaginationScrollListener
import com.locatocam.app.views.settings.ViewFileActivity
import com.locatocam.app.views.settings.ViewImageFileActivity
import com.locatocam.app.views.settings.adapters.*
import com.locatocam.app.views.settings.myPostReelsApprovalPending.adapters.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
@AndroidEntryPoint
class MyPostReelsApprovalPendingActivity : AppCompatActivity(),CompanyPendingClickEvents,
    CompanyApprovedClickEvents,CompanyRejectedClickEvents {
    lateinit var binding:ActivityMyPostReelsApprovalPendingBinding
    lateinit var viewModel: SettingsViewModel
    var userId: String?=null
    lateinit var viewRejectedLIst: Data
    var process:String="pending"
    var type:String="post"
    private val pageStart: Int = 0
    private var isLoading: Boolean = false
    private var isLastPage: Boolean = false
    private var totalPages: Int = 200
    private val pageSize =10
    private var currentPage: Int = pageStart
    private var currentPageNumber: HashMap<String, PageDetails> = HashMap<String, PageDetails>()
    private var appovedPosts: MutableList<com.locatocam.app.data.responses.settings.Approved.Detail> = ArrayList()
    private var pendingPosts: MutableList<com.locatocam.app.data.responses.settings.pendingPost.Detail> = ArrayList()
    private var rejectPosts: MutableList<com.locatocam.app.data.responses.settings.rejectedPost.Detail> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMyPostReelsApprovalPendingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.Pendinglist.layoutManager = LinearLayoutManager(MyApp.context)
        binding.Pendinglist.itemAnimator = DefaultItemAnimator()
        binding.Approvedlist.layoutManager = LinearLayoutManager(MyApp.context)
        binding.Approvedlist.itemAnimator = DefaultItemAnimator()
        binding.Rejectedlist.layoutManager = LinearLayoutManager(MyApp.context)
        binding.Rejectedlist.itemAnimator = DefaultItemAnimator()
        viewModel= ViewModelProvider(this).get(SettingsViewModel::class.java)
        onClick()
        initMyPendingRecyclerView()
    }
    private fun onClick() {
        binding.pending.setOnClickListener {
            isLoading = false
            isLastPage = false
            process="pending"
            type = "post"
            currentPage=0
            currentPageNumber.clear()
            pendingPosts.clear()
            initMyPendingRecyclerView()
            binding.selectedType.text="List of Post Pending For Approval"
            binding.pending.setBackgroundResource(R.drawable.button_rnd_red_filled_oval)
            binding.approved.setBackgroundResource(R.drawable.oval_red_border)
            binding.rejected.setBackgroundResource(R.drawable.oval_red_border)
            binding.post.setBackgroundResource(R.drawable.button_rnd_red_filled_oval)
            binding.rolls.setBackgroundResource(R.drawable.oval_red_border)
            binding.pending.setTextColor(Color.parseColor("#FFFFFFFF"))
            binding.approved.setTextColor(Color.parseColor("#AC0000"))
            binding.rejected.setTextColor(Color.parseColor("#AC0000"))
            binding.post.setTextColor(Color.parseColor("#FFFFFFFF"))
            binding.rolls.setTextColor(Color.parseColor("#AC0000"))
            binding.brandPending.setBackgroundResource(R.drawable.oval_red_border)
            binding.brandPending.setTextColor(Color.parseColor("#AC0000"))
            binding.brandPending.visibility=View.VISIBLE
        }
        binding.approved.setOnClickListener {
            isLoading = false
            isLastPage = false
            currentPage=0
            currentPageNumber.clear()
            process="approved"
            type="post"
            appovedPosts.clear()
            binding.selectedType.text="List of Post Pending For Approval"
            currentPageNumber.remove("$type#$process")
            initMyApprovedRecyclerView()
            binding.pending.setBackgroundResource(R.drawable.oval_red_border)
            binding.approved.setBackgroundResource(R.drawable.button_rnd_red_filled_oval)
            binding.rejected.setBackgroundResource(R.drawable.oval_red_border)
            binding.post.setBackgroundResource(R.drawable.button_rnd_red_filled_oval)
            binding.rolls.setBackgroundResource(R.drawable.oval_red_border)
            binding.pending.setTextColor(Color.parseColor("#AC0000"))
            binding.approved.setTextColor(Color.parseColor("#FFFFFFFF"))
            binding.rejected.setTextColor(Color.parseColor("#AC0000"))
            binding.post.setTextColor(Color.parseColor("#FFFFFFFF"))
            binding.rolls.setTextColor(Color.parseColor("#AC0000"))
            binding.brandPending.setTextColor(Color.parseColor("#AC0000"))
            binding.brandPending.setBackgroundResource(R.drawable.oval_red_border)
            binding.brandPending.setTextColor(Color.parseColor("#AC0000"))
            binding.brandPending.visibility=View.GONE
        }
        binding.rejected.setOnClickListener {
            isLoading = false
            isLastPage = false
            process="rejected"
            type="post"
            currentPage=0
            currentPageNumber.clear()
            rejectPosts.clear()
            initMyRejectRecyclerView()
            binding.selectedType.text="List of Post Pending For Approval"
            binding.pending.setBackgroundResource(R.drawable.oval_red_border)
            binding.approved.setBackgroundResource(R.drawable.oval_red_border)
            binding.rejected.setBackgroundResource(R.drawable.button_rnd_red_filled_oval)
            binding.post.setBackgroundResource(R.drawable.button_rnd_red_filled_oval)
            binding.rolls.setBackgroundResource(R.drawable.oval_red_border)
            binding.pending.setTextColor(Color.parseColor("#AC0000"))
            binding.approved.setTextColor(Color.parseColor("#AC0000"))
            binding.rejected.setTextColor(Color.parseColor("#FFFFFFFF"))
            binding.post.setTextColor(Color.parseColor("#FFFFFFFF"))
            binding.rolls.setTextColor(Color.parseColor("#AC0000"))
            binding.brandPending.setTextColor(Color.parseColor("#AC0000"))
            binding.brandPending.setBackgroundResource(R.drawable.oval_red_border)
            binding.brandPending.setTextColor(Color.parseColor("#AC0000"))
            binding.brandPending.visibility=View.GONE
        }
        binding.post.setOnClickListener {
            isLoading = false
            isLastPage = false
            type="post"
            currentPage=0
            currentPageNumber.clear()
            if(process=="pending") {
                pendingPosts.clear()
                initMyPendingRecyclerView()
            }
            else if(process=="approved"){
                appovedPosts.clear()
                initMyApprovedRecyclerView()
            }
            else{
                rejectPosts.clear()
                initMyRejectRecyclerView()
            }
            binding.post.setBackgroundResource(R.drawable.button_rnd_red_filled_oval)
            binding.rolls.setBackgroundResource(R.drawable.oval_red_border)
            binding.brandPending.setBackgroundResource(R.drawable.oval_red_border)
            binding.post.setTextColor(Color.parseColor("#FFFFFFFF"))
            binding.rolls.setTextColor(Color.parseColor("#AC0000"))
            binding.brandPending.setTextColor(Color.parseColor("#AC0000"))

        }
        binding.rolls.setOnClickListener {
            isLoading = false
            isLastPage = false
            type = "rolls"
            currentPage=0
            currentPageNumber.clear()
            binding.selectedType.text="List of Rolls Pending For Approval"
            if(process=="pending") {
                pendingPosts.clear()
                initMyPendingRecyclerView()
            }
            else if(process=="approved"){
                appovedPosts.clear()
                initMyApprovedRecyclerView()
            }
            else{
                rejectPosts.clear()
                initMyRejectRecyclerView()
            }
            binding.brandPending.setBackgroundResource(R.drawable.oval_red_border)
            binding.post.setBackgroundResource(R.drawable.oval_red_border)
            binding.rolls.setBackgroundResource(R.drawable.button_rnd_red_filled_oval)
            binding.post.setTextColor(Color.parseColor("#AC0000"))
            binding.brandPending.setTextColor(Color.parseColor("#AC0000"))
            binding.rolls.setTextColor(Color.parseColor("#FFFFFFFF"))
        }
        binding.brandPending.setOnClickListener {
            isLoading = false
            isLastPage = false
            type="brand_pending"
            currentPage=0
            currentPageNumber.clear()
            if(process=="pending") {
                pendingPosts.clear()
                initMyPendingRecyclerView()
            }
            binding.brandPending.setBackgroundResource(R.drawable.button_rnd_red_filled_oval)
            binding.rolls.setBackgroundResource(R.drawable.oval_red_border)
            binding.post.setBackgroundResource(R.drawable.oval_red_border)
            binding.brandPending.setTextColor(Color.parseColor("#FFFFFFFF"))
            binding.rolls.setTextColor(Color.parseColor("#AC0000"))
            binding.post.setTextColor(Color.parseColor("#AC0000"))

        }

        binding.home.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        binding.back.setOnClickListener {
            finish()
        }
    }
    fun getUserID():String{
        return SharedPrefEnc.getPref(MyApp.context,"user_id")
    }
    private fun initMyPendingRecyclerView() {
        setdataPendingPost(type,process)
        //attach adapter to  recycler
        binding.Pendinglist.addOnScrollListener(object : PaginationScrollListener(binding.Pendinglist.layoutManager as LinearLayoutManager) {
            var key = type + "#" + process
            var pageDetails = currentPageNumber.get(key)
            override fun loadMoreItems() {
                isLoading = true
                Handler(Looper.myLooper()!!).postDelayed({
                    setdataPendingPost(type,process)
                    isLoading = false
                }, 1000)
            }
            override fun getTotalPageCount(): Int {
                return totalPages
            }
            override fun isLastPage(): Boolean {
                return isLastPage
            }
            override fun isLoading(): Boolean {
                return isLoading
            }
        })
    }
    fun setdataPendingPost(type:String, process:String){
        binding.Pendinglist.visibility=View.VISIBLE
        binding.Rejectedlist.visibility=View.GONE
        binding.Approvedlist.visibility=View.GONE
        val userId:String=SharedPrefEnc.getPref(MyApp.context,"user_id")
        val user_id:Int=userId.toInt()
        var key = type + "#" + process
        var pageDetails = currentPageNumber.get(key)
        var pageNumber = pageDetails?.currentPage ?: -1;
        ++pageNumber
        if(pageDetails == null) {
            pageDetails = PageDetails(key, pageNumber,0)
            currentPageNumber.put(key,pageDetails);
        }
        pageDetails.currentPage = pageNumber
        currentPage = pageNumber
        var reqViewApproval:ReqViewApproval
        if(type.equals("brand_pending")){
            reqViewApproval=ReqViewApproval(
                pageNumber.toString(), "brand_pending",
                "post",
                user_id.toString()
            )
        }
        else {
            reqViewApproval = ReqViewApproval(
                pageNumber.toString(), process,
                type,
                user_id.toString()
            )
        }
        Log.i("egeegggg",process+":"+type)
        lifecycleScope.launch {
            viewModel.getMyPostReelsPendingList(reqViewApproval).collect {
                when(it.status){
                    Status.SUCCESS -> {
                        var totalCount =0
                        binding.loader.visibility= View.GONE
                        if(type.equals("post")) {
                            lateinit var viewPendingLIst: DataX
                            viewPendingLIst =  it.data?.data ?: viewPendingLIst
                            totalCount = viewPendingLIst.post
                            pendingPosts.addAll(viewPendingLIst.details!!)
                            val pendingApprovalAdapter = CompanyPendingPostApprovalAdapter(
                                pendingPosts,
                                applicationContext,this@MyPostReelsApprovalPendingActivity)
                            binding.Pendinglist.adapter = pendingApprovalAdapter
                            pendingApprovalAdapter.notifyDataSetChanged()
                            if(pageNumber>0) {
                                val scrollPosition = pageNumber * 11
                                binding.Pendinglist.scrollToPosition(scrollPosition)
                            }
                            binding.pending.text="Pending("+viewPendingLIst.pending+")"
                            binding.approved.text="Approved("+viewPendingLIst.approved+")"
                            binding.rejected.text="Rejected("+viewPendingLIst.rejected+")"
                            binding.post.text="Post("+viewPendingLIst.post+")"
                            binding.rolls.text="Rolls("+viewPendingLIst.rolls+")"
                            binding.brandPending.text="Brand Pending("+viewPendingLIst.brand_pending+")"
                        }
                        else if(type.equals("rolls")){
                            binding.loader.visibility= View.GONE
                            lateinit var viewPendingLIst: DataX
                            viewPendingLIst =  it.data?.data ?: viewPendingLIst
                            totalCount = viewPendingLIst.rolls
                            pendingPosts.addAll(viewPendingLIst.details!!)
                            val pendingRollsApprovalAdapter = CompanyPendingRollsApprovalAdapter(pendingPosts,
                                applicationContext,this@MyPostReelsApprovalPendingActivity)
                            binding.Pendinglist.adapter = pendingRollsApprovalAdapter
                            pendingRollsApprovalAdapter.notifyDataSetChanged()
                            if(pageNumber>0) {
                                val scrollPosition = pageNumber * 11
                                binding.Pendinglist.scrollToPosition(scrollPosition)
                            }
                            binding.pending.text="Pending("+viewPendingLIst.pending+")"
                            binding.approved.text="Approved("+viewPendingLIst.approved+")"
                            binding.rejected.text="Rejected("+viewPendingLIst.rejected+")"
                            binding.post.text="Post("+viewPendingLIst.post+")"
                            binding.rolls.text="Rolls("+viewPendingLIst.rolls+")"
                            binding.brandPending.text="Brand Pending("+viewPendingLIst.brand_pending+")"
                        }
                        if(type.equals("brand_pending")) {
                            lateinit var viewPendingLIst: DataX
                            viewPendingLIst =  it.data?.data ?: viewPendingLIst
                            totalCount = viewPendingLIst.brand_pending.toInt()
                            pendingPosts.addAll(viewPendingLIst.details)
                            val pendingApprovalAdapter = CompanyPendingPostApprovalAdapter(
                                pendingPosts,
                                applicationContext,this@MyPostReelsApprovalPendingActivity)
                            binding.Pendinglist.adapter = pendingApprovalAdapter
                            pendingApprovalAdapter.notifyDataSetChanged()
                            if(pageNumber>0) {
                                val scrollPosition = pageNumber * 11
                                binding.Pendinglist.scrollToPosition(scrollPosition)
                            }
                            binding.pending.text="Pending("+viewPendingLIst.pending+")"
                            binding.approved.text="Approved("+viewPendingLIst.approved+")"
                            binding.rejected.text="Rejected("+viewPendingLIst.rejected+")"
                            binding.post.text="Post("+viewPendingLIst.post+")"
                            binding.rolls.text="Rolls("+viewPendingLIst.rolls+")"
                            binding.brandPending.text="Brand Pending("+viewPendingLIst.brand_pending+")"
                        }
                        pageDetails.totalPages = totalCount/pageSize
                        pageDetails.totalPages = if(totalCount%pageSize ==0) pageDetails.totalPages else pageDetails.totalPages +1
                        totalPages = pageDetails?.totalPages ?: 0
                        isLastPage = pageNumber + 1 == totalPages

                    }
                    Status.LOADING -> {
                        binding.loader.visibility= View.VISIBLE
                        //showProgress(true,"Fetching Data")
                        Log.e("stateList", "Loading")
                    }
                    Status.ERROR -> {
                        binding.loader.visibility= View.GONE
                        //showProgress(false,"")
                        Log.e("stateList", it.message.toString())
                    }

                }

            }
        }
    }
    private fun initMyApprovedRecyclerView() {
        setdataApprovedPost(type,process)
        //attach adapter to  recycler
        binding.Approvedlist.addOnScrollListener(object : PaginationScrollListener(binding.Approvedlist.layoutManager as LinearLayoutManager) {
            var key = type + "#" + process
            var pageDetails = currentPageNumber.get(key)
            override fun loadMoreItems() {
                isLoading = true
                Handler(Looper.myLooper()!!).postDelayed({
                    setdataApprovedPost(type,process)
                    isLoading = false
                }, 1000)
            }
            override fun getTotalPageCount(): Int {
                return totalPages
            }
            override fun isLastPage(): Boolean {
                return isLastPage
            }
            override fun isLoading(): Boolean {
                return isLoading
            }
        })
    }
    fun setdataApprovedPost(type:String, process:String){
        binding.Approvedlist.visibility=View.VISIBLE
        binding.Pendinglist.visibility=View.GONE
        binding.Rejectedlist.visibility=View.GONE
        val userId:String=SharedPrefEnc.getPref(MyApp.context,"user_id")
        val user_id:Int=userId.toInt()
        var key = type + "#" + process
        var pageDetails = currentPageNumber.get(key)
        var pageNumber = pageDetails?.currentPage ?: -1;
        ++pageNumber
        if(pageDetails == null) {
            pageDetails = PageDetails(key, pageNumber,0)
            currentPageNumber.put(key,pageDetails);
        }
        pageDetails.currentPage = pageNumber
        currentPage = pageNumber
        val reqViewApproval= ReqViewApproval(
            pageNumber.toString(), process,
            type,
            user_id.toString()
        )
        Log.i("egeegggg",process+":"+type)
        lifecycleScope.launch {
            viewModel.getMyPostReelsApprovedList(reqViewApproval).collect {
                when(it.status){
                    Status.SUCCESS -> {
                        var totalCount =0
                        binding.loader.visibility= View.GONE
                        if(type.equals("post")) {
                            lateinit var viewApprovedLIst: com.locatocam.app.data.responses.settings.Approved.DataX
                            viewApprovedLIst =  it.data?.data ?: viewApprovedLIst
                            totalCount = viewApprovedLIst.post
                            appovedPosts.addAll(viewApprovedLIst.details)
                            val approvedPostApprovalsAdapter = CompanyApprovedPostApprovalsAdapter(
                                appovedPosts,
                                applicationContext,this@MyPostReelsApprovalPendingActivity
                            )
                            binding.Approvedlist.adapter = approvedPostApprovalsAdapter
                            approvedPostApprovalsAdapter.notifyDataSetChanged()
                            if(pageNumber>0) {
                                val scrollPosition = pageNumber * 11
                                binding.Approvedlist.scrollToPosition(scrollPosition)
                            }
                            binding.pending.text="Pending("+viewApprovedLIst.pending+")"
                            binding.approved.text="Approved("+viewApprovedLIst.approved+")"
                            binding.rejected.text="Rejected("+viewApprovedLIst.rejected+")"
                            binding.post.text="Post("+viewApprovedLIst.post+")"
                            binding.rolls.text="Rolls("+viewApprovedLIst.rolls+")"
                            binding.brandPending.text="Brand Pending("+viewApprovedLIst.brand_pending+")"
                        }
                        else if(type.equals("rolls")){
                            lateinit var viewApprovedLIst: com.locatocam.app.data.responses.settings.Approved.DataX
                            viewApprovedLIst =  it.data?.data ?: viewApprovedLIst
                            totalCount = viewApprovedLIst.rolls
                            appovedPosts.addAll(viewApprovedLIst.details)
                            val approvedRollsApprovalsAdapter = CompanyApprovedRollsApprovalsAdapter(appovedPosts,
                                applicationContext,this@MyPostReelsApprovalPendingActivity
                            )
                            binding.Approvedlist.adapter = approvedRollsApprovalsAdapter
                            approvedRollsApprovalsAdapter.notifyDataSetChanged()
                            if(pageNumber>0) {
                                val scrollPosition = pageNumber * 11
                                binding.Approvedlist.scrollToPosition(scrollPosition)
                            }
                            binding.pending.text="Pending("+viewApprovedLIst.pending+")"
                            binding.approved.text="Approved("+viewApprovedLIst.approved+")"
                            binding.rejected.text="Rejected("+viewApprovedLIst.rejected+")"
                            binding.post.text="Post("+viewApprovedLIst.post+")"
                            binding.rolls.text="Rolls("+viewApprovedLIst.rolls+")"
                            binding.brandPending.text="Brand Pending("+viewApprovedLIst.brand_pending+")"
                        }
                        if(type.equals("brand_pending")) {
                            /*lateinit var viewApprovedLIst: com.locatocam.app.data.responses.settings.Approved.DataX
                            viewApprovedLIst =  it.data?.data ?: viewApprovedLIst
                            totalCount = viewApprovedLIst.post
                            appovedPosts.addAll(viewApprovedLIst.details)
                            val approvedPostApprovalsAdapter = CompanyApprovedPostApprovalsAdapter(
                                appovedPosts,
                                applicationContext,this@MyPostReelsApprovalPendingActivity
                            )
                            binding.Approvedlist.adapter = approvedPostApprovalsAdapter
                            approvedPostApprovalsAdapter.notifyDataSetChanged()
                            if(pageNumber>0) {
                                val scrollPosition = pageNumber * 11
                                binding.Approvedlist.scrollToPosition(scrollPosition)
                            }
                            binding.pending.text="Pending("+viewApprovedLIst.pending+")"
                            binding.approved.text="Approved("+viewApprovedLIst.approved+")"
                            binding.rejected.text="Rejected("+viewApprovedLIst.rejected+")"
                            binding.post.text="Post("+viewApprovedLIst.post+")"
                            binding.rolls.text="Rolls("+viewApprovedLIst.rolls+")"
                            binding.brandPending.text="Brand Pending("+viewApprovedLIst.brand_pending+")"*/
                        }
                        pageDetails.totalPages = totalCount/pageSize
                        pageDetails.totalPages = if(totalCount%pageSize ==0) pageDetails.totalPages else pageDetails.totalPages +1
                        totalPages = pageDetails?.totalPages ?: 0
                        isLastPage = pageNumber + 1 == totalPages

                    }
                    Status.LOADING -> {
                        binding.loader.visibility= View.VISIBLE
                        //showProgress(true,"Fetching Data")
                        Log.e("stateList", "Loading")
                    }
                    Status.ERROR -> {
                        binding.loader.visibility= View.GONE
                        //showProgress(false,"")
                        Log.e("stateList", it.message.toString())
                    }

                }

            }
        }
    }
    private fun initMyRejectRecyclerView() {
        setdataRejectedPost(type,process)
        //attach adapter to  recycler
        binding.Rejectedlist.addOnScrollListener(object : PaginationScrollListener(binding.Rejectedlist.layoutManager as LinearLayoutManager) {
            var key = type + "#" + process
            var pageDetails = currentPageNumber.get(key)
            override fun loadMoreItems() {
                isLoading = true
                Handler(Looper.myLooper()!!).postDelayed({
                    setdataRejectedPost(type,process)
                    isLoading = false
                }, 1000)
            }
            override fun getTotalPageCount(): Int {
                return totalPages
            }
            override fun isLastPage(): Boolean {
                return isLastPage
            }
            override fun isLoading(): Boolean {
                return isLoading
            }
        })
    }
    fun setdataRejectedPost(type:String, process:String){
        binding.Pendinglist.visibility=View.GONE
        binding.Rejectedlist.visibility=View.VISIBLE
        binding.Approvedlist.visibility=View.GONE
        val userId:String=SharedPrefEnc.getPref(MyApp.context,"user_id")
        val user_id:Int=userId.toInt()
        var key = type + "#" + process
        var pageDetails = currentPageNumber.get(key)
        var pageNumber = pageDetails?.currentPage ?: -1;
        ++pageNumber
        if(pageDetails == null) {
            pageDetails = PageDetails(key, pageNumber,0)
            currentPageNumber.put(key,pageDetails);
        }
        pageDetails.currentPage = pageNumber
        currentPage = pageNumber
        val reqViewApproval= ReqViewApproval(
            pageNumber.toString(), process,
            type,
            user_id.toString()
        )
        Log.i("egeegggg",process+":"+type)
        lifecycleScope.launch {
            viewModel.getMyPostReelsejectedList(reqViewApproval).collect {
                when(it.status){
                    Status.SUCCESS -> {
                        var totalCount =0
                        binding.loader.visibility= View.GONE
                        if(type.equals("post")) {
                            lateinit var viewRejectedLIst: Data
                            viewRejectedLIst =  it.data?.data ?: viewRejectedLIst
                            totalCount = viewRejectedLIst.post
                            rejectPosts.addAll(viewRejectedLIst.details)
                            val rejectedPostApprovalsAdapter = CompanyRejectedPostApprovalsAdapter(
                                rejectPosts,
                                applicationContext,this@MyPostReelsApprovalPendingActivity
                            )
                            binding.Rejectedlist.adapter = rejectedPostApprovalsAdapter
                            rejectedPostApprovalsAdapter.notifyDataSetChanged()
                            if(pageNumber>0) {
                                val scrollPosition = pageNumber * 11
                                binding.Rejectedlist.scrollToPosition(scrollPosition)
                            }
                            binding.pending.text="Pending("+viewRejectedLIst.pending+")"
                            binding.approved.text="Approved("+viewRejectedLIst.approved+")"
                            binding.rejected.text="Rejected("+viewRejectedLIst.rejected+")"
                            binding.post.text="Post("+viewRejectedLIst.post+")"
                            binding.rolls.text="Rolls("+viewRejectedLIst.rolls+")"
                            binding.brandPending.text="Brand Pending("+viewRejectedLIst.brand_pending+")"

                        }
                        else if(type.equals("rolls")){
                            lateinit var viewRejectedLIst: Data
                            viewRejectedLIst =  it.data?.data ?: viewRejectedLIst
                            totalCount = viewRejectedLIst.rolls
                            rejectPosts.addAll(viewRejectedLIst.details)
                            val rejectedRollsApprovalsAdapter = CompanyRejectedRollsApprovalsAdapter(rejectPosts,
                                applicationContext,this@MyPostReelsApprovalPendingActivity
                            )
                            binding.Rejectedlist.adapter = rejectedRollsApprovalsAdapter
                            rejectedRollsApprovalsAdapter.notifyDataSetChanged()
                            if(pageNumber>0) {
                                val scrollPosition = pageNumber * 11
                                binding.Rejectedlist.scrollToPosition(scrollPosition)
                            }
                            binding.pending.text="Pending("+viewRejectedLIst.pending+")"
                            binding.approved.text="Approved("+viewRejectedLIst.approved+")"
                            binding.rejected.text="Rejected("+viewRejectedLIst.rejected+")"
                            binding.post.text="Post("+viewRejectedLIst.post+")"
                            binding.rolls.text="Rolls("+viewRejectedLIst.rolls+")"
                            binding.brandPending.text="Brand Pending("+viewRejectedLIst.brand_pending+")"
                        }
                        if(type.equals("brand_pending")) {
                            /* lateinit var viewRejectedLIst: Data
                             viewRejectedLIst =  it.data?.data ?: viewRejectedLIst
                             totalCount = viewRejectedLIst.post
                             rejectPosts.addAll(viewRejectedLIst.details)
                             val rejectedPostApprovalsAdapter = CompanyRejectedPostApprovalsAdapter(
                                 rejectPosts,
                                 applicationContext,this@MyPostReelsApprovalPendingActivity
                             )
                             binding.Rejectedlist.adapter = rejectedPostApprovalsAdapter
                             rejectedPostApprovalsAdapter.notifyDataSetChanged()
                             if(pageNumber>0) {
                                 val scrollPosition = pageNumber * 11
                                 binding.Rejectedlist.scrollToPosition(scrollPosition)
                             }
                             binding.pending.text="Pending("+viewRejectedLIst.pending+")"
                             binding.approved.text="Approved("+viewRejectedLIst.approved+")"
                             binding.rejected.text="Rejected("+viewRejectedLIst.rejected+")"
                             binding.post.text="Post("+viewRejectedLIst.post+")"
                             binding.rolls.text="Rolls("+viewRejectedLIst.rolls+")"
                             binding.brandPending.text="Brand Pending("+viewRejectedLIst.brand_pending+")"*/
                        }
                        pageDetails.totalPages = totalCount/pageSize
                        pageDetails.totalPages = if(totalCount%pageSize ==0) pageDetails.totalPages else pageDetails.totalPages +1
                        totalPages = pageDetails?.totalPages ?: 0
                        isLastPage = pageNumber + 1 == totalPages

                    }
                    Status.LOADING -> {
                        binding.loader.visibility= View.VISIBLE
                        //showProgress(true,"Fetching Data")
                        Log.e("stateList", "Loading")
                    }
                    Status.ERROR -> {
                        binding.loader.visibility= View.GONE
                        //showProgress(false,"")
                        Log.e("stateList", it.message.toString())
                    }

                }

            }
        }
    }

    override fun Approvedview(
        v: View,
        data: com.locatocam.app.data.responses.settings.Approved.Detail,
        process: String,
        type: String
    ) {
        if(type=="post"){
            val intent = Intent(this, ViewImageFileActivity::class.java)
            intent.putExtra("file",data.file)
            startActivity(intent)
        }
        else{
            val intent = Intent(this, ViewFileActivity::class.java)
            intent.putExtra("file",data.file)
            startActivity(intent)
        }
    }

    override fun Pendingview(
        v: View,
        data: com.locatocam.app.data.responses.settings.pendingPost.Detail,
        process: String,
        type: String
    ) {
        if(type=="post"){
            val intent = Intent(this, ViewImageFileActivity::class.java)
            intent.putExtra("file",data.file)
            startActivity(intent)
        }
        else{
            val intent = Intent(this, ViewFileActivity::class.java)
            intent.putExtra("file",data.file)
            startActivity(intent)
        }
    }

    override fun Rejectedview(
        v: View,
        data: com.locatocam.app.data.responses.settings.rejectedPost.Detail,
        process: String,
        type: String
    ) {
        if(type=="post"){
            val intent = Intent(this, ViewImageFileActivity::class.java)
            intent.putExtra("file",data.file)
            startActivity(intent)
        }
        else{
            val intent = Intent(this, ViewFileActivity::class.java)
            intent.putExtra("file",data.file)
            startActivity(intent)
        }
    }
}