package com.locatocam.app.views.followers.brand

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.locatocam.app.databinding.FragmentBrandBinding

class BrandFragment : Fragment() {
    lateinit var binding: FragmentBrandBinding
    //lateinit var viewmodel:BrandFollowersViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentBrandBinding.inflate(layoutInflater)
       /* var repository= BrandFollowersRepository(requireActivity().application)
        var factory= BrandFollowerViewModelFactory(repository)

        viewmodel= ViewModelProvider(this,factory).get(BrandFollowersViewModel::class.java)
*/
        setObservers()
        return binding.root
    }

    fun setObservers(){


    }


}