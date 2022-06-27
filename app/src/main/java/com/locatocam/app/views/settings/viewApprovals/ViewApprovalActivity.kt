package com.locatocam.app.views.settings.viewApprovals

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
import com.locatocam.app.data.requests.viewApproval.ReqReject
import com.locatocam.app.data.responses.settings.Approved.PageDetails
import com.locatocam.app.data.responses.settings.pendingPost.DataX
import com.locatocam.app.data.responses.settings.pendingPost.Detail
import com.locatocam.app.data.responses.settings.rejectedPost.Data
import com.locatocam.app.databinding.ActivityViewApprovalBinding
import com.locatocam.app.network.Status
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.viewmodels.SettingsViewModel
import com.locatocam.app.views.MainActivity
import com.locatocam.app.views.settings.*
import com.locatocam.app.views.settings.adapters.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.popup_rejected.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
@AndroidEntryPoint
class ViewApprovalActivity : AppCompatActivity(),ApprovedClickEvents, PendingClickEvents,
    RejectedClickEvents {
    lateinit var binding: ActivityViewApprovalBinding
    lateinit var viewModel:SettingsViewModel
    var userId: String?=null
    var process:String="pending"
    var type:String="post"
    private val pageStart: Int = 0
    private var isLoading: Boolean = false
    private var isLastPage: Boolean = false
    private var totalPages: Int = 200
    private val pageSize =10
    private var currentPage: Int = pageStart
    private var currentPageNumber: HashMap<String,PageDetails> = HashMap<String,PageDetails>()
    private var appovedPosts: MutableList<com.locatocam.app.data.responses.settings.Approved.Detail> = ArrayList()
    private var pendingPosts: MutableList<Detail> = ArrayList()
    private var rejectPosts: MutableList<com.locatocam.app.data.responses.settings.rejectedPost.Detail> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityViewApprovalBinding.inflate(layoutInflater)
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
            currentPage=0
            currentPageNumber.clear()
            pendingPosts.clear()
            initMyPendingRecyclerView()
            //setdataPendingPost(type,process)
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
            //setdataRejectedPost(type,process)
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
                //setdataPendingPost(type, process)
            }
            else if(process=="approved"){
                appovedPosts.clear()
                initMyApprovedRecyclerView()
               // setdataApprovedPost(type,process)
            }
            else{
                rejectPosts.clear()
                initMyRejectRecyclerView()
                //setdataRejectedPost(type,process)
            }
            binding.post.setBackgroundResource(R.drawable.button_rnd_red_filled_oval)
            binding.rolls.setBackgroundResource(R.drawable.oval_red_border)
            binding.post.setTextColor(Color.parseColor("#FFFFFFFF"))
            binding.rolls.setTextColor(Color.parseColor("#AC0000"))

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
                //setdataPendingPost(type, process)
            }
            else if(process=="approved"){
                appovedPosts.clear()
                initMyApprovedRecyclerView()
                //setdataApprovedPost(type,process)
            }
            else{
                rejectPosts.clear()
                initMyRejectRecyclerView()
                //setdataRejectedPost(type,process)
            }
            binding.post.setBackgroundResource(R.drawable.oval_red_border)
            binding.rolls.setBackgroundResource(R.drawable.button_rnd_red_filled_oval)
            binding.post.setTextColor(Color.parseColor("#AC0000"))
            binding.rolls.setTextColor(Color.parseColor("#FFFFFFFF"))
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

        val reqViewApproval= ReqViewApproval(
            pageNumber.toString(), process,
            type,
            user_id.toString()
        )
        Log.i("egeegggg",process+":"+type)
        lifecycleScope.launch {
            viewModel.getViewPendingUser(reqViewApproval).collect {
                when(it.status){
                    Status.SUCCESS -> {
                        var totalCount =0;
                        if(type.equals("post")) {
                            lateinit var viewPendingLIst: DataX
                            //MainActivity.binding.loader.visibility= View.GONE
                            viewPendingLIst =  it.data?.data ?: viewPendingLIst
                            totalCount = viewPendingLIst.post
                            pendingPosts.addAll(viewPendingLIst.details)
                            val pendingApprovalAdapter = PendingPostViewApprovalAdapter(pendingPosts,
                                applicationContext,this@ViewApprovalActivity)
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
                        }
                        else if(type.equals("rolls")){
                            lateinit var viewPendingLIst: DataX
                            //MainActivity.binding.loader.visibility= View.GONE
                            viewPendingLIst =  it.data?.data ?: viewPendingLIst
                            totalCount = viewPendingLIst.rolls
                            pendingPosts.addAll(viewPendingLIst.details)
                            val pendingRollsApprovalAdapter = PendingRollsApprovalAdapter(pendingPosts,
                                applicationContext,this@ViewApprovalActivity)
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
                        }
                        pageDetails.totalPages = totalCount/pageSize
                        pageDetails.totalPages = if(totalCount%pageSize ==0) pageDetails.totalPages else pageDetails.totalPages +1
                        totalPages = pageDetails?.totalPages ?: 0
                        isLastPage = pageNumber + 1 == totalPages
                    }
                    Status.LOADING -> {
                        hideLoader()
                        //MainActivity.binding.loader.visibility= View.VISIBLE
                        //showProgress(true,"Fetching Data")
                        Log.e("stateList", "Loading")
                    }
                    Status.ERROR -> {
                        hideLoader()
                        MainActivity.binding.loader.visibility= View.GONE
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
            viewModel.getViewRejectedList(reqViewApproval).collect {
                when(it.status){
                    Status.SUCCESS -> {
                        var totalCount =0
                        if(type.equals("post")) {
                            lateinit var viewRejectedLIst: Data
                            MainActivity.binding.loader.visibility= View.GONE
                            viewRejectedLIst =  it.data?.data ?: viewRejectedLIst
                            totalCount = viewRejectedLIst.post
                            rejectPosts.addAll(viewRejectedLIst.details)
                            val rejectedPostApprovalsAdapter = RejectedPostApprovalsAdapter(rejectPosts,
                                applicationContext,this@ViewApprovalActivity)
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
                        }
                        else {
                            lateinit var viewRejectedLIst: Data
                            MainActivity.binding.loader.visibility= View.GONE
                            viewRejectedLIst =  it.data?.data ?: viewRejectedLIst
                            totalCount = viewRejectedLIst.rolls
                            rejectPosts.addAll(viewRejectedLIst.details)
                            val rejectedRollsApprovalsAdapter = RejectedRollsApprovalsAdapter(rejectPosts,
                                applicationContext,this@ViewApprovalActivity)
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
                        }
                        pageDetails.totalPages = totalCount/pageSize
                        pageDetails.totalPages = if(totalCount%pageSize ==0) pageDetails.totalPages else pageDetails.totalPages +1
                        totalPages = pageDetails?.totalPages ?: 0
                        isLastPage = pageNumber + 1 == totalPages
                    }
                    Status.LOADING -> {
                        MainActivity.binding.loader.visibility= View.VISIBLE
                        //showProgress(true,"Fetching Data")
                        Log.e("stateList", "Loading")
                    }
                    Status.ERROR -> {
                        MainActivity.binding.loader.visibility= View.GONE
                        //showProgress(false,"")
                        Log.e("stateList", it.message.toString())
                    }

                }

            }
        }
    }
    //Approved Reject
    override fun ApprovedReject(
        v: View,
        data: com.locatocam.app.data.responses.settings.Approved.Detail,
        process: String,
        type: String
    ) {
        approvedConfirmReject(data,process,type)
    }
   //Approved View
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
   //Pending Reject
    override fun PendingReject(v: View, data: Detail, process: String, type: String) {
        pendingConfirmReject(data,process,type)
    }
   //Pending View
    override fun Pendingview(v: View, data: Detail, process: String, type: String) {
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
   //PendingApprove
    override fun PendingApprove(v: View, data: Detail, process: String, type: String) {
        pendingConfirmApprove(data,process,type)
    }
   //RejectedView
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
   // Rejected Repost
    override fun RejectedRepost(
        v: View,
        data: com.locatocam.app.data.responses.settings.rejectedPost.Detail,
        process: String,
        type: String
    ) {
       repostConfirmApproval(data,process,type)
    }
    fun approvedConfirmReject(data: com.locatocam.app.data.responses.settings.Approved.Detail, process: String, type: String) {
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
        val reqReject= ReqReject( id.toInt(),
            resaon,
            type,
            user_id.toInt())
        lifecycleScope.launch {
            viewModel.postReject(reqReject).collect {
                when(it.status){
                    Status.SUCCESS -> {
                        MainActivity.binding.loader.visibility= View.GONE
                        if(process.equals("pending")) {
                            isLoading = false
                            isLastPage = false
                            currentPage=0
                            currentPageNumber.clear()
                            pendingPosts.clear()
                            initMyPendingRecyclerView()
                        }
                        else if(process.equals("approved")) {
                            isLoading = false
                            isLastPage = false
                            currentPage=0
                            currentPageNumber.clear()
                            appovedPosts.clear()
                            initMyApprovedRecyclerView()
                        }
                        else if(process.equals("rejected")) {
                            isLoading = false
                            isLastPage = false
                            currentPage=0
                            currentPageNumber.clear()
                            rejectPosts.clear()
                            initMyRejectRecyclerView()
                        }
                    }
                    Status.LOADING -> {
                        MainActivity.binding.loader.visibility= View.VISIBLE
                        //showProgress(true,"Fetching Data")
                        Log.e("stateList", "Loading")
                    }
                    Status.ERROR -> {
                        MainActivity.binding.loader.visibility= View.GONE
                        //showProgress(false,"")
                        Log.e("stateList", it.message.toString())
                    }

                }

            }
        }
    }

    fun pendingConfirmApprove(data: Detail, process: String,type: String) {
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
        val reqApprove= ReqApprove( id.toInt(),
            type,
            user_id.toInt())
        lifecycleScope.launch {
            viewModel.postApprove(reqApprove).collect {
                when(it.status){
                    Status.SUCCESS -> {
                        MainActivity.binding.loader.visibility= View.GONE
                        if(process.equals("pending")) {
                            isLoading = false
                            isLastPage = false
                            currentPage=0
                            currentPageNumber.clear()
                            pendingPosts.clear()
                            initMyPendingRecyclerView()
                        }
                        else if(process.equals("approved")) {
                            isLoading = false
                            isLastPage = false
                            currentPage=0
                            currentPageNumber.clear()
                            appovedPosts.clear()
                            initMyApprovedRecyclerView()
                        }
                        else if(process.equals("rejected")) {
                            isLoading = false
                            isLastPage = false
                            currentPage=0
                            currentPageNumber.clear()
                            rejectPosts.clear()
                            initMyRejectRecyclerView()
                        }
                    }
                    Status.LOADING -> {
                        MainActivity.binding.loader.visibility= View.VISIBLE
                        //showProgress(true,"Fetching Data")
                        Log.e("stateList", "Loading")
                    }
                    Status.ERROR -> {
                        MainActivity.binding.loader.visibility= View.GONE
                        //showProgress(false,"")
                        Log.e("stateList", it.message.toString())
                    }

                }

            }
        }
    }

    fun repostConfirmApproval(data: com.locatocam.app.data.responses.settings.rejectedPost.Detail, process: String,type: String) {
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
        val reqApprove= ReqApprove( id.toInt(),
            type,
            user_id.toInt())
        lifecycleScope.launch {
            viewModel.postrepost(reqApprove).collect {
                when(it.status){
                    Status.SUCCESS -> {
                        MainActivity.binding.loader.visibility= View.GONE
                        if(process.equals("pending")) {
                            isLoading = false
                            isLastPage = false
                            currentPage=0
                            currentPageNumber.clear()
                            pendingPosts.clear()
                            initMyPendingRecyclerView()
                        }
                        else if(process.equals("approved")) {
                            isLoading = false
                            isLastPage = false
                            currentPage=0
                            currentPageNumber.clear()
                            appovedPosts.clear()
                            initMyApprovedRecyclerView()
                        }
                        else if(process.equals("rejected")) {
                            isLoading = false
                            isLastPage = false
                            currentPage=0
                            currentPageNumber.clear()
                            rejectPosts.clear()
                            initMyRejectRecyclerView()
                        }
                    }
                    Status.LOADING -> {
                        MainActivity.binding.loader.visibility= View.VISIBLE
                        //showProgress(true,"Fetching Data")
                        Log.e("stateList", "Loading")
                    }
                    Status.ERROR -> {
                        MainActivity.binding.loader.visibility= View.GONE
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
        val reqViewApproval= ReqViewApproval(
            pageNumber.toString(), process,
            type,
            user_id.toString()
        )
        Log.i("egeegggg",process+":"+type)

        lifecycleScope.launch {
            viewModel.getViewApprovedList(reqViewApproval).collect {
                when(it.status){
                    Status.SUCCESS -> {
                        var totalCount =0
                        if(type.equals("post")) {
                        lateinit var viewApprovedLIst: com.locatocam.app.data.responses.settings.Approved.DataX
                        MainActivity.binding.loader.visibility= View.GONE
                        viewApprovedLIst =  it.data?.data ?: viewApprovedLIst

                            totalCount = viewApprovedLIst.post
                            appovedPosts.addAll(viewApprovedLIst.details)

                           var approvedPostApprovalsAdapter = ApprovedPostApprovalsAdapter(appovedPosts, applicationContext,this@ViewApprovalActivity)
                           binding.Approvedlist.adapter = approvedPostApprovalsAdapter
                                approvedPostApprovalsAdapter.notifyDataSetChanged()
                               // approvedPostApprovalsAdapter.notifyItemChanged(approvedPostApprovalsAdapter.getDetails().size-size)
                                if(pageNumber>0) {
                                    val scrollPosition = pageNumber * 11
                                    binding.Approvedlist.scrollToPosition(scrollPosition)
                                }
                            binding.pending.text="Pending("+viewApprovedLIst.pending+")"
                            binding.approved.text="Approved("+viewApprovedLIst.approved+")"
                            binding.rejected.text="Rejected("+viewApprovedLIst.rejected+")"
                            binding.post.text="Post("+viewApprovedLIst.post+")"
                            binding.rolls.text="Rolls("+viewApprovedLIst.rolls+")"
                        }
                        else{
                            lateinit var viewApprovedLIst: com.locatocam.app.data.responses.settings.Approved.DataX
                            MainActivity.binding.loader.visibility= View.GONE
                            viewApprovedLIst =  it.data?.data ?: viewApprovedLIst
                            appovedPosts.addAll(viewApprovedLIst.details)
                            totalCount = viewApprovedLIst.rolls
                            val approvedRollsApprovalsAdapter = ApprovedRollsApprovalsAdapter(appovedPosts, applicationContext,this@ViewApprovalActivity)
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
                           }
                        pageDetails.totalPages = totalCount/pageSize
                        pageDetails.totalPages = if(totalCount%pageSize ==0) pageDetails.totalPages else pageDetails.totalPages +1
                        totalPages = pageDetails?.totalPages ?: 0
                        isLastPage = pageNumber +1 == totalPages


                    }
                    Status.LOADING -> {
                        MainActivity.binding.loader.visibility= View.VISIBLE
                        //showProgress(true,"Fetching Data")
                        Log.e("stateList", "Loading")
                    }
                    Status.ERROR -> {
                        MainActivity.binding.loader.visibility= View.GONE
                        //showProgress(false,"")
                        Log.e("stateList", it.message.toString())
                    }

                }

            }
        }
    }
    public fun hideLoader(){
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            Handler().postDelayed({
                MainActivity.binding.loader.visibility= View.GONE
            },3000)

            MainActivity.binding.bttmNav.visibility=View.VISIBLE
//            binding.orderOnline.visibility=View.VISIBLE
        }

    }
}