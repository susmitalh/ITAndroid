package com.locatocam.app.views.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.locatocam.app.R
import com.locatocam.app.databinding.FragmentEditProfileBinding


class EditProfileFragment : Fragment() {

     lateinit var binding : FragmentEditProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditProfileBinding.inflate(layoutInflater)
        return binding.root
    }

}