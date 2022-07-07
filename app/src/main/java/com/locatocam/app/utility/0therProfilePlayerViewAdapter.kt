package com.locatocam.app.utility

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.locatocam.app.MyApp
import com.locatocam.app.R
import com.locatocam.app.utils.Constant



class OtherProfilePlayerViewAdapter {

  companion object {

      var playersMap: MutableMap<Int, SimpleExoPlayer>  = mutableMapOf()

      private var currentPlayingVideo: Pair<Int, SimpleExoPlayer>? = null
      fun releaseAllPlayers(){
          playersMap.map {
              it.value.release()
          }
      }

      // call when item recycled to improve performance
      fun releaseRecycledPlayers(index: Int){
          playersMap[index]?.release()
      }

      // call when scroll to pause any playing player
      fun pauseCurrentPlayingVideo(){
          if (currentPlayingVideo != null){
              currentPlayingVideo?.second?.playWhenReady = false
          }
      }

      fun resumeCurrentPlayingVideo() {
          if (currentPlayingVideo != null) {
              currentPlayingVideo?.second?.playWhenReady = true
          }
      }


      fun playIndexThenPausePreviousPlayer(index: Int) {
          Log.e("TAG", "playIndexThenPausePreviousPlayer: release " +index)
          if (playersMap[index]?.playWhenReady == false) {
              pauseCurrentPlayingVideo()
              playersMap[index]?.playWhenReady = true
              currentPlayingVideo = Pair(index, playersMap.get(index)!!)
          }

      }

      @JvmStatic
      @BindingAdapter(value = ["thumbnail", "error"], requireAll = false)
      fun loadImage(view: ImageView, profileImage: String?, error: Int) {
          if (!profileImage.isNullOrEmpty()) {
              Glide.with(view.context)
                  .setDefaultRequestOptions(
                      RequestOptions()
                          .placeholder(R.drawable.ic_video_placeholder)
                          .error(R.drawable.ic_white_background))
                  .load(profileImage)
                  .diskCacheStrategy(DiskCacheStrategy.ALL)
                  .into(view)
          }
      }

      @JvmStatic
      @BindingAdapter(
          value = ["video_url_other", "on_state_change_other", "thumbnail_other", "item_index_other", "autoPlay_other", "btnMuteUnmute_other","type_other"],
          requireAll = false
      )
      fun PlayerView.loadVideo(
          url: String,
          callback: PlayerStateCallback,
//          progressbar: ProgressBar,
          thumbnail: ImageView,
          item_index: Int? = null,
          autoPlay_other: Boolean = true,
//          btnVideoClick: View,
          btnMuteUnmute: ImageView,
          type:String
//          isMutedVideo: String
      ) {
          if (url == null) return
          Log.e("TAG", "loadVideo: "+playersMap.toString() )
          Log.e("TAG", "loadVideo: "+currentPlayingVideo.toString() )
          val mediaItem: MediaItem = MediaItem.fromUri(Uri.parse(url))
          val httpDataSourceFactory = DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true)
          val defaultDataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(context, httpDataSourceFactory)
          val cacheDataSourceFactory = CacheDataSource.Factory()
              .setCache(MyApp.simpleCache!!)
              .setUpstreamDataSourceFactory(defaultDataSourceFactory)
              .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

          val mediaSources: MediaSource = ProgressiveMediaSource.Factory(cacheDataSourceFactory).createMediaSource(mediaItem)



          val player = SimpleExoPlayer.Builder(context).build()
          player.addMediaSource(mediaSources)
          player.playWhenReady = autoPlay_other
          player.repeatMode = Player.REPEAT_MODE_ALL
          player.prepare()

          // When changing track, retain the latest frame instead of showing a black screen
          setKeepContentOnPlayerReset(true)
          this.useController = false
          this.player = player

/*          if(isMutedVideo.contentEquals("is_not_mute")) {
              btnMuteUnmute.visibility=View.VISIBLE
              if (Constant.videoMute) {
                  //mute
                  if (Constant.VOLUME == 0f) {
                      //get the volume before set to mute
                      Constant.VOLUME = player.volume
                  }
                  player.volume = 0F
                  btnMuteUnmute.setImageDrawable(
                      ContextCompat.getDrawable(
                          context,
                          R.drawable.ic_volume_off_grey_24dp
                      )
                  )
              } else {
                  btnMuteUnmute.setImageDrawable(
                      ContextCompat.getDrawable(
                          context,
                          R.drawable.ic_volume_up_grey_24dp
                      )
                  )
              }
          }else{*/
              if (Constant.VOLUME == 0f) {
                  //get the volume before set to mute
                  Constant.VOLUME = player.volume
              }
              player.volume = 0F
              btnMuteUnmute.visibility=View.GONE
//          }
          // add player with its index to map
          if (playersMap.containsKey(item_index))
              playersMap.remove(item_index)
          if (item_index != null)
              playersMap[item_index] = player

          this.player!!.addListener(object : Player.EventListener {

              override fun onPlayerError(error: PlaybackException) {
                  super.onPlayerError(error)
                  Log.e("TAG", "onPlayerError: "+error.message )
              }

              override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                  super.onPlayerStateChanged(playWhenReady, playbackState)


                  if (playWhenReady && playbackState == Player.STATE_READY) {
                      Log.e("TAG", "onPlayerStateChanged: STATE Ready" )

                      // media actually playing
                  } else if (playWhenReady) {
                      // might be idle (plays after prepare()),
                      // buffering (plays when data available)
                      // or ended (plays when seek away from end)
                  } else {
                      // player paused in any state
                      thumbnail.visibility = View.VISIBLE
//                      progressbar.visibility = View.VISIBLE
//                      btnVideoClick.visibility = View.GONE
                      if (Constant.videoMute) {
                          //mute
                          if (Constant.VOLUME == 0f) {
                              //get the volume before set to mute
                              Constant.VOLUME = player.volume
                          }
                          player.volume = 0F
                          btnMuteUnmute.setImageDrawable(
                              ContextCompat.getDrawable(
                                  context,
                                  R.drawable.ic_volume_off_grey_24dp
                              )
                          )
                      } else {
//                          player.volume = Constant.VOLUME
                          btnMuteUnmute.setImageDrawable(
                              ContextCompat.getDrawable(
                                  context,
                                  R.drawable.ic_volume_up_grey_24dp
                              )
                          )
                      }
                  }


                  if (playbackState == Player.STATE_BUFFERING) {
                      Log.e("TAG", "onPlayerStateChanged: STATE_BUFFERING" )

                      callback.onVideoBuffering(player)
                      // Buffering..
                      // set progress bar visible here
                      // set thumbnail visible
                      thumbnail.visibility = View.VISIBLE
//                      progressbar.visibility = View.VISIBLE
//                      btnVideoClick.visibility = View.GONE

                      if (Constant.videoMute) {
                          //mute
                          if (Constant.VOLUME == 0f) {
                              //get the volume before set to mute
                              Constant.VOLUME = player.volume
                          }
                          player.volume = 0F
                          btnMuteUnmute.setImageDrawable(
                              ContextCompat.getDrawable(
                                  context,
                                  R.drawable.ic_volume_off_grey_24dp
                              )
                          )
                      } else {
//                          player.volume = Constant.VOLUME
                          btnMuteUnmute.setImageDrawable(
                              ContextCompat.getDrawable(
                                  context,
                                  R.drawable.ic_volume_up_grey_24dp
                              )
                          )
                      }
                  }

                  if (playbackState == Player.STATE_READY) {
                      Log.e("TAG", "onPlayerStateChanged: STATE" )

                      // [PlayerView] has fetched the video duration so this is the block to hide the buffering progress bar
//                      progressbar.visibility = View.GONE
                      // set thumbnail gone

//                      if (type.equals("image")){
                      if (type.equals("image")){
                          thumbnail.visibility = View.VISIBLE
                      }else {
                          thumbnail.visibility = View.GONE
                      }
//                      btnVideoClick.visibility = View.VISIBLE

                      if (Constant.videoMute) {
                          //mute
                          if (Constant.VOLUME == 0f) {
                              //get the volume before set to mute
                              Constant.VOLUME = player.volume
                          }
                          player.volume = 0F
                          btnMuteUnmute.setImageDrawable(
                              ContextCompat.getDrawable(
                                  context,
                                  R.drawable.ic_volume_off_grey_24dp
                              )
                          )
                      } else {
//                          player.volume = Constant.VOLUME
                          player.volume = 1f
                          btnMuteUnmute.setImageDrawable(
                              ContextCompat.getDrawable(
                                  context,
                                  R.drawable.ic_volume_up_grey_24dp
                              )
                          )
                      }

                      callback.onVideoDurationRetrieved(this@loadVideo.player!!.duration, player)
                  }

                  if (playbackState == Player.STATE_READY && player.playWhenReady) {
                      Log.e("TAG", "onPlayerStateChanged: STATE_READY" )
                      // [PlayerView] has started playing/resumed the video
                      callback.onStartedPlaying(player)
                  }
              }
          })

          btnMuteUnmute.setOnClickListener {

              if (Constant.videoMute) {
                  //unmute
                  player.volume = Constant.VOLUME
                  Constant.videoMute = false
                  btnMuteUnmute.setImageDrawable(
                      ContextCompat.getDrawable(
                          context,
                          R.drawable.ic_volume_up_grey_24dp
                      )
                  )
              } else {
                  //mute
                  if (Constant.VOLUME == 0f) {
                      //get the volume before set to mute
                      Constant.VOLUME = player.volume
                  }
                  player.volume = 0F

                  Constant.videoMute = true
                  btnMuteUnmute.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_volume_off_grey_24dp)
                  )
              }

          }

         /* btnVideoClick.setOnClickListener {



          }*/
      }
  }

}