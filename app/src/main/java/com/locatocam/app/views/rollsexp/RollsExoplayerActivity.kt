package com.locatocam.app.views.rollsexp

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.facebook.shimmer.ShimmerFrameLayout
import com.locatocam.app.Activity.PlayPostActivity
import com.locatocam.app.adapter.PlayPostAdapter
import com.locatocam.app.data.responses.playrolls.RollsData
import com.locatocam.app.databinding.ActivityRollsExoplayerBinding
import com.locatocam.app.repositories.RollsRepository
import com.locatocam.app.services.PreCachingService
import com.locatocam.app.utils.Constants
import com.locatocam.app.viewmodels.RollsViewModel
import com.locatocam.app.views.PlayPost.UpdateData
import com.locatocam.app.views.rolls.RollsViewModelFactory

class RollsExoplayerActivity : FragmentActivity() {
    lateinit var binding: ActivityRollsExoplayerBinding
    lateinit var viewmodel: RollsViewModel
    public lateinit var loadersh:ShimmerFrameLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.statusBarColor = Color.TRANSPARENT

        binding= ActivityRollsExoplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadersh=binding.loader
        Log.e("TAG", "onCreate: " )

        var repository= RollsRepository(application)
        var factory= RollsViewModelFactory(repository)
        viewmodel= ViewModelProvider(this,factory).get(RollsViewModel::class.java)


        if(intent.getStringExtra("firstid")!=null){
            viewmodel.lastid= intent.getStringExtra("firstid")!!.toInt()

        }
        else if(intent.getStringExtra("inf_code")!=null){
            viewmodel.influencer_code= intent.getStringExtra("inf_code")!!.toString()
        }

//        binding.viewpager.offscreenPageLimit=2

        viewmodel.roll_items.observe(this,{
            if(viewmodel.offset==0){
                var storiesPagerAdapter = RollsPageAdapter(this, it as MutableList<RollsData>,object :UpdateData{
                    override fun updatefollowstatus(followStatus: String?) {
                        for (item in (binding.viewpager.adapter as RollsPageAdapter).dataList) {
                            item.i_following_this_influencer = followStatus
                        }
                        binding.viewpager.adapter!!.notifyDataSetChanged()
                    }

                })
                binding.viewpager.adapter = storiesPagerAdapter
            }else{
                (binding.viewpager.adapter as RollsPageAdapter).dataList.addAll(it)
                binding.viewpager.adapter!!.notifyDataSetChanged()
            }
            //binding.loader.visibility=View.GONE
            startPreCaching(it)
        })
        viewmodel.getRolls()

            var myPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    Log.i("jiuuuu",position.toString())

                   if(position!=0 && position%4==0 && position>viewmodel.lastposition){
                       Log.i("jiuuuu","call")
                       viewmodel.getRolls()
                       viewmodel.lastposition=position

                   }
                }
            }

            binding.back.setOnClickListener {
                finish()
            }
            binding.viewpager.registerOnPageChangeCallback(myPageChangeCallback)

    }

    private fun startPreCaching(dataList: List<RollsData>) {
        val urlList = arrayOfNulls<String>(dataList.size)
        dataList.mapIndexed { index, storiesDataModel ->
            urlList[index] = storiesDataModel.video_url
        }
        val inputData = Data.Builder().putStringArray(Constants.KEY_STORIES_LIST_DATA, urlList).build()
        val preCachingWork = OneTimeWorkRequestBuilder<PreCachingService>().setInputData(inputData)
            .build()
        WorkManager.getInstance(this)
            .enqueue(preCachingWork)
    }
}