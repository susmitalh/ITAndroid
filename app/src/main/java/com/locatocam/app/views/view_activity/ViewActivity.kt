package com.locatocam.app.views.view_activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.libraries.places.internal.it
import com.locatocam.app.R
import com.locatocam.app.databinding.ActivityViewBinding
import com.locatocam.app.network.Status
import com.locatocam.app.repositories.FollowersRepository
import com.locatocam.app.repositories.ViewActivityRepository
import com.locatocam.app.viewmodels.FollowersViewModel
import com.locatocam.app.viewmodels.ViewActivityViewModel
import com.locatocam.app.views.followers.FollowersViewModelFactory
import com.locatocam.app.views.followers.adapters.InfluencerFollowersAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ViewActivity : AppCompatActivity() {
    lateinit var binding:ActivityViewBinding
    lateinit var viewmodel: ViewActivityViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var repository= ViewActivityRepository(application)
        var factory= ViewActivityViewModelFactory(repository)
        viewmodel= ViewModelProvider(this,factory).get(ViewActivityViewModel::class.java)

        setOnClickListeners()
        setObservables()
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
                        Toast.makeText(this@ViewActivity,it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    fun setOnClickListeners(){
        binding.back.setOnClickListener { finish() }
    }
}