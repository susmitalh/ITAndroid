package com.locatocam.app.views.approvals.fregments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.locatocam.app.R
import com.locatocam.app.data.requests.ReqPostApproval
import com.locatocam.app.databinding.FragmentBrandPendingBinding
import com.locatocam.app.network.Status
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.viewmodels.PendingApprovalViewModel
import com.locatocam.app.views.approvals.adapters.PendingApprovalAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BrandPendingFragment : Fragment() {

    private lateinit var brandPendingBinding: FragmentBrandPendingBinding
    lateinit var viewModel : PendingApprovalViewModel
    val type = "brand"
    private lateinit var process : String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        brandPendingBinding= FragmentBrandPendingBinding.inflate(layoutInflater)

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

        brandPendingBinding.list.layoutManager = LinearLayoutManager(context)

        brandPendingBinding.list.itemAnimator = DefaultItemAnimator()

        brandPendingBinding.list.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        observeBrandPending()


        return brandPendingBinding.root
    }

    private fun observeBrandPending() {

        lifecycleScope.launch {
            viewModel.pendingApprovals.collect {

                when(it.status){

                    Status.SUCCESS -> {
                        if (it.data.isNullOrEmpty()){
                            brandPendingBinding.noDataText.text = getString(R.string.no_data)
                            brandPendingBinding.noDataText.visibility = View.VISIBLE
                        }else{


                            val pendingApprovalAdapter = PendingApprovalAdapter(it.data, requireContext())
                            brandPendingBinding.list.adapter = pendingApprovalAdapter
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