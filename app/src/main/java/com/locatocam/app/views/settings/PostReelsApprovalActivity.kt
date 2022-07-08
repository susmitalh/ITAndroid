package com.locatocam.app.views.settings

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.locatocam.app.MyApp
import com.locatocam.app.R
import com.locatocam.app.data.requests.reqUserProfile.ReqViewApproval
import com.locatocam.app.data.requests.viewApproval.ReqApprove
import com.locatocam.app.data.requests.viewApproval.ReqCompanyApprove
import com.locatocam.app.data.requests.viewApproval.ReqCompanyReject
import com.locatocam.app.data.requests.viewApproval.ReqReject
import com.locatocam.app.data.responses.settings.Approved.PageDetails
import com.locatocam.app.data.responses.settings.companyPending.Detail
import com.locatocam.app.databinding.ActivityPostReelsApprovalBinding
import com.locatocam.app.network.Status
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.viewmodels.SettingsViewModel
import com.locatocam.app.views.MainActivity
import com.locatocam.app.views.settings.adapters.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
@AndroidEntryPoint
class PostReelsApprovalActivity : AppCompatActivity(),CompanyPendingClickEvents,CompanyApprovedClickEvents,CompanyRejectedClickEvents {
    lateinit var binding: ActivityPostReelsApprovalBinding
    lateinit var viewModel: SettingsViewModel
    var userId: String?=null
    var process:String="pending"
    var type:String="post"
    private val pageStart: Int = 0
    private var isLoading: Boolean = false
    private var isLastPage: Boolean = false
    private var totalPages: Int = 200
    private val pageSize =10
    private var currentPage: Int = pageStart
    private var currentPageNumber: HashMap<String, PageDetails> = HashMap<String, PageDetails>()
    private var appovedPosts: MutableList<com.locatocam.app.data.responses.settings.companyApproved.Detail> = ArrayList()
    private var pendingPosts: MutableList<Detail> = ArrayList()
    private var rejectPosts: MutableList<com.locatocam.app.data.responses.settings.companyRejected.Detail> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPostReelsApprovalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bundle: Bundle? = intent.extras
        bundle?.let {
            bundle.apply {
                userId = getString("userId")
            }

        }
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
            pendingPosts.clear()
            currentPageNumber.clear()
            currentPageNumber.remove("$type#$process")
            initMyPendingRecyclerView()
            binding.pending.setBackgroundResource(R.drawable.button_rnd_red_filled_oval)
            binding.approved.setBackgroundResource(R.drawable.oval_red_border)
            binding.rejected.setBackgroundResource(R.drawable.oval_red_border)
            binding.pending.setTextColor(Color.parseColor("#FFFFFFFF"))
            binding.approved.setTextColor(Color.parseColor("#AC0000"))
            binding.rejected.setTextColor(Color.parseColor("#AC0000"))
        }
        binding.approved.setOnClickListener {
            isLoading = false
            isLastPage = false
            process="approved"
            type="post"
            appovedPosts.clear()
            currentPageNumber.clear()
            currentPageNumber.remove("$type#$process")
            initMyApprovedRecyclerView()
            binding.pending.setBackgroundResource(R.drawable.oval_red_border)
            binding.approved.setBackgroundResource(R.drawable.button_rnd_red_filled_oval)
            binding.rejected.setBackgroundResource(R.drawable.oval_red_border)
            binding.pending.setTextColor(Color.parseColor("#AC0000"))
            binding.approved.setTextColor(Color.parseColor("#FFFFFFFF"))
            binding.rejected.setTextColor(Color.parseColor("#AC0000"))
        }
        binding.rejected.setOnClickListener {
            isLoading = false
            isLastPage = false
            process="rejected"
            type="post"
            rejectPosts.clear()
            currentPageNumber.clear()
            currentPageNumber.remove("$type#$process")
            initMyRejectRecyclerView()
            binding.pending.setBackgroundResource(R.drawable.oval_red_border)
            binding.approved.setBackgroundResource(R.drawable.oval_red_border)
            binding.rejected.setBackgroundResource(R.drawable.button_rnd_red_filled_oval)
            binding.pending.setTextColor(Color.parseColor("#AC0000"))
            binding.approved.setTextColor(Color.parseColor("#AC0000"))
            binding.rejected.setTextColor(Color.parseColor("#FFFFFFFF"))
            }

        binding.home.setOnClickListener {
            var intent= Intent(applicationContext,MainActivity::class.java)
            intent.putExtra("lat",MainActivity.instances.viewModel.lat)
            intent.putExtra("lng",MainActivity.instances.viewModel.lng)
            intent.putExtra("address",MainActivity.instances.viewModel.add)
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
        setComapanyPendingPost(type,process)
        //attach adapter to  recycler
        binding.Pendinglist.addOnScrollListener(object : PaginationScrollListener(binding.Pendinglist.layoutManager as LinearLayoutManager) {
            var key = type + "#" + process
            var pageDetails = currentPageNumber.get(key)
            override fun loadMoreItems() {
                isLoading = true
                Handler(Looper.myLooper()!!).postDelayed({
                    setComapanyPendingPost(type,process)
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

    fun setComapanyPendingPost(type:String, process:String){
        binding.Pendinglist.visibility=View.VISIBLE
        binding.Rejectedlist.visibility=View.GONE
        binding.Approvedlist.visibility=View.GONE
        val userId:String= SharedPrefEnc.getPref(MyApp.context,"user_id")
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
        Log.i("egeegggg",process+":"+type+":"+userId)
        lifecycleScope.launch {
            viewModel.getCompanyPendingUser(reqViewApproval).collect {
                when(it.status){
                    Status.SUCCESS -> {
                        if(type.equals("post")) {
                            var totalCount =0
                            lateinit var viewPendingLIst: com.locatocam.app.data.responses.settings.companyPending.Data
                            binding.loader.visibility= View.GONE
                            viewPendingLIst =  it.data?.data ?: viewPendingLIst
                            totalCount = viewPendingLIst.pending
                            pendingPosts.addAll(viewPendingLIst.details)
                            val companyPendingApprovalAdapter = CompanyPendingApprovalAdapter(pendingPosts,
                                applicationContext,this@PostReelsApprovalActivity)
                            binding.Pendinglist.adapter = companyPendingApprovalAdapter
                            companyPendingApprovalAdapter.notifyDataSetChanged()
                            binding.pending.text="Pending("+viewPendingLIst.pending+")"
                            binding.approved.text="Approved("+viewPendingLIst.approved+")"
                            binding.rejected.text="Rejected("+viewPendingLIst.rejected+")"
                            if(pageNumber>0) {
                                val scrollPosition = pageNumber * 11
                                binding.Pendinglist.scrollToPosition(scrollPosition)
                            }
                            pageDetails.totalPages = totalCount/pageSize
                            pageDetails.totalPages = if(totalCount%pageSize ==0) pageDetails.totalPages else pageDetails.totalPages +1
                            totalPages = pageDetails?.totalPages ?: 0
                            isLastPage = pageNumber + 1 == totalPages
                        }

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
        setComapanyApprovedPost(type,process)
        //attach adapter to  recycler
        binding.Approvedlist.addOnScrollListener(object : PaginationScrollListener(binding.Approvedlist.layoutManager as LinearLayoutManager) {
            var key = type + "#" + process
            var pageDetails = currentPageNumber.get(key)
            override fun loadMoreItems() {
                isLoading = true
                Handler(Looper.myLooper()!!).postDelayed({
                    setComapanyApprovedPost(type,process)
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
    fun setComapanyApprovedPost(type:String, process:String){
        binding.Approvedlist.visibility=View.VISIBLE
        binding.Pendinglist.visibility=View.GONE
        binding.Rejectedlist.visibility=View.GONE
        val userId:String= SharedPrefEnc.getPref(MyApp.context,"user_id")
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
            viewModel.getCompanyApprovedList(reqViewApproval).collect {
                when(it.status){
                    Status.SUCCESS -> {
                        var totalCount =0
                            lateinit var viewApprovedLIst: com.locatocam.app.data.responses.settings.companyApproved.Data
                            binding.loader.visibility= View.GONE
                            viewApprovedLIst =  it.data?.data ?: viewApprovedLIst
                            totalCount = viewApprovedLIst.approved
                            appovedPosts.addAll(viewApprovedLIst.details)
                            val comaonyApprovedApprovalsAdapter = ComaonyApprovedApprovalsAdapter(appovedPosts,
                                applicationContext,this@PostReelsApprovalActivity)
                            binding.Approvedlist.adapter = comaonyApprovedApprovalsAdapter
                            comaonyApprovedApprovalsAdapter.notifyDataSetChanged()
                            binding.pending.text="Pending("+viewApprovedLIst.pending+")"
                            binding.approved.text="Approved("+viewApprovedLIst.approved+")"
                            binding.rejected.text="Rejected("+viewApprovedLIst.rejected+")"
                            if(pageNumber>0) {
                                val scrollPosition = pageNumber * 11
                                binding.Approvedlist.scrollToPosition(scrollPosition)
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
        setComapanyRejectedPost(type,process)
        //attach adapter to  recycler
        binding.Rejectedlist.addOnScrollListener(object : PaginationScrollListener(binding.Rejectedlist.layoutManager as LinearLayoutManager) {
            var key = type + "#" + process
            var pageDetails = currentPageNumber.get(key)
            override fun loadMoreItems() {
                isLoading = true
                Handler(Looper.myLooper()!!).postDelayed({
                    setComapanyRejectedPost(type,process)
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
    fun setComapanyRejectedPost(type:String, process:String){
        binding.Pendinglist.visibility=View.GONE
        binding.Rejectedlist.visibility=View.VISIBLE
        binding.Approvedlist.visibility=View.GONE
        val userId:String= SharedPrefEnc.getPref(MyApp.context,"user_id")
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
        val reqViewApproval= ReqViewApproval(pageNumber.toString(), process,
            type,
            user_id.toString()
        )
        Log.i("egeegggg",process+":"+type)
        lifecycleScope.launch {
            viewModel.getComapnyRejectedList(reqViewApproval).collect {
                when(it.status){
                    Status.SUCCESS -> {
                        var totalCount =0
                            lateinit var viewRejectedLIst: com.locatocam.app.data.responses.settings.companyRejected.Data
                            binding.loader.visibility= View.GONE
                            viewRejectedLIst =  it.data?.data ?: viewRejectedLIst
                            totalCount = viewRejectedLIst.rejected
                            rejectPosts.addAll(viewRejectedLIst.details)
                            val ComapnyRejectedApprovalsAdapter = ComapnyRejectedApprovalsAdapter(
                                rejectPosts,
                                applicationContext,this@PostReelsApprovalActivity
                            )
                            binding.Rejectedlist.adapter = ComapnyRejectedApprovalsAdapter
                            ComapnyRejectedApprovalsAdapter.notifyDataSetChanged()
                            binding.pending.text="Pending("+viewRejectedLIst.pending+")"
                            binding.approved.text="Approved("+viewRejectedLIst.approved+")"
                            binding.rejected.text="Rejected("+viewRejectedLIst.rejected+")"
                            if(pageNumber>0) {
                                val scrollPosition = pageNumber * 11
                                binding.Rejectedlist.scrollToPosition(scrollPosition)
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
     fun confirmUpload(data: Detail,process: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.popup_rejected)
        dialog.setCanceledOnTouchOutside(false)
        val reason = dialog.findViewById<View>(R.id.reason) as EditText
        val yes = dialog.findViewById<View>(R.id.yes) as Button
        val no = dialog.findViewById<View>(R.id.no) as Button
        no.setOnClickListener { dialog.dismiss() }
        yes.setOnClickListener {
            reject(data.id,process)
            dialog.dismiss()
        }
        dialog.show()
    }

   /* override fun showPopup(v: View, data: Detail, process: String, type: String) {
        confirmUpload(data,process)
    }*/

    fun reject(id:String,process: String){

        val userId:String= SharedPrefEnc.getPref(MyApp.context,"user_id")
        val user_id:Int=userId.toInt()
        val reqReject= ReqReject( id.toInt(),
            "post",
            "",
                    user_id.toInt())
        lifecycleScope.launch {
            viewModel.postReject(reqReject).collect {
                when(it.status){
                    Status.SUCCESS -> {
                        binding.loader.visibility= View.GONE
                        if(process=="pending") {
                            setComapanyPendingPost(type, process)
                        }
                        else if(process=="approved") {
                            setComapanyApprovedPost(type, process)
                        }
                        else if(process=="rejected") {
                            setComapanyRejectedPost(type, process)
                        }
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

    override fun PendingReject(v: View, data: Detail, process: String, type: String) {
        pendingConfirmReject(data,process,type)
    }

    override fun Pendingview(v: View, data: Detail, process: String, type: String) {
        val intent = Intent(this, ViewImageFileActivity::class.java)
        intent.putExtra("file",data.file)
        startActivity(intent)
    }

    override fun PendingApprove(v: View, data: Detail, process: String, type: String) {
        pendingConfirmApprove(data,process,type)
    }

    override fun ApprovedReject(
        v: View,
        data: com.locatocam.app.data.responses.settings.companyApproved.Detail,
        process: String,
        type: String
    ) {
        approvedConfirmReject(data,process,type)
    }

    override fun Approvedview(
        v: View,
        data: com.locatocam.app.data.responses.settings.companyApproved.Detail,
        process: String,
        type: String
    ) {
        val intent = Intent(this, ViewImageFileActivity::class.java)
        intent.putExtra("file",data.file)
        startActivity(intent)
    }

    override fun Rejectedview(
        v: View,
        data: com.locatocam.app.data.responses.settings.companyRejected.Detail,
        process: String,
        type: String
    ) {
        val intent = Intent(this, ViewImageFileActivity::class.java)
        intent.putExtra("file",data.file)
        startActivity(intent)
    }

    override fun RejectedRepost(
        v: View,
        data: com.locatocam.app.data.responses.settings.companyRejected.Detail,
        process: String,
        type: String
    ) {
        repostConfirmApproval(data,process,type)
    }

    fun approvedConfirmReject(data: com.locatocam.app.data.responses.settings.companyApproved.Detail, process: String, type: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.popup_rejected)
        dialog.setCanceledOnTouchOutside(false)
        val reason = dialog.findViewById<View>(R.id.reason) as EditText
        val yes = dialog.findViewById<View>(R.id.yes) as Button
        val no = dialog.findViewById<View>(R.id.no) as Button
        no.setOnClickListener { dialog.dismiss() }
        yes.setOnClickListener {
            reject(data.id,process,type,reason.text.toString())
            dialog.dismiss()
        }
        dialog.show()
    }
    fun pendingConfirmReject(data: Detail, process: String, type: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.popup_rejected)
        dialog.setCanceledOnTouchOutside(false)
        val reason = dialog.findViewById<View>(R.id.reason) as EditText
        val yes = dialog.findViewById<View>(R.id.yes) as Button
        val no = dialog.findViewById<View>(R.id.no) as Button
        no.setOnClickListener { dialog.dismiss() }
        yes.setOnClickListener {
            reject(data.id,process,type,reason.text.toString())
            dialog.dismiss()
        }
        dialog.show()
    }
    fun reject(id:String,process: String,type: String,resaon:String){

        val userId:String= SharedPrefEnc.getPref(MyApp.context,"user_id")
        val user_id:Int=userId.toInt()
        val reqCompanyReject=ReqCompanyReject(
            id.toInt(),
            resaon,
            user_id.toInt()
        )
        lifecycleScope.launch {
            viewModel.postCompanyReject(reqCompanyReject).collect {
                when(it.status){
                    Status.SUCCESS -> {
                        binding.loader.visibility= View.GONE
                        if(process=="pending") {
                            isLoading = false
                            isLastPage = false
                            pendingPosts.clear()
                            currentPageNumber.clear()
                            setComapanyPendingPost(type, process)
                        }
                        else if(process=="approved") {
                            isLoading = false
                            isLastPage = false
                            appovedPosts.clear()
                            currentPageNumber.clear()
                            setComapanyApprovedPost(type, process)
                        }
                        else if(process=="rejected") {
                            isLoading = false
                            isLastPage = false
                            rejectPosts.clear()
                            currentPageNumber.clear()
                            setComapanyRejectedPost(type, process)
                        }
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

    fun pendingConfirmApprove(data: Detail, process: String, type: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.popup_confirmation)
        dialog.setCanceledOnTouchOutside(false)
        val yes = dialog.findViewById<View>(R.id.yes) as Button
        val no = dialog.findViewById<View>(R.id.no) as Button
        no.setOnClickListener { dialog.dismiss() }
        yes.setOnClickListener {
            approve(data.id,process,type)
            dialog.dismiss()
        }
        dialog.show()
    }
    fun approve(id:String,process: String,type: String){

        val userId:String= SharedPrefEnc.getPref(MyApp.context,"user_id")
        val user_id:Int=userId.toInt()
        val reqApprove= ReqCompanyApprove( id.toInt(),
            user_id.toInt())
        lifecycleScope.launch {
            viewModel.postCompanyApprove(reqApprove).collect {
                when(it.status){
                    Status.SUCCESS -> {
                        binding.loader.visibility= View.GONE
                        if(process=="pending") {
                            isLoading = false
                            isLastPage = false
                            pendingPosts.clear()
                            currentPageNumber.clear()
                            setComapanyPendingPost(type, process)
                        }
                        else if(process=="approved") {
                            isLoading = false
                            isLastPage = false
                            appovedPosts.clear()
                            currentPageNumber.clear()
                            setComapanyApprovedPost(type, process)
                        }
                        else if(process=="rejected") {
                            isLoading = false
                            isLastPage = false
                            rejectPosts.clear()
                            currentPageNumber.clear()
                            setComapanyRejectedPost(type, process)
                        }
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

    fun repostConfirmApproval(data: com.locatocam.app.data.responses.settings.companyRejected.Detail, process: String,type: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.popup_confirmation)
        dialog.setCanceledOnTouchOutside(false)
        val tittle = dialog.findViewById<View>(R.id.tittle) as TextView
        val yes = dialog.findViewById<View>(R.id.yes) as Button
        val no = dialog.findViewById<View>(R.id.no) as Button
        tittle.text="Are you sure want to repost?"
        no.setOnClickListener { dialog.dismiss() }
        yes.setOnClickListener {
            repost(data.id,process,type)
            dialog.dismiss()
        }
        dialog.show()
    }

    fun repost(id:String,process: String,type: String){

        val userId:String= SharedPrefEnc.getPref(MyApp.context,"user_id")
        val user_id:Int=userId.toInt()
        val reqApprove= ReqCompanyApprove( id.toInt(),
            user_id.toInt())
        lifecycleScope.launch {
            viewModel.postCompanyRepost(reqApprove).collect {
                when(it.status){
                    Status.SUCCESS -> {
                        binding.loader.visibility= View.GONE
                        if(process.equals("pending")) {
                            setComapanyPendingPost(type, process)
                        }
                        else if(process.equals("approved")) {
                            setComapanyApprovedPost(type, process)
                        }
                        else if(process.equals("rejected")) {
                            setComapanyRejectedPost(type, process)
                        }
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




}