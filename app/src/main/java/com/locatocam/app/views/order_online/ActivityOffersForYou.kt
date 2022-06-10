package com.locatocam.app.views.order_online

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.locatocam.app.databinding.ActivityOffersForYouBinding
import com.locatocam.app.databinding.ActivityOrderOnlineBinding
import com.locatocam.app.network.Status
import com.locatocam.app.repositories.OrderOnlineRepository
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.viewmodels.OrderOnlineViewModel
import com.locatocam.app.views.order_online.fragments.OrderOnlineModelFactory
import com.locatocam.app.views.order_online.fragments.SingleClickEvent
import com.locatocam.app.views.order_online.fragments.adapters.OffersAdapter
import com.locatocam.app.views.search.ClickEvents
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ActivityOffersForYou : OrderOnlineBaseActivity(),SingleClickEvent{

    lateinit var activityOffersForYouBinding: ActivityOffersForYouBinding
    lateinit var viewModel: OrderOnlineViewModel
    lateinit var clickEvent: SingleClickEvent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityOffersForYouBinding= ActivityOffersForYouBinding.inflate(layoutInflater,baseBinding.container,true)
        clickEvent = this

        val repository= OrderOnlineRepository(this.application)
        val factory= OrderOnlineModelFactory(repository)
        viewModel= ViewModelProvider(this,factory).get(OrderOnlineViewModel::class.java)



        val offerID = intent.extras?.getString("offer_id")
        Log.e("offerID",offerID.toString())
        viewModel.getOffers(offerID,SharedPrefEnc.getPref(this,"selected_lat"), SharedPrefEnc.getPref(this,"selected_lng"))

        observeData()

    }

    fun observeData(){

        lifecycleScope.launch {
            viewModel.offersForYouList.collect {

                when (it.status) {

                    Status.SUCCESS -> {

                        Log.e("success",it.data.toString())
                        if (it.data?.data?.size!=0) {

                            val adapter = OffersAdapter(it.data!!.data, clickEvent)
                            activityOffersForYouBinding.offerList.adapter = adapter
                            Log.e("adapter",it.data.data[0].name)
                        }


                    }
                    Status.LOADING -> {
                        Log.e("loading","loading")
                    }
                    Status.ERROR -> {
                        Log.e("error","eror")


                    }

                }

            }
        }

    }

    override fun onclickItem(id: String) {
        val intent = Intent(this, AddProductActivity::class.java)
        intent.putExtra("store_id", id)
        startActivity(intent)
    }
}