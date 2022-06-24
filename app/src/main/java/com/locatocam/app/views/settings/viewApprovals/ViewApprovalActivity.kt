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
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.locatocam.app.views.home.HomeFragment
import com.locatocam.app.views.settings.*
import com.locatocam.app.views.settings.adapters.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.popup_rejected.view.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
@AndroidEntryPoint
class ViewApprovalActivity : AppCompatActivity(),ApprovedClickEvents, PendingClickEvents,
    RejectedClickEvents {
    lateinit var binding: ActivityViewApprovalBinding
    lateinit var viewModel:SettingsViewModel
    var userId: String?=null
    lateinit var viewPendingLIst: DataX

    lateinit var viewRejectedLIst: Data
     var process:String="pending"
     var type:String="post"
    private val pageStart: Int = 0
    private var isLoading: Boolean = false
    private var isLastPage: Boolean = false
    private var totalPages: Int = 200
    private var currentPage: Int = pageStart
    private var currentPageNumber: HashMap<String,PageDetails> = HashMap<String,PageDetails>()
    private var appovedPosts: MutableList<com.locatocam.app.data.responses.settings.Approved.Detail> = ArrayList()
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
        setdataPendingPost(type, process)
    }

    private fun onClick() {
        binding.pending.setOnClickListener {
            process="pending"
            type = "post"
            binding.Pendinglist.visibility=View.VISIBLE
            binding.Rejectedlist.visibility=View.GONE
            binding.Approvedlist.visibility=View.GONE
            setdataPendingPost(type,process)
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
            process="approved"
            type="post"
            binding.Pendinglist.visibility=View.GONE
            binding.Rejectedlist.visibility=View.GONE
            binding.Approvedlist.visibility=View.VISIBLE
            appovedPosts.clear()
            binding.selectedType.text="List of Post Pending For Approval"
            currentPageNumber.remove("$type#$process")
            initMyOrderRecyclerView()
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
            process="rejected"
            type="post"
            binding.Pendinglist.visibility=View.GONE
            binding.Rejectedlist.visibility=View.VISIBLE
            binding.Approvedlist.visibility=View.GONE
            setdataRejectedPost(type,process)
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
            type="post"
            if(process=="pending") {
                setdataPendingPost(type, process)
            }
            else if(process=="approved"){
                initMyOrderRecyclerView()
               // setdataApprovedPost(type,process)
            }
            else{
                setdataRejectedPost(type,process)
            }
            binding.post.setBackgroundResource(R.drawable.button_rnd_red_filled_oval)
            binding.rolls.setBackgroundResource(R.drawable.oval_red_border)
            binding.post.setTextColor(Color.parseColor("#FFFFFFFF"))
            binding.rolls.setTextColor(Color.parseColor("#AC0000"))

        }
        binding.rolls.setOnClickListener {
            type = "rolls"
            binding.selectedType.text="List of Rolls Pending For Approval"
            if(process=="pending") {

                setdataPendingPost(type, process)
            }
            else if(process=="approved"){
                setdataApprovedPost(type,process)
            }
            else{
                setdataRejectedPost(type,process)
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

    fun setdataPendingPost(type:String, process:String){
        val userId:String=SharedPrefEnc.getPref(MyApp.context,"user_id")
        val user_id:Int=userId.toInt()
        val reqViewApproval= ReqViewApproval(
            "", process,
            type,
            user_id.toString()
        )
        Log.i("egeegggg",process+":"+type)
        lifecycleScope.launch {
            viewModel.getViewPendingUser(reqViewApproval).collect {
                when(it.status){
                    Status.SUCCESS -> {
                        MainActivity.binding.loader.visibility= View.GONE
                        viewPendingLIst =  it.data?.data ?: viewPendingLIst
                        if(type.equals("post")) {

                            val pendingApprovalAdapter = PendingPostViewApprovalAdapter(
                                viewPendingLIst.details,
                                applicationContext,this@ViewApprovalActivity)
                            binding.Pendinglist.adapter = pendingApprovalAdapter
                        }
                        else if(type.equals("rolls")){
                            val pendingRollsApprovalAdapter = PendingRollsApprovalAdapter(viewPendingLIst.details,
                                applicationContext,this@ViewApprovalActivity)
                            binding.Pendinglist.adapter = pendingRollsApprovalAdapter
                        }
                        binding.pending.text="Pending("+viewPendingLIst.pending+")"
                        binding.approved.text="Approved("+viewPendingLIst.approved+")"
                        binding.rejected.text="Rejected("+viewPendingLIst.rejected+")"
                        binding.post.text="Post("+viewPendingLIst.post+")"
                        binding.rolls.text="Rolls("+viewPendingLIst.rolls+")"
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

    fun setdataRejectedPost(type:String, process:String){
        val userId:String=SharedPrefEnc.getPref(MyApp.context,"user_id")
        val user_id:Int=userId.toInt()
        val reqViewApproval= ReqViewApproval(
            "", process,
            type,
            user_id.toString()
        )
        Log.i("egeegggg",process+":"+type)
        lifecycleScope.launch {
            viewModel.getViewRejectedList(reqViewApproval).collect {
                when(it.status){
                    Status.SUCCESS -> {
                        MainActivity.binding.loader.visibility= View.GONE
                        viewRejectedLIst =  it.data?.data ?: viewRejectedLIst
                        if(type.equals("post")) {
                            val rejectedPostApprovalsAdapter = RejectedPostApprovalsAdapter(
                                viewRejectedLIst.details,
                                applicationContext,this@ViewApprovalActivity
                            )
                            binding.Rejectedlist.adapter = rejectedPostApprovalsAdapter
                        }
                        else if(type.equals("rolls")){
                            val rejectedRollsApprovalsAdapter = RejectedRollsApprovalsAdapter(viewRejectedLIst.details,
                                applicationContext,this@ViewApprovalActivity
                            )
                            binding.Rejectedlist.adapter = rejectedRollsApprovalsAdapter
                        }
                        binding.pending.text="Pending("+viewRejectedLIst.pending+")"
                        binding.approved.text="Approved("+viewRejectedLIst.approved+")"
                        binding.rejected.text="Rejected("+viewRejectedLIst.rejected+")"
                        binding.post.text="Post("+viewRejectedLIst.post+")"
                        binding.rolls.text="Rolls("+viewRejectedLIst.rolls+")"
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
                        if(process=="pending") {
                            setdataPendingPost(type, process)
                        }
                        else if(process=="approved") {
                            setdataApprovedPost(type, process)
                        }
                        else if(process=="rejected") {
                            setdataRejectedPost(type, process)
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
                        if(process=="pending") {
                            setdataPendingPost(type, process)
                        }
                        else if(process=="approved") {
                            setdataApprovedPost(type, process)
                        }
                        else if(process=="rejected") {
                            setdataRejectedPost(type, process)
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
                        if(process=="pending") {
                            setdataPendingPost(type, process)
                        }
                        else if(process=="approved") {
                            setdataApprovedPost(type, process)
                        }
                        else if(process=="rejected") {
                            setdataRejectedPost(type, process)
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
    private fun initMyOrderRecyclerView() {
        setdataApprovedPost(type,process)
        //attach adapter to  recycler
        binding.Approvedlist.addOnScrollListener(object : PaginationScrollListener(binding.Approvedlist.layoutManager as LinearLayoutManager) {
            var key = type + "#" + process
            var pageDetails = currentPageNumber.get(key)
            override fun loadMoreItems() {
                isLoading = true
                currentPage = pageDetails?.currentPage ?: 0
                totalPages = pageDetails?.totalPages ?: 0
                isLastPage = currentPage == totalPages

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
                        lateinit var viewApprovedLIst: com.locatocam.app.data.responses.settings.Approved.DataX
                        MainActivity.binding.loader.visibility= View.GONE
                        viewApprovedLIst =  it.data?.data ?: viewApprovedLIst
                        var totalCount =0;
                        var size =1;
                            if(type.equals("post")) {
                            totalCount = viewApprovedLIst.post
                            appovedPosts.addAll(viewApprovedLIst.details)

                            size = viewApprovedLIst.details.size
                           var approvedPostApprovalsAdapter = ApprovedPostApprovalsAdapter(appovedPosts, applicationContext,this@ViewApprovalActivity,totalCount)
                           binding.Approvedlist.adapter = approvedPostApprovalsAdapter
                                approvedPostApprovalsAdapter.notifyDataSetChanged()
                               // approvedPostApprovalsAdapter.notifyItemChanged(approvedPostApprovalsAdapter.getDetails().size-size)
                                if(pageNumber>0) {
                                    val scrollPosition = pageNumber * 11
                                    binding.Approvedlist.scrollToPosition(scrollPosition)
                                }
                        }
                        else if(type.equals("rolls")){
                            val approvedRollsApprovalsAdapter = ApprovedRollsApprovalsAdapter(viewApprovedLIst.details,
                                applicationContext,this@ViewApprovalActivity
                            )
                            totalCount = viewApprovedLIst.rolls
                            size = viewApprovedLIst.details.size
                            binding.Approvedlist.adapter = approvedRollsApprovalsAdapter
                           }
                        if(size > 0){
                            pageDetails.totalPages = totalCount/size
                            pageDetails.totalPages = if(totalCount%size ==0) pageDetails.totalPages else pageDetails.totalPages +1
                        }
                        binding.pending.text="Pending("+viewApprovedLIst.pending+")"
                        binding.approved.text="Approved("+viewApprovedLIst.approved+")"
                        binding.rejected.text="Rejected("+viewApprovedLIst.rejected+")"
                        binding.post.text="Post("+viewApprovedLIst.post+")"
                        binding.rolls.text="Rolls("+viewApprovedLIst.rolls+")"


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







}