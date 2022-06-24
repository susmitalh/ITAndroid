package com.locatocam.app.views.settings.myPostReelsApprovalPending

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.locatocam.app.data.responses.settings.companyPending.Detail
import com.locatocam.app.data.responses.settings.pendingPost.DataX
import com.locatocam.app.data.responses.settings.rejectedPost.Data
import com.locatocam.app.databinding.ActivityMyPostReelsApprovalPendingBinding
import com.locatocam.app.network.Status
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.viewmodels.SettingsViewModel
import com.locatocam.app.views.MainActivity
import com.locatocam.app.views.settings.ViewFileActivity
import com.locatocam.app.views.settings.ViewImageFileActivity
import com.locatocam.app.views.settings.adapters.*
import com.locatocam.app.views.settings.myPostReelsApprovalPending.adapters.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MyPostReelsApprovalPendingActivity : AppCompatActivity(),CompanyPendingClickEvents,
    CompanyApprovedClickEvents,CompanyRejectedClickEvents {
    lateinit var binding:ActivityMyPostReelsApprovalPendingBinding
    lateinit var viewModel: SettingsViewModel
    var userId: String?=null
    lateinit var viewPendingLIst: DataX
    lateinit var viewApprovedLIst: com.locatocam.app.data.responses.settings.Approved.DataX
    lateinit var viewRejectedLIst: Data
    var process:String="pending"
    var type:String="post"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMyPostReelsApprovalPendingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.list.layoutManager = LinearLayoutManager(MyApp.context)
        binding.list.itemAnimator = DefaultItemAnimator()
        viewModel= ViewModelProvider(this).get(SettingsViewModel::class.java)
        onClick()
        setdataPendingPost(type, process)
    }
    private fun onClick() {
        binding.pending.setOnClickListener {
            process="pending"
            type = "post"
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
            binding.selectedType.text="List of Post Pending For Approval"
            setdataApprovedPost(type,process)
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
                setdataApprovedPost(type,process)
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
            viewModel.getMyPostReelsPendingList(reqViewApproval).collect {
                when(it.status){
                    Status.SUCCESS -> {
                        MainActivity.binding.loader.visibility= View.GONE
                        viewPendingLIst =  it.data?.data ?: viewPendingLIst
                        if(type.equals("post")) {
                            val pendingApprovalAdapter = CompanyPendingPostApprovalAdapter(
                                viewPendingLIst.details,
                                applicationContext,this@MyPostReelsApprovalPendingActivity)
                            binding.list.adapter = pendingApprovalAdapter
                        }
                        else if(type.equals("rolls")){
                            val pendingRollsApprovalAdapter = CompanyPendingRollsApprovalAdapter(viewPendingLIst.details,
                                applicationContext,this@MyPostReelsApprovalPendingActivity)
                            binding.list.adapter = pendingRollsApprovalAdapter
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

    fun setdataApprovedPost(type:String, process:String){
        binding.list.adapter=null
        val userId:String=SharedPrefEnc.getPref(MyApp.context,"user_id")
        val user_id:Int=userId.toInt()
        val reqViewApproval= ReqViewApproval(
            "", process,
            type,
            user_id.toString()
        )
        Log.i("egeegggg",process+":"+type)
        lifecycleScope.launch {
            viewModel.getMyPostReelsApprovedList(reqViewApproval).collect {
                when(it.status){
                    Status.SUCCESS -> {
                        MainActivity.binding.loader.visibility= View.GONE
                        viewApprovedLIst =  it.data?.data ?: viewApprovedLIst
                        if(type.equals("post")) {
                            val approvedPostApprovalsAdapter = CompanyApprovedPostApprovalsAdapter(
                                viewApprovedLIst.details,
                                applicationContext,this@MyPostReelsApprovalPendingActivity
                            )
                            binding.list.adapter = approvedPostApprovalsAdapter
                        }
                        else if(type.equals("rolls")){
                            val approvedRollsApprovalsAdapter = CompanyApprovedRollsApprovalsAdapter(viewApprovedLIst.details,
                                applicationContext,this@MyPostReelsApprovalPendingActivity
                            )
                            binding.list.adapter = approvedRollsApprovalsAdapter
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
            viewModel.getMyPostReelsejectedList(reqViewApproval).collect {
                when(it.status){
                    Status.SUCCESS -> {
                        MainActivity.binding.loader.visibility= View.GONE
                        viewRejectedLIst =  it.data?.data ?: viewRejectedLIst
                        if(type.equals("post")) {
                            val rejectedPostApprovalsAdapter = CompanyRejectedPostApprovalsAdapter(
                                viewRejectedLIst.details,
                                applicationContext,this@MyPostReelsApprovalPendingActivity
                            )
                            binding.list.adapter = rejectedPostApprovalsAdapter
                        }
                        else if(type.equals("rolls")){
                            val rejectedRollsApprovalsAdapter = CompanyRejectedRollsApprovalsAdapter(viewRejectedLIst.details,
                                applicationContext,this@MyPostReelsApprovalPendingActivity
                            )
                            binding.list.adapter = rejectedRollsApprovalsAdapter
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