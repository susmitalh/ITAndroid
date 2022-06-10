package com.locatocam.app.views.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.locatocam.app.custom.TestYu
import com.locatocam.app.databinding.ActivityNotificationsBinding
import com.locatocam.app.databinding.ActivityViewBinding
import com.locatocam.app.network.Status
import com.locatocam.app.repositories.ViewActivityRepository
import com.locatocam.app.viewmodels.ViewActivityViewModel
import com.locatocam.app.views.view_activity.ViewActivityAdapter
import com.locatocam.app.views.view_activity.ViewActivityViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ActivityNotifications : AppCompatActivity() {
    lateinit var binding: ActivityNotificationsBinding
    lateinit var viewmodel: ViewActivityViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var repository= ViewActivityRepository(application)
        var factory= ViewActivityViewModelFactory(repository)
        viewmodel= ViewModelProvider(this,factory).get(ViewActivityViewModel::class.java)

        setOnClickListeners()
        setObservables()


      /*  var br=object :BroadcastReceiver(){
            override fun onReceive(p0: Context?, p1: Intent?) {
               Log.i("uijjj", p1?.getIntExtra("thg",4).toString())
            }

        }
        registerReceiver(br, IntentFilter("hnmm"))

        sendBroadcast(Intent("hnmm").putExtra("thg",45888))*/

    }

    fun setObservables(){
        var layoutManager = LinearLayoutManager(this)
        binding.recyc.setLayoutManager(layoutManager)
        lifecycleScope.launch {
            viewmodel.getActivity()
            viewmodel.myactivity.collect {
                when (it.status) {
                    Status.SUCCESS -> {
                        val adapter= ViewActivityAdapter(it.data!!?.data)
                        binding.recyc.adapter=adapter
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