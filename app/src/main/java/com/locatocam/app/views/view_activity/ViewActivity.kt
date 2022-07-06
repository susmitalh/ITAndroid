package com.locatocam.app.views.view_activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.libraries.places.internal.it
import com.locatocam.app.MyApp
import com.locatocam.app.R
import com.locatocam.app.data.requests.ReqMyActivity
import com.locatocam.app.data.requests.reqUserProfile.ReqViewApproval
import com.locatocam.app.data.responses.my_activity.Data
import com.locatocam.app.data.responses.settings.Approved.PageDetails
import com.locatocam.app.data.responses.settings.pendingPost.Detail
import com.locatocam.app.databinding.ActivityViewBinding
import com.locatocam.app.network.Status
import com.locatocam.app.repositories.FollowersRepository
import com.locatocam.app.repositories.ViewActivityRepository
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.viewmodels.FollowersViewModel
import com.locatocam.app.viewmodels.ViewActivityViewModel
import com.locatocam.app.views.MainActivity
import com.locatocam.app.views.followers.FollowersViewModelFactory
import com.locatocam.app.views.followers.adapters.InfluencerFollowersAdapter
import com.locatocam.app.views.settings.PaginationScrollListener
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ViewActivity : AppCompatActivity() {
    lateinit var binding:ActivityViewBinding
    lateinit var viewmodel: ViewActivityViewModel
    private val pageStart: Int = 0
    private var isLoading: Boolean = false
    private var isLastPage: Boolean = false
    private var totalPages: Int = 200
    private var currentPage: Int = pageStart
    private var currentPageNumber: HashMap<String, PageDetails> = HashMap<String, PageDetails>()
    private var pendingPosts: MutableList<Data> = ArrayList()
    private val pageSize =10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var repository= ViewActivityRepository(application)
        var factory= ViewActivityViewModelFactory(repository)
        viewmodel= ViewModelProvider(this,factory).get(ViewActivityViewModel::class.java)

        setOnClickListeners()
        initRecyclerView()
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
        currentPage = pageNumber
        val reqMyActivity= ReqMyActivity(
            user_id,pageNumber.toString()
        )
        lifecycleScope.launch {
            viewmodel.getActivity(reqMyActivity)
            viewmodel.myactivity.collect {
                when (it.status) {
                    Status.SUCCESS -> {
                        binding.loader.visibility= View.GONE
                        var totalCount =0;
                        totalCount = it.data!!?.data.size
                        pendingPosts.addAll(it.data!!?.data)
                        val adapter= ViewActivityAdapter(pendingPosts)
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
                        binding.loader.visibility= View.VISIBLE
                        Log.i("ki999","Loading")
                    }
                    Status.ERROR -> {
                        binding.loader.visibility= View.VISIBLE
                        Log.i("ki999",it.message.toString())
                        Toast.makeText(this@ViewActivity,it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        }

    fun setOnClickListeners(){
        binding.back.setOnClickListener { finish() }
        binding.home.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}