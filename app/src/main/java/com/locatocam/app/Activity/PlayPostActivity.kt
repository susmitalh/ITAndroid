package com.locatocam.app.Activity

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.facebook.shimmer.ShimmerFrameLayout
import com.locatocam.app.ModalClass.PlayPostData
import com.locatocam.app.R
import com.locatocam.app.adapter.PlayPostAdapter
import com.locatocam.app.databinding.ActivityPlayPostBinding
import com.locatocam.app.repositories.PlayPostRepository
import com.locatocam.app.services.PreCachingService
import com.locatocam.app.utils.Constants
import com.locatocam.app.viewmodels.PlayPostViewModal
import com.locatocam.app.views.PlayPost.PlayPostFragment
import com.locatocam.app.views.PlayPost.PlayPostViewModelFactory
import com.locatocam.app.views.PlayPost.UpdateData
import kotlinx.android.synthetic.main.activity_approval_base.*
import kotlinx.android.synthetic.main.activity_play_post.*
import pl.droidsonroids.gif.GifImageView

class PlayPostActivity : AppCompatActivity() {

    companion object {
        lateinit var viewModel: PlayPostViewModal
        lateinit var binding: ActivityPlayPostBinding
        lateinit var activity: PlayPostActivity

    }

    public lateinit var loadersh: ShimmerFrameLayout
    var dialog: Dialog? = null
    var position: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.statusBarColor = Color.TRANSPARENT

        binding = ActivityPlayPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = PlayPostActivity()
        PlayPostFragment.volumeMute=false
//        loadersh=binding.loader
        showLoader()


        var repository = PlayPostRepository(application)
        var factory = PlayPostViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory).get(PlayPostViewModal::class.java)

        viewModel.post_id = intent.getStringExtra("post_id").toString()
        viewModel.influencer_code = intent.getStringExtra("influencer_code").toString()

            setObserverts()

    }


    fun setObserverts() {

        viewModel.playPostList.observe(this, {

            if (viewModel.offset == 0) {
                var storiesPagerAdapter =
                    PlayPostAdapter(this, it as MutableList<PlayPostData>, object : UpdateData {
                        override fun updatefollowstatus(followStatus: String?) {
                            Log.e("TAG", "updatefollowstatus: " + followStatus)
                            for (item in (binding.viewpager.adapter as PlayPostAdapter).dataList) {
                                item.i_following_this_influencer = followStatus
                            }
                            binding.viewpager.adapter!!.notifyDataSetChanged()

                        }

                    })
                binding.viewpager.adapter = storiesPagerAdapter

            } else {
                (binding.viewpager.adapter as PlayPostAdapter).dataList.addAll(it)
                binding.viewpager.adapter!!.notifyDataSetChanged()
            }
            //binding.loader.visibility=View.GONE
            startPreCaching(it)
        })
        viewModel.PlayPost(this)

        /* playPost=object :PlayPost{
             override fun like(likeCount: String?) {

                 Log.e("TAG", "like1: "+likeCount )
                 var data:PlayPostData
                 data=(binding.viewpager.adapter as PlayPostAdapter).dataList.get(position)
                 data.like_count=likeCount
                 (binding.viewpager.adapter as PlayPostAdapter).dataList.set(position,data)

                 (binding.viewpager.adapter as PlayPostAdapter).notifyDataSetChanged()


             }

             override fun comment(commentCount: String?) {
                 Log.e("TAG", "like1: "+commentCount )

             }

             override fun position(position: Int) {
                 this@PlayPostActivity.position =position
             }

         }
*/
        var myPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                Log.i("jiuuuu", position.toString())
                if (position != 0 && position % 4 == 0 && position > viewModel.lastposition) {
                    Log.i("jiuuuu", "call")
                    viewModel.PlayPost(this@PlayPostActivity)
                    viewModel.lastposition = position

                }
            }
        }

        binding.back.setOnClickListener {
            finish()
        }
        binding.viewpager.registerOnPageChangeCallback(myPageChangeCallback)
    }

    private fun startPreCaching(dataList: List<PlayPostData>) {
        val urlList = arrayOfNulls<String>(dataList.size)
        dataList.mapIndexed { index, storiesDataModel ->
            urlList[index] = storiesDataModel.video_url
        }
        val inputData =
            Data.Builder().putStringArray(Constants.KEY_STORIES_LIST_DATA, urlList).build()
        val preCachingWork = OneTimeWorkRequestBuilder<PreCachingService>().setInputData(inputData)
            .build()
        WorkManager.getInstance(this)
            .enqueue(preCachingWork)
    }

    fun showLoader() {
        dialog = Dialog(this, R.style.AppTheme_Dialog)
        val view = View.inflate(this, R.layout.progressdialog_item, null)
        dialog?.setContentView(view)
        dialog?.setCancelable(true)
        val progressbar: GifImageView = dialog?.findViewById(R.id.img_loader)!!
        dialog?.show()
    }

    fun hideLoader() {
        if (dialog != null) {
            dialog?.dismiss()
        }
    }
}