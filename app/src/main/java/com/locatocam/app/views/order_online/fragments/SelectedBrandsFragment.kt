package com.locatocam.app.views.order_online.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.locatocam.app.databinding.FragmentSelectedBrandsBinding
import com.locatocam.app.repositories.OrderOnlineRepository
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.viewmodels.OrderOnlineViewModel
import com.locatocam.app.views.order_online.AddProductActivity
import com.locatocam.app.views.order_online.fragments.adapters.SelectedBrandAdapter

class SelectedBrandsFragment : Fragment(),SingleClickEvent {

    lateinit var viewModel: OrderOnlineViewModel
    lateinit var binding:FragmentSelectedBrandsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val repository= OrderOnlineRepository(requireActivity().application)
        val factory= OrderOnlineModelFactory(repository)
        viewModel= ViewModelProvider(requireActivity(),factory).get(OrderOnlineViewModel::class.java)
        binding= FragmentSelectedBrandsBinding.inflate(layoutInflater)
        setObservers()
        return binding.root
    }

    fun setObservers(){
        val layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        binding.recyc.setLayoutManager(layoutManager)
        viewModel.selectedbrands.observe(viewLifecycleOwner,{data->
            Log.e("selectedbrands",data.toString())
            if (data.isNotEmpty()){
                val adapter = SelectedBrandAdapter(data,this)
                binding.recyc.setAdapter(adapter)
            }else{
                binding.sb.visibility  = View.GONE
            }

        })
        viewModel.getSelectedBrands(SharedPrefEnc.getPref(context,"selected_lat"), SharedPrefEnc.getPref(context,"selected_lng"))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onclickItem(id: String) {
        Intent(requireActivity(),AddProductActivity::class.java)
            .apply {
                putExtra("store_id",id)
                startActivity(this)
            }
    }
}