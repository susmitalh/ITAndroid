package com.locatocam.app.views.approvals.fregments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.locatocam.app.R
import com.locatocam.app.data.requests.ReqPostApproval
import com.locatocam.app.databinding.FragmentPendingPostFragmentsBinding
import com.locatocam.app.databinding.FragmentPendingRollsBinding
import com.locatocam.app.databinding.FragmentPendingSubscriberBinding
import com.locatocam.app.network.Status
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.viewmodels.PendingApprovalViewModel
import com.locatocam.app.views.approvals.adapters.PendingApprovalAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PendingSubscriberFragment : Fragment() {

    private lateinit var binding : FragmentPendingSubscriberBinding

    lateinit var viewModel : PendingApprovalViewModel

    private lateinit var process : String

    val type = "subscriber"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPendingSubscriberBinding.inflate(layoutInflater)

        val bundle = arguments
        if (bundle != null) {
            process = bundle.getString("process")!!
        }

        viewModel = ViewModelProvider(this).get(PendingApprovalViewModel::class.java)

        viewModel.getBrandPending(
            ReqPostApproval(
                offset = "",
                process = process,
                type = type,
                user_id = SharedPrefEnc.getPref(context,"user_id")
            )
        )

        binding.list.layoutManager = LinearLayoutManager(context)

        binding.list.itemAnimator = DefaultItemAnimator()

        binding.list.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        observeBrandPending()

        return binding.root
    }

    private fun observeBrandPending() {

        lifecycleScope.launch {
            viewModel.pendingApprovals.collect {

                when(it.status){

                    Status.SUCCESS -> {
                        if (it.data.isNullOrEmpty()){
                            binding.noDataText.text = getString(R.string.no_data)
                            binding.noDataText.visibility = View.VISIBLE
                        }else{

                            val pendingApprovalAdapter = PendingApprovalAdapter(it.data,requireContext())
                            binding.list.adapter = pendingApprovalAdapter
                            Log.e("observeBrandPending", it.data[0].posted_by)

                        }
                    }
                    Status.LOADING -> {
                        Log.i("observeBrandPending","Loading")
                    }
                    Status.ERROR -> {
                        Log.i("observeBrandPending",it.message.toString())
//                        Toast.makeText(this@FollowersActivity,it.message, Toast.LENGTH_LONG).show()
                    }

                }
            }
        }
    }

}