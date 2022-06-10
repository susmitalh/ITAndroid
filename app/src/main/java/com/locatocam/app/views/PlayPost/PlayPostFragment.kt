package com.locatocam.app.views.PlayPost

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.util.Log
import com.google.android.exoplayer2.util.Util
import com.locatocam.app.Activity.PlayPostActivity
import com.locatocam.app.Activity.ViewMyPostActivity
import com.locatocam.app.ModalClass.AddShare
import com.locatocam.app.ModalClass.Follow
import com.locatocam.app.ModalClass.PlayPostData
import com.locatocam.app.MyApp
import com.locatocam.app.R
import com.locatocam.app.di.module.NetworkModule.Companion.getClient
import com.locatocam.app.network.WebApi
import com.locatocam.app.repositories.PlayPostRepository
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.utils.Constants
import com.locatocam.app.viewmodels.PlayPostViewModal
import com.locatocam.app.views.comments.CommentsActivity
import com.locatocam.app.views.home.test.SimpleExoPlayerViewHolder
import com.skyhope.showmoretextview.ShowMoreTextView
import de.hdodenhof.circleimageview.CircleImageView
import im.ene.toro.exoplayer.ExoPlayerViewHelper
import kotlinx.android.synthetic.main.activity_play_post.*
import net.minidev.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlayPostFragment : Fragment(R.layout.layout_play_post_view) {


    lateinit var viewModel: PlayPostViewModal

    var apiInterface: WebApi? = null
    lateinit var playPost: PlayPost

    private var storyUrl: String? = null
    private var storiesDataModel: PlayPostData? = null

    private var simplePlayer: SimpleExoPlayer? = null
    private var cacheDataSourceFactory: CacheDataSourceFactory? = null
    private val simpleCache = MyApp.simpleCache
    private var toPlayVideoPosition: Int = -1
    lateinit var comment_count: TextView
    lateinit var player_view_story: PlayerView
    lateinit var userimage: CircleImageView
    lateinit var name: TextView
    lateinit var follow: ImageView
    lateinit var user_unfollows: TextView
    lateinit var like: LinearLayout
    lateinit var comment: LinearLayout
    lateinit var share: LinearLayout
    lateinit var description: ShowMoreTextView
    lateinit var like_count: TextView



    lateinit var like_icon: ImageView

    lateinit var followStatus: String
    lateinit var updateDatas: UpdateData

    companion object {
        fun newInstance(storiesDataModel: PlayPostData, updateData: UpdateData) = PlayPostFragment()
            .apply {
                arguments = Bundle().apply {
                    putParcelable(Constants.KEY_STORY_DATA, storiesDataModel)
                    updateDatas = updateData
                }

            }

        lateinit var instance: PlayPostFragment
        var volumeMute:Boolean=false

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var repository = PlayPostRepository(requireActivity().application)

        android.util.Log.e("TAG", "onViewCreated: ")

        var factory = PlayPostViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(PlayPostViewModal::class.java)
        apiInterface = getClient()!!.create(WebApi::class.java)

        player_view_story = view.findViewById(R.id.player_view)
        instance = PlayPostFragment()
        userimage = view.findViewById(R.id.user_image)
        name = view.findViewById(R.id.user_name)
        user_unfollows = view.findViewById(R.id.user_unfollow)
        follow = view.findViewById(R.id.user_follow)
        like = view.findViewById(R.id.user_like)
        comment = view.findViewById(R.id.user_comment)
        share = view.findViewById(R.id.user_share)
        description = view.findViewById(R.id.user_description)
        like_count = view.findViewById(R.id.txt_like_count)
        comment_count = view.findViewById(R.id.txt_comment_count)
        like_icon = view.findViewById(R.id.like_icon_img)
        description.addShowLessText("Read Less")
        description.addShowMoreText("Read More")
        description.setShowingLine(2)
        description.setShowMoreColor(Color.WHITE)
        description.setShowLessTextColor(Color.WHITE)


        storiesDataModel = arguments?.getParcelable(Constants.KEY_STORY_DATA)
        setData()
    }

    private fun setData() {

        Glide.with(userimage.context)
            .load(storiesDataModel?.influencer_logo)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .placeholder(R.drawable.ic_placeholder)
            .into(userimage)

        name.setText(storiesDataModel?.influencer_name)
        description.setText(storiesDataModel?.headline + ", " + storiesDataModel?.content)
        like_count.setText(storiesDataModel?.like_count)
        comment_count.setText(storiesDataModel?.comment_count)

        val simplePlayer = getPlayer()
        player_view_story.player = simplePlayer


        (activity as PlayPostActivity).volume_playPost.setOnClickListener {


            if (!volumeMute) {

                (activity as PlayPostActivity).volume_playPost.setImageResource(R.drawable.ic_volume_up_grey_24dp)
                if (simplePlayer != null)
                    simplePlayer!!.volume =
                    1.0f
                volumeMute = true
            } else {

                (activity as PlayPostActivity).volume_playPost.setImageResource(R.drawable.ic_volume_off_grey_24dp)
                if (simplePlayer != null)
                    simplePlayer!!.volume =
                    0.0f
                volumeMute = false
            }
        }





        storyUrl = storiesDataModel?.video_url
        storyUrl?.let { prepareMedia(it) }

        if (storiesDataModel?.i_liked_this_post.equals("0")) {
            like_icon.setImageResource(R.drawable.ic_baseline_unfavorite_border)
        } else {
            like_icon.setImageResource(R.drawable.ic_baseline_favorite_24)
        }

        if (storiesDataModel?.i_following_this_influencer.equals("1")) {
            user_unfollows.setText("Follow")
            follow.visibility = View.VISIBLE
            user_unfollows.visibility = View.GONE
        } else {
            follow.visibility = View.GONE
            user_unfollows.visibility = View.VISIBLE
        }


        player_view_story.getVideoSurfaceView().setOnClickListener {
            if (simplePlayer?.isPlaying!!) {
                simplePlayer?.playWhenReady = false
            }else{
                simplePlayer?.playWhenReady = true
            }

        }

        userimage.setOnClickListener {
            var intent=Intent(context,ViewMyPostActivity::class.java)
            intent.putExtra("my_id",storiesDataModel?.user_id)
            context?.startActivity(intent)

        }

        follow.setOnClickListener {
            storiesDataModel?.i_following_this_influencer = "1"
            follow.visibility = View.GONE
            user_unfollows.visibility = View.VISIBLE
            followStatus = "unfollow"
            updateDatas.updatefollowstatus("0")
            followApi(storiesDataModel)
        }

        user_unfollows.setOnClickListener {
            storiesDataModel?.i_following_this_influencer = "0"
            follow.visibility = View.VISIBLE
            user_unfollows.visibility = View.GONE
            followStatus = "follow"
            updateDatas.updatefollowstatus("1")
            followApi(storiesDataModel)


        }
        playPost = object : PlayPost {
            override fun like(likeCount: String?) {
                like_count.setText(likeCount)
            }

            override fun comment(commentCount: String?) {
//               comment_count.setText(commentCount)
//                storiesDataModel?.comment_count=commentCount
                android.util.Log.e("TAG", "coddmmentc: " + commentCount)
            }
        }
        android.util.Log.e("TAG", "coddmmentc: " + storiesDataModel?.comment_count)

        like.setOnClickListener {
            android.util.Log.e("TAG", "setData: ")

            if (storiesDataModel?.i_liked_this_post.equals("0")) {
                storiesDataModel?.i_liked_this_post = "1"
                like_icon.setImageResource(R.drawable.ic_baseline_favorite_24)
                viewModel.like("like", storiesDataModel?.id!!.toInt(), playPost)

            } else {
                storiesDataModel?.i_liked_this_post = "0"
                like_icon.setImageResource(R.drawable.ic_baseline_unfavorite_border)
                viewModel.like("unlike", storiesDataModel?.id!!.toInt(), playPost)
            }
        }


        share.setOnClickListener {
            sharePost(storiesDataModel)
            /* val message: String = "https://loca-toca.com/Main/index?si="+ storiesDataModel!!.influencer_code+"&id="+ storiesDataModel!!.id
             val share = Intent(Intent.ACTION_SEND)
             share.type = "text/plain"
             share.putExtra(Intent.EXTRA_TEXT, message)
             startActivity(Intent.createChooser(share, "Share"))
 */
            /*  val url = "https://api.whatsapp.com/send?phone=+&text=urlencodedtext"
              val i = Intent(Intent.ACTION_VIEW)
              i.data = Uri.parse(url)

              requireActivity().startActivity(i)*/
        }


        comment.setOnClickListener {
            var intent = Intent(requireActivity(), CommentsActivity::class.java)
            intent.putExtra("postid", storiesDataModel?.id.toString())
            intent.putExtra("userid", storiesDataModel?.user_id.toString())
            intent.putExtra("commentType", "post")
            startActivityForResult(intent, 500)
        }
    }


    private fun followApi(dataList: PlayPostData?) {
        android.util.Log.e(
            "TAG",
            "followApi: " + SharedPrefEnc.getPref(
                context,
                "user_id"
            ) + ", " + followStatus + "," + dataList?.user_id
        )

        val jsonObject = JSONObject()
        jsonObject["user_id"] = SharedPrefEnc.getPref(context, "user_id")
        jsonObject["follow_type"] = "influencer"
        jsonObject["follow_process"] = followStatus
        jsonObject["follower_id"] = dataList?.user_id

        apiInterface!!.getFollow(jsonObject)!!.enqueue(object : Callback<Follow> {
            override fun onResponse(call: Call<Follow>, response: Response<Follow>) {
                if (response.body()!!.status == "true") {
                    Toast.makeText(context, ""+response.body()?.message, Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context, ""+response.body()?.message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Follow>, t: Throwable) {}
        })


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 500) {
            comment_count.setText(data?.getStringExtra("comment_count"))
//            Toast.makeText(requireActivity(), "count : " + data?.getStringExtra("comment_count"), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        pauseVideo()
        super.onPause()
    }

    override fun onResume() {
        restartVideo()
        super.onResume()
    }

    override fun onDestroy() {
        releasePlayer()
        super.onDestroy()
    }

    private val playerCallback: Player.EventListener? = object : Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            Log.i("rffvvvvvvvbb", playbackState.toString())
            if (playbackState >= 2) {
//                Handler().postDelayed(Runnable {
                (activity as PlayPostActivity).hideLoader()
//                }, 1500)

                /* (activity as PlayPostActivity).loadersh.stopShimmer()
                 (activity as PlayPostActivity).loadersh.visibility=View.GONE*/

                if (simplePlayer != null) {
                    try {
                        if (volumeMute) {
                            simplePlayer!!.volume = 1.0f
                            (activity as PlayPostActivity).volume_playPost.setImageResource(R.drawable.ic_volume_up_grey_24dp)
                        } else {
                            simplePlayer!!.volume = 0.0f
                            (activity as PlayPostActivity).volume_playPost.setImageResource(R.drawable.ic_volume_off_grey_24dp)
                        }
                    } catch (e: NullPointerException) {
                        e.printStackTrace()
                    }
                }
            }

        }

        override fun onPlayerError(error: com.google.android.exoplayer2.ExoPlaybackException?) {
            super.onPlayerError(error)
        }
    }

    private fun prepareVideoPlayer() {
        simplePlayer = ExoPlayerFactory.newSimpleInstance(context)
        cacheDataSourceFactory = CacheDataSourceFactory(
            simpleCache,
            DefaultHttpDataSourceFactory(
                Util.getUserAgent(
                    context,
                    "exo"
                )
            )
        )
    }

    private fun getPlayer(): SimpleExoPlayer? {
        if (simplePlayer == null) {
            prepareVideoPlayer()
        }
        return simplePlayer
    }

    private fun prepareMedia(linkUrl: String) {
        Log.i("prepareMedia linkUrl", " $linkUrl")

        val uri = Uri.parse(linkUrl)

        val mediaSource =
            ProgressiveMediaSource.Factory(cacheDataSourceFactory).createMediaSource(uri)

        simplePlayer?.prepare(mediaSource, true, true)
        simplePlayer?.repeatMode = Player.REPEAT_MODE_ONE
        // simplePlayer?.playWhenReady = true
        simplePlayer?.addListener(playerCallback)

        toPlayVideoPosition = -1
    }

    private fun setArtwork(drawable: Drawable, playerView: PlayerView) {
        playerView.useArtwork = true
        playerView.defaultArtwork = drawable
    }

    private fun playVideo() {
        simplePlayer?.playWhenReady = true
    }

    private fun restartVideo() {
        if (simplePlayer == null) {
            storyUrl?.let { prepareMedia(it) }
        } else {
            if (!simplePlayer!!.isPlaying) {
                simplePlayer?.seekToDefaultPosition()
                simplePlayer?.playWhenReady = true
            }

        }
    }

    private fun pauseVideo() {
        simplePlayer?.playWhenReady = false
    }

    private fun releasePlayer() {
        simplePlayer?.stop(true)
        simplePlayer?.release()
    }

    private fun sharePost(dataList: PlayPostData?) {

        val message =
            "https://loca-toca.com/Login/index?si=" + dataList?.influencer_code + "&id=" + dataList?.id
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        share.putExtra(Intent.EXTRA_TEXT, message)
        comment.context.startActivity(Intent.createChooser(share, "Share"))
        android.util.Log.e(
            "TAG",
            "onCliffck: " + SharedPrefEnc.getPref(
                context,
                "user_id"
            ) + "  " + dataList?.id + "  " + dataList?.play_type
        )
        val jsonObject = JSONObject()
        jsonObject["id"] = dataList?.id
        jsonObject["share_type"] = dataList?.play_type
        jsonObject["user_id"] = SharedPrefEnc.getPref(context, "user_id")
        apiInterface?.getAddShare(jsonObject)?.enqueue(object : Callback<AddShare> {
            override fun onResponse(call: Call<AddShare>, response: Response<AddShare>) {
                android.util.Log.e("TAG", "onRespoxxnse: " + response.body()!!.message)
//                shares.setText(" " + response.body()!!.data!!.shareCount)
            }

            override fun onFailure(call: Call<AddShare>, t: Throwable) {
//                Toast.makeText(app, "Item Share Fail", Toast.LENGTH_SHORT).show()
            }
        })
    }


}