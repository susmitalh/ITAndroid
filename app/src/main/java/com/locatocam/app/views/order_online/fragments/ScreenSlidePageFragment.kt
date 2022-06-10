package com.locatocam.app.views.order_online.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.locatocam.app.R
import com.locatocam.app.data.responses.popular_brands.Data
import com.locatocam.app.databinding.RowLayoutSliderItemBinding

class ScreenSlidePageFragment(val data: Data) : Fragment() {

    lateinit var binding: RowLayoutSliderItemBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= RowLayoutSliderItemBinding.inflate(layoutInflater)
        setValue()
        return binding.root
    }

    fun setValue(){
        binding.brandName.setText(data.name)
        binding.distance.setText(data.distance.toString()+" km")
        binding.locationName.setText(data.outlet_fullname)
        binding.cusine.setText(data.cuisine)
        binding.openHours.setText(data.opening_hours)

        Log.i("jn777",data.banner_image_url)
        Glide.with(this)
            .load(data.banner_image_url)
            .into(binding.thumbnile)
    }
}