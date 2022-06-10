package com.locatocam.app.views.order_online.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.locatocam.app.databinding.FragmentSelectedBrandsBinding
import com.locatocam.app.network.Status
import com.locatocam.app.repositories.AddProductRepository
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.viewmodels.AddProductViewModel
import com.locatocam.app.views.order_online.AddProductViewModelFactory
import com.locatocam.app.views.order_online.fragments.adapters.SelectedBrandAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class BrandsinThisOutletFragment : Fragment(),SingleClickEvent {

    lateinit var viewModel: AddProductViewModel
    lateinit var binding:FragmentSelectedBrandsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var repository= AddProductRepository(requireActivity().application)
        var factory= AddProductViewModelFactory(repository)
        viewModel= ViewModelProvider(requireActivity(),factory).get(AddProductViewModel::class.java)
        binding= FragmentSelectedBrandsBinding.inflate(layoutInflater)
        setObservers()
        return binding.root
    }

    fun setObservers() {
        var layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyc.setLayoutManager(layoutManager)

        lifecycleScope.launch {
            viewModel.getSelectedBrands(SharedPrefEnc.getPref(context,"selected_lat"), SharedPrefEnc.getPref(context,"selected_lng"))
            viewModel.selectedbrands.collect {
                when (it.status) {
                    Status.SUCCESS -> {
                        var adapter = it.data?.let { it1 ->
                            SelectedBrandAdapter(it1, this@BrandsinThisOutletFragment)
                        }
                        binding.recyc.setAdapter(adapter)
                        Log.i("ki999","Succes")

                    }
                    Status.LOADING -> {
                        Log.i("ki999","Loading")
                    }
                    Status.ERROR -> {
                        Log.i("ki999","Error")

                        Toast.makeText(
                            requireContext(),
                            it.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onclickItem(id: String) {

    }
}