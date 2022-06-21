package com.locatocam.app.views.settings

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
    lateinit var viewPendingLIst: com.locatocam.app.data.responses.settings.companyPending.Data
    lateinit var viewApprovedLIst: com.locatocam.app.data.responses.settings.companyApproved.Data
    lateinit var viewRejectedLIst: com.locatocam.app.data.responses.settings.companyRejected.Data
    var process:String="pending"
    var type:String="post"
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
        binding.list.layoutManager = LinearLayoutManager(MyApp.context)
        binding.list.itemAnimator = DefaultItemAnimator()
        viewModel= ViewModelProvider(this).get(SettingsViewModel::class.java)
        onClick()
        setComapanyPendingPost(type, process)

    }

    private fun onClick() {
        binding.pending.setOnClickListener {
            process="pending"
            type = "post"
            setComapanyPendingPost(type,process)
            binding.pending.setBackgroundResource(R.drawable.button_rnd_red_filled_oval)
            binding.approved.setBackgroundResource(R.drawable.oval_red_border)
            binding.rejected.setBackgroundResource(R.drawable.oval_red_border)
            binding.pending.setTextColor(Color.parseColor("#FFFFFFFF"))
            binding.approved.setTextColor(Color.parseColor("#AC0000"))
            binding.rejected.setTextColor(Color.parseColor("#AC0000"))
        }
        binding.approved.setOnClickListener {
            process="approved"
            type="post"
            setComapanyApprovedPost(type,process)
            binding.pending.setBackgroundResource(R.drawable.oval_red_border)
            binding.approved.setBackgroundResource(R.drawable.button_rnd_red_filled_oval)
            binding.rejected.setBackgroundResource(R.drawable.oval_red_border)
            binding.pending.setTextColor(Color.parseColor("#AC0000"))
            binding.approved.setTextColor(Color.parseColor("#FFFFFFFF"))
            binding.rejected.setTextColor(Color.parseColor("#AC0000"))
        }
        binding.rejected.setOnClickListener {
            process="rejected"
            type="post"
            setComapanyRejectedPost(type,process)
            binding.pending.setBackgroundResource(R.drawable.oval_red_border)
            binding.approved.setBackgroundResource(R.drawable.oval_red_border)
            binding.rejected.setBackgroundResource(R.drawable.button_rnd_red_filled_oval)
            binding.pending.setTextColor(Color.parseColor("#AC0000"))
            binding.approved.setTextColor(Color.parseColor("#AC0000"))
            binding.rejected.setTextColor(Color.parseColor("#FFFFFFFF"))
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

    fun setComapanyPendingPost(type:String, process:String){

        val userId:String= SharedPrefEnc.getPref(MyApp.context,"user_id")
        val user_id:Int=userId.toInt()
        val reqViewApproval= ReqViewApproval(
            "", process,
            type,
            user_id.toString()
        )
        Log.i("egeegggg",process+":"+type+":"+userId)
        lifecycleScope.launch {
            viewModel.getCompanyPendingUser(reqViewApproval).collect {
                when(it.status){
                    Status.SUCCESS -> {
                        MainActivity.binding.loader.visibility= View.GONE
                        viewPendingLIst =  it.data?.data ?: viewPendingLIst
                        if(type.equals("post")) {
                            val companyPendingApprovalAdapter = CompanyPendingApprovalAdapter(viewPendingLIst.details,
                                applicationContext,this@PostReelsApprovalActivity)
                            binding.list.adapter = companyPendingApprovalAdapter
                        }

                        binding.pending.text="Pending("+viewPendingLIst.pending+")"
                        binding.approved.text="Approved("+viewPendingLIst.approved+")"
                        binding.rejected.text="Rejected("+viewPendingLIst.rejected+")"

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
    fun setComapanyApprovedPost(type:String, process:String){
        binding.list.adapter=null
        val userId:String= SharedPrefEnc.getPref(MyApp.context,"user_id")
        val user_id:Int=userId.toInt()
        val reqViewApproval= ReqViewApproval(
            "", process,
            type,
            user_id.toString()
        )
        Log.i("egeegggg",process+":"+type)
        lifecycleScope.launch {
            viewModel.getCompanyApprovedList(reqViewApproval).collect {
                when(it.status){
                    Status.SUCCESS -> {
                        MainActivity.binding.loader.visibility= View.GONE
                        viewApprovedLIst =  it.data?.data ?: viewApprovedLIst
                        if(type.equals("post")) {
                            val comaonyApprovedApprovalsAdapter = ComaonyApprovedApprovalsAdapter(
                                viewApprovedLIst.details,
                                applicationContext,this@PostReelsApprovalActivity)
                            binding.list.adapter = comaonyApprovedApprovalsAdapter
                        }

                        binding.pending.text="Pending("+viewPendingLIst.pending+")"
                        binding.approved.text="Approved("+viewPendingLIst.approved+")"
                        binding.rejected.text="Rejected("+viewPendingLIst.rejected+")"
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
    fun setComapanyRejectedPost(type:String, process:String){

        val userId:String= SharedPrefEnc.getPref(MyApp.context,"user_id")
        val user_id:Int=userId.toInt()
        val reqViewApproval= ReqViewApproval(
            "", process,
            type,
            user_id.toString()
        )
        Log.i("egeegggg",process+":"+type)
        lifecycleScope.launch {
            viewModel.getComapnyRejectedList(reqViewApproval).collect {
                when(it.status){
                    Status.SUCCESS -> {
                        MainActivity.binding.loader.visibility= View.GONE
                        viewRejectedLIst =  it.data?.data ?: viewRejectedLIst
                        if(type.equals("post")) {
                            val ComapnyRejectedApprovalsAdapter = ComapnyRejectedApprovalsAdapter(
                                viewRejectedLIst.details,
                                applicationContext,this@PostReelsApprovalActivity
                            )
                            binding.list.adapter = ComapnyRejectedApprovalsAdapter
                        }
                        binding.pending.text="Pending("+viewPendingLIst.pending+")"
                        binding.approved.text="Approved("+viewPendingLIst.approved+")"
                        binding.rejected.text="Rejected("+viewPendingLIst.rejected+")"
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
                        MainActivity.binding.loader.visibility= View.GONE
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
                        MainActivity.binding.loader.visibility= View.GONE
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
                        MainActivity.binding.loader.visibility= View.GONE
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
                        MainActivity.binding.loader.visibility= View.GONE
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