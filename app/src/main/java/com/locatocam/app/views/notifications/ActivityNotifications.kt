package com.locatocam.app.views.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.locatocam.app.MyApp
import com.locatocam.app.custom.TestYu
import com.locatocam.app.data.requests.ReqMyActivity
import com.locatocam.app.data.responses.my_activity.Data
import com.locatocam.app.data.responses.settings.Approved.PageDetails
import com.locatocam.app.databinding.ActivityNotificationsBinding
import com.locatocam.app.databinding.ActivityViewBinding
import com.locatocam.app.network.Status
import com.locatocam.app.repositories.ViewActivityRepository
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.viewmodels.ViewActivityViewModel
import com.locatocam.app.views.settings.PaginationScrollListener
import com.locatocam.app.views.view_activity.ViewActivityAdapter
import com.locatocam.app.views.view_activity.ViewActivityViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ActivityNotifications : AppCompatActivity() {
    lateinit var binding: ActivityNotificationsBinding
    lateinit var viewmodel: ViewActivityViewModel
    private val pageStart: Int = 0
    private var isLoading: Boolean = false
    private var isLastPage: Boolean = false
    private var totalPages: Int = 200
    private val pageSize =10
    private var currentPage: Int = pageStart
    private var currentPageNumber: HashMap<String, PageDetails> = HashMap<String, PageDetails>()
    private var pendingPosts: MutableList<com.locatocam.app.data.responses.notification.Data> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var repository= ViewActivityRepository(application)
        var factory= ViewActivityViewModelFactory(repository)
        viewmodel= ViewModelProvider(this,factory).get(ViewActivityViewModel::class.java)

        setOnClickListeners()
        initRecyclerView()


        /*  var br=object :BroadcastReceiver(){
              override fun onReceive(p0: Context?, p1: Intent?) {
                 Log.i("uijjj", p1?.getIntExtra("thg",4).toString())
              }

          }
          registerReceiver(br, IntentFilter("hnmm"))

          sendBroadcast(Intent("hnmm").putExtra("thg",45888))*/

    }
    private fun initRecyclerView() {
        setObservables()
        //attach adapter to  recycler
        binding.recyc.addOnScrollListener(object : PaginationScrollListener(binding.recyc.layoutManager as LinearLayoutManager) {
            var key = "type" + "#" + "process"
            var pageDetails = currentPageNumber.get(key)
            override fun loadMoreItems() {
                isLoading = true
                Handler(Looper.myLooper()!!).postDelayed({
                    setObservables()
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
    fun setObservables(){
        var layoutManager = LinearLayoutManager(this)
        binding.recyc.setLayoutManager(layoutManager)
        val userId:String= SharedPrefEnc.getPref(MyApp.context,"user_id")
        val user_id:Int=userId.toInt()
        var key = "type" + "#" + "process"
        var pageDetails = currentPageNumber.get(key)
        var pageNumber = pageDetails?.currentPage ?: -1;
        ++pageNumber
        if(pageDetails == null) {
            pageDetails = PageDetails(key, pageNumber,0)
            currentPageNumber.put(key,pageDetails);
        }
        pageDetails.currentPage = pageNumber
        pageDetails.currentPage = pageNumber
        val reqMyActivity= ReqMyActivity(
            user_id,pageNumber.toString()
        )
        lifecycleScope.launch {
            viewmodel.getNotification(reqMyActivity)
            viewmodel.mynotification.collect {
                when (it.status) {
                    Status.SUCCESS -> {
                        var totalCount =0;
                        totalCount = it.data!!?.data.size
                        pendingPosts.addAll(it.data!!?.data)
                        val adapter= NotificationAdapter(pendingPosts)
                        binding.recyc.adapter=adapter
                        adapter.notifyDataSetChanged()
                        if(pageNumber>0) {
                            val scrollPosition = pageNumber * 11
                            binding.recyc.scrollToPosition(scrollPosition)
                        }
                        pageDetails.totalPages = totalCount/pageSize
                        pageDetails.totalPages = if(totalCount%pageSize ==0) pageDetails.totalPages else pageDetails.totalPages +1
                        totalPages = pageDetails?.totalPages ?: 0
                        isLastPage = pageNumber + 1 == totalPages
                    }
                    Status.LOADING -> {
                        Log.i("ki999","Loading")
                    }
                    Status.ERROR -> {
                        Log.i("ki999",it.message.toString())
                        Toast.makeText(this@ActivityNotifications,it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    fun setOnClickListeners(){
        binding.back.setOnClickListener { finish() }
    }


}