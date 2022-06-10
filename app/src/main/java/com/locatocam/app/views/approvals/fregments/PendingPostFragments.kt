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
import com.locatocam.app.network.Status
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.utils.Constant
import com.locatocam.app.viewmodels.PendingApprovalViewModel
import com.locatocam.app.views.approvals.adapters.ApprovedApprovalsAdapter
import com.locatocam.app.views.approvals.adapters.PendingApprovalAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PendingPostFragments : Fragment(){


    private lateinit var postBinding : FragmentPendingPostFragmentsBinding
    lateinit var viewModel : PendingApprovalViewModel
    val type = "post"
    private lateinit var process : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        postBinding = FragmentPendingPostFragmentsBinding.inflate(layoutInflater)

        val bundle = arguments
        if (bundle != null) {
            process = bundle.getString("process")!!
        }

        viewModel = ViewModelProvider(this).get(PendingApprovalViewModel::class.java)

        postBinding.list.layoutManager = LinearLayoutManager(context)

        postBinding.list.itemAnimator = DefaultItemAnimator()

        postBinding.list.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        observeBrandPending(process)


        return postBinding.root
    }

    private fun observeBrandPending(process : String) {

        when(process){

            Constant.PENDING_PROCESS-> {

                viewModel.getBrandPending(
                    ReqPostApproval(
                        offset = "",
                        process = process,
                        type = type,
                        user_id = SharedPrefEnc.getPref(context,"user_id")
                    )
                )

                lifecycleScope.launch {
                    viewModel.pendingApprovals.collect {

                        when (it.status) {

                            Status.SUCCESS -> {
                                if (it.data.isNullOrEmpty()) {
                                    postBinding.noDataText.text = getString(R.string.no_data)
                                    postBinding.noDataText.visibility = View.VISIBLE
                                } else {

                                    val pendingApprovalAdapter =
                                        PendingApprovalAdapter(it.data, requireContext())
                                    postBinding.list.adapter = pendingApprovalAdapter
                                    Log.e("observeBrandPending", it.data[0].posted_by)

                                }
                            }
                            Status.LOADING -> {
                                Log.i("observeBrandPending", "Loading")
                            }
                            Status.ERROR -> {
                                Log.i("observeBrandPending", it.message.toString())
//                        Toast.makeText(this@FollowersActivity,it.message, Toast.LENGTH_LONG).show()
                            }

                        }
                    }

                }
            }
            Constant.APPROVED_PROCESS,Constant.REJECTED_PROCESS->{

                viewModel.getApprovedApprovals(
                    ReqPostApproval(
                        offset = "",
                        process = process,
                        type = type,
                        user_id = SharedPrefEnc.getPref(context,"user_id")
                    )
                )

                lifecycleScope.launch {
                    viewModel.approvedApprovals.collect {

                        when (it.status) {

                            Status.SUCCESS -> {
                                if (it.data.isNullOrEmpty()) {
                                    postBinding.noDataText.text = getString(R.string.no_data)
                                    postBinding.noDataText.visibility = View.VISIBLE
                                } else {

                                    val adapter =
                                        ApprovedApprovalsAdapter(it.data, requireContext())
                                    postBinding.list.adapter = adapter
//                                    Log.e("observeBrandPending", it.data[0].posted_by!!)

                                }
                            }
                            Status.LOADING -> {
                                Log.i("observeBrandPending", "Loading")
                            }
                            Status.ERROR -> {
                                Log.i("observeBrandPending", it.message.toString())
                            }

                        }
                    }

                }

            }


        }

    }


}

