package com.locatocam.app.views.order_online.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.locatocam.app.databinding.FragmentFfersForYouBinding
import com.locatocam.app.repositories.OrderOnlineRepository
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.viewmodels.OrderOnlineViewModel
import com.locatocam.app.views.order_online.ActivityOffersForYou
import com.locatocam.app.views.order_online.ActivityOrderOnline
import com.locatocam.app.views.order_online.AddProductActivity
import com.locatocam.app.views.order_online.fragments.adapters.OffersForYouAdapter

class OffersForYouFragment : Fragment(),SingleClickEvent {

    lateinit var viewModel: OrderOnlineViewModel
    lateinit var binding:FragmentFfersForYouBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var repository= OrderOnlineRepository(requireActivity().application)
        var factory= OrderOnlineModelFactory(repository)
        viewModel= ViewModelProvider(requireActivity(),factory).get(OrderOnlineViewModel::class.java)

        binding= FragmentFfersForYouBinding.inflate(layoutInflater)
        setObservers()
        return binding.root
    }

    fun setObservers(){
        var layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        binding.recyc.setLayoutManager(layoutManager)
        viewModel.offersForYou.observe(viewLifecycleOwner,{data->
            var adapter = OffersForYouAdapter(data,this)
            binding.recyc.setAdapter(adapter)
        })
        viewModel.getOffersForYou(SharedPrefEnc.getPref(context,"selected_lat"), SharedPrefEnc.getPref(context,"selected_lng"))


    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onclickItem(id: String) {

        Intent(requireActivity(), ActivityOffersForYou::class.java)
            .apply {
                putExtra("offer_id",id)
                startActivity(this)
            }

    }
}