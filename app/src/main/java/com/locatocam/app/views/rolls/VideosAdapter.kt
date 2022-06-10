package com.locatocam.app.views.rolls

import android.R.attr
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.locatocam.app.R
import com.locatocam.app.data.responses.VideoItem
import com.locatocam.app.data.responses.rolls_and_short_videos.Data
import com.locatocam.app.databinding.RowLayoutVideoBinding
import android.R.attr.visible
import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver

import androidx.annotation.NonNull
import androidx.core.view.isVisible
import com.locatocam.app.data.responses.playrolls.RollsData


class VideosAdapter(private val newsitems:List<RollsData>): RecyclerView.Adapter<VideosAdapter.SliderViewHolder>() {

    inner class SliderViewHolder(val binding: RowLayoutVideoBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(rollsitem: RollsData){
            binding.videoView.setVideoURI(Uri.parse(rollsitem.video_url))

        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        //val binding = LayoutSlideItemBinding.inflate(inflater)
        val binding: RowLayoutVideoBinding= DataBindingUtil.inflate(
            inflater,
            R.layout.row_layout_video,
            parent,
            false
        )
        return SliderViewHolder(binding)
    }

    override fun onViewAttachedToWindow(holder: SliderViewHolder) {
            holder.binding.videoView.start()

        super.onViewAttachedToWindow(holder)
    }


    override fun onViewDetachedFromWindow(holder: SliderViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.binding.videoView.isVisible
        holder.binding.videoView.pause()
    }
    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.bind(newsitems[position])
    }

    override fun getItemCount(): Int {
        return newsitems.size
    }

    fun getItem(position: Int):RollsData{
        return newsitems[position]
    }

    fun addItemsAll(newItems:List<VideoItem>){
        //newsitems.addAll(newItems)
    }


}