package com.locatocam.app.views.rolls

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.VideoView
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.locatocam.app.R
import com.locatocam.app.data.responses.rolls_and_short_videos.Data
import com.locatocam.app.databinding.ActivityRollsBinding
import com.locatocam.app.viewmodels.RollsViewModel

class RollsActivity : AppCompatActivity() {
    lateinit var binding:ActivityRollsBinding
    lateinit var viewmodel:RollsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRollsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewmodel=ViewModelProvider(this).get(RollsViewModel::class.java)

        binding.viewpager.offscreenPageLimit=3
        viewmodel.roll_items.observe(this,{
            binding.viewpager.adapter=VideosAdapter(it)
        })
        viewmodel.getRolls()

        var myPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                Log.i("current_pos",position.toString())
                //binding.viewpager.adapterfindViewById<>()
            }
        }

        binding.viewpager.registerOnPageChangeCallback(myPageChangeCallback)

    }
}