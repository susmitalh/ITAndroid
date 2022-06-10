package com.locatocam.app.views.order_online.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.locatocam.app.databinding.FragmentTopPicsBinding
import com.locatocam.app.repositories.OrderOnlineRepository
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.viewmodels.OrderOnlineViewModel
import com.locatocam.app.views.order_online.fragments.adapters.PopularBrandsAdapter

class PopularBrandsFragment : Fragment(),SingleClickEvent {

    lateinit var viewModel: OrderOnlineViewModel
    lateinit var binding:FragmentTopPicsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentTopPicsBinding.inflate(layoutInflater)
        var repository= OrderOnlineRepository(requireActivity().application)
        var factory= OrderOnlineModelFactory(repository)
        viewModel= ViewModelProvider(requireActivity(),factory).get(OrderOnlineViewModel::class.java)

        setObservers()
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
    fun setObservers(){
        var layoutManager = LinearLayoutManager(requireContext())
        binding.recyc.setLayoutManager(layoutManager)
        viewModel.popular_brands.observe(viewLifecycleOwner,{data->
            var adapter = PopularBrandsAdapter(data,this)
            binding.recyc.setAdapter(adapter)
        })
        viewModel.getPopularBrands(SharedPrefEnc.getPref(context,"selected_lat"), SharedPrefEnc.getPref(context,"selected_lng"))


    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onclickItem(id: String) {

    }
}