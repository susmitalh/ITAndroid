package com.locatocam.app.Activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.locatocam.app.ModalClass.MyPostsPost
import com.locatocam.app.R
import com.locatocam.app.adapter.MyPostsAdapter
import com.locatocam.app.repositories.ViewMyPostRepository
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.services.PreCachingService
import com.locatocam.app.utils.Constants
import com.locatocam.app.viewmodels.ViewMyPostViewModal
import com.locatocam.app.views.ViewMyPost.ViewMyPostFactory
import com.locatocam.app.views.home.test.Follow
import com.locatocam.app.views.home.test.SimpleEvents
import kotlinx.android.synthetic.main.activity_view_my_post.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.droidsonroids.gif.GifImageView

class ViewMyPostActivity : AppCompatActivity() {

    lateinit var viewModel: ViewMyPostViewModal
    var myPost: Boolean = false
    var dialog: Dialog? = null
    lateinit var followListner:Follow
    var followCountStatus:Int = 0

    lateinit var followId:String
    lateinit var simpleEvents: SimpleEvents

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_my_post)
        showLoader()

        var repository = ViewMyPostRepository(application)
        var factory = ViewMyPostFactory(repository)

        viewModel = ViewModelProvider(this, factory).get(ViewMyPostViewModal::class.java)

        viewModel.my_id = intent.getStringExtra("my_id").toString()
        var layoutManager =
            GridLayoutManager(this, 3)








            viewModel.MyPost(this)
        setObserverts(layoutManager)
        OnClickListener(layoutManager)

    }

    private fun OnClickListener(layoutManager: GridLayoutManager) {

        var shortVideo=intent.getStringExtra("shortVideo").toString()

        if (shortVideo!="null") {

            viewModel.my_id=SharedPrefEnc.getPref(application,"user_id")

            Log.e("TAG", "OnClickListdsener: " + shortVideo)

           /* title_text.setText("View Short Videos")
            posts_text.setBackground(ContextCompat.getDrawable(this, R.drawable.button_rnd_red))
            posts_text.setTextColor(ContextCompat.getColor(this, R.color.red))

            short_video_text.setBackground(ContextCompat.getDrawable(this, R.drawable.button_rnd_red_filled))
            short_video_text.setTextColor(Color.WHITE)

            myPost=true
            viewModel.offset=0
            viewModel.post_id="0"
            viewModel.myShort(this)
            shortVideo(layoutManager)*/

            title_text.setText("View Short Videos")
            posts_text.setBackground(ContextCompat.getDrawable(this, R.drawable.button_rnd_red))
            posts_text.setTextColor(ContextCompat.getColor(this, R.color.red))

            short_video_text.setBackground(ContextCompat.getDrawable(this, R.drawable.button_rnd_red_filled))
            short_video_text.setTextColor(Color.WHITE)


            myPost=true
            viewModel.offset=0
            viewModel.post_id="0"
            viewModel.myShort(this)
            shortVideo(layoutManager)


        }

        simpleEvents=object :SimpleEvents{
            override fun like(process: String, post_id: String) {

            }

            override fun trash(post_id: String, position: Int) {
                viewModel.trash(post_id.toInt())
                (myPostRecycler.adapter as MyPostsAdapter).dataListPosts?.removeAt(position)
                (myPostRecycler.adapter as MyPostsAdapter).notifyDataSetChanged()
                viewModel.trash.observe(this@ViewMyPostActivity, {
                    Toast.makeText(this@ViewMyPostActivity, "" + it.message, Toast.LENGTH_SHORT).show()
                })
            }

            override fun isHeaderAdded(): Boolean {
                TODO("Not yet implemented")
            }

            override fun addHeader() {
                TODO("Not yet implemented")
            }

        }

        if (viewModel.my_id.equals(SharedPrefEnc.getPref(application,"user_id"))){
            userDetailsLayout.visibility=View.GONE
        }
        followListner=object :Follow{
            override fun follow(position: Int, followStatus: String) {
                follow.setText(followStatus)
              if (followStatus.equals("Follow")){
                 followCountStatus= (followCountStatus)-1
                  followCount.setText(followCountStatus.toString()+" Followers")
                  follow.setTextColor(ContextCompat.getColor(this@ViewMyPostActivity, R.color.red))
              }else{
                  followCountStatus= (followCountStatus)+1
                  followCount.setText(followCountStatus.toString()+" Followers")
                  follow.setTextColor(ContextCompat.getColor(this@ViewMyPostActivity, R.color.green))

              }
            }

            override fun brandFollow(position: Int, followStatus: String) {
                TODO("Not yet implemented")
            }

        }

        follow.setOnClickListener {
            viewModel.follow(viewModel.followProcess,followId,this,followListner)
        }
        back_img.setOnClickListener {
            finish()
        }
        title_text.setText("View Post Videos")

        posts_text.setOnClickListener {
            title_text.setText("View Post Videos")
            posts_text.setBackground(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.button_rnd_red_filled
                )
            )
            posts_text.setTextColor(Color.WHITE)

            short_video_text.setBackground(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.button_rnd_red
                )
            )
            short_video_text.setTextColor(ContextCompat.getColor(this, R.color.red))
            viewModel.offset = 0
            viewModel.post_id="0"
            viewModel.MyPost(this)
            myPost=false


        }
        short_video_text.setOnClickListener {
            title_text.setText("View Short Videos")
            showLoader()
            posts_text.setBackground(ContextCompat.getDrawable(this, R.drawable.button_rnd_red))
            posts_text.setTextColor(ContextCompat.getColor(this, R.color.red))

            short_video_text.setBackground(ContextCompat.getDrawable(this, R.drawable.button_rnd_red_filled))
            short_video_text.setTextColor(Color.WHITE)

            (myPostRecycler.adapter as MyPostsAdapter).dataListPosts?.clear()
            (myPostRecycler.adapter as MyPostsAdapter).notifyDataSetChanged()

            myPost=true
            viewModel.offset=0
            viewModel.post_id="0"
            viewModel.myShort(this)
            shortVideo(layoutManager)
        }
    }

    private fun shortVideo(layoutManager: GridLayoutManager) {


        viewModel.myShortList.observe(this) {
            viewModel.loading = false

            Glide.with(this).load(it.photo).into(profileImage)
            profile_name.setText(it.name)
            postCount.setText(it.postCount + " Short Video")
            followCount.setText(it.followCount + " Followers")
            follow.setText(it.followStatus)
            followId = it.userId.toString()
            if (it.followStatus.equals("Following")) {
                follow.setTextColor(ContextCompat.getColor(this, R.color.green))
                viewModel.followProcess = "unfollow"
            } else {
                viewModel.followProcess = "follow"
            }

            if (viewModel.offset == 0) {

                myPostRecycler.setLayoutManager(layoutManager)
                followCountStatus=it.followCount!!.toInt()
                if (!it.rolls.isNullOrEmpty()) {
                    var adapter =
                        MyPostsAdapter(this, it.rolls as ArrayList<MyPostsPost>?, simpleEvents,it)
                    myPostRecycler.setAdapter(adapter)
                }
            } else {

                if (!it.rolls.isNullOrEmpty()) {
                    (myPostRecycler.adapter as MyPostsAdapter).dataListPosts?.addAll(it.rolls!!)
                    (myPostRecycler.adapter as MyPostsAdapter)!!.notifyDataSetChanged()
                }
            }
            //binding.loader.visibility=View.GONE
//            startPreCaching(it.posts!!)
        }
    }

    private fun setObserverts(layoutManager: GridLayoutManager) {
        myPostRecycler.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) { //check for scroll down
                    var visibleItemCount = layoutManager.getChildCount()
                    var totalItemCount = layoutManager.getItemCount()
                    var pastVisiblesItems = layoutManager.findFirstVisibleItemPosition()
                    if (!viewModel.loading) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount - 6) {
                            Log.v("gt66666", "Last Item Wow !")
                            if (myPost == false)
                                viewModel.MyPost(this@ViewMyPostActivity)
                            else
                               viewModel.myShort(this@ViewMyPostActivity)


                        }
                    }
                }
            }
        })



        viewModel.myPostList.observe(this, {

            viewModel.loading = false

            Glide.with(this).load(it.photo).into(profileImage)
            profile_name.setText(it.name)
            postCount.setText(it.postCount + " Post")
            followCount.setText(it.followCount + " Followers")
            follow.setText(it.followStatus)
            followId=it.userId.toString()
            followCountStatus=it.followCount!!.toInt()
            if (it.followStatus.equals("Following")) {
                follow.setTextColor(ContextCompat.getColor(this, R.color.green))
              viewModel.followProcess="unfollow"
        }else{
            viewModel.followProcess="follow"
        }

            if (viewModel.offset == 0) {

                myPostRecycler.setLayoutManager(layoutManager)

                if (!it.posts.isNullOrEmpty()) {

                    var adapter = MyPostsAdapter(
                        this,
                        it.posts as ArrayList<MyPostsPost>?,
                        simpleEvents,
                        it
                    )
                    myPostRecycler.setAdapter(adapter)
                }
            } else {

                if (!it.posts.isNullOrEmpty()) {
                    (myPostRecycler.adapter as MyPostsAdapter).dataListPosts?.addAll(it.posts!!)
                    (myPostRecycler.adapter as MyPostsAdapter)!!.notifyDataSetChanged()
                }
            }
            //binding.loader.visibility=View.GONE
            startPreCaching(it.posts!!)

        })

    }


    private fun startPreCaching(dataList: List<MyPostsPost>) {
        val urlList = arrayOfNulls<String>(dataList.size)
        dataList.mapIndexed { index, storiesDataModel ->
            urlList[index] = storiesDataModel.screenshot
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