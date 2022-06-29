package com.locatocam.app.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.locatocam.app.R
import com.locatocam.app.adapter.FAQAdapter
import com.locatocam.app.repositories.OnlineOrderHelpRepository
import com.locatocam.app.viewmodels.OnlineOrderHelpViewModel
import com.locatocam.app.views.OnlineOrderHelpFactory
import kotlinx.android.synthetic.main.activity_online_ordering_help.*

class OnlineOrderingHelpActivity : AppCompatActivity() {

    lateinit var viewmodel: OnlineOrderHelpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online_ordering_help)

        var repository = OnlineOrderHelpRepository(application)
        var factory = OnlineOrderHelpFactory(repository)

        viewmodel = ViewModelProvider(this, factory).get(OnlineOrderHelpViewModel::class.java)

        viewmodel.getDataFAQ()
        onClickListener()
        setObserverts()
    }

    private fun setObserverts() {
        viewmodel.FAQDataList.observe(this,{
            var manager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
            rcFAQ.layoutManager=manager
            var adapter= FAQAdapter(this,it)
            rcFAQ.adapter=adapter
        })

    }

    private fun onClickListener() {

        btnBack.setOnClickListener {
            finish()
        }
        imgHome.setOnClickListener {
            finish()
        }
    }
}