package com.locatocam.app.views.home.header

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.locatocam.app.R
import com.locatocam.app.data.responses.rolls_and_short_videos.Data

class RollsAndShortVideosAdapter(private val items:List<Data>, private val iTopinfluencer: IHeaderEvents):
    RecyclerView.Adapter<RollsAndShortVideosAdapter.MostPopularVideosViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MostPopularVideosViewHolder {
        var view:View = LayoutInflater.from(parent.context).inflate(R.layout.row_layout_rolls_and_short_videos, parent, false)
        return MostPopularVideosViewHolder(view)
    }

    override fun onBindViewHolder(holder: MostPopularVideosViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.name
        holder.views.text = item.views



        try {
            Glide.with(holder.video_image.context)
                .load(item.screenshot)
                .into(holder.video_image)

            Log.i("ki8888",item.screenshot.toString())
        }catch (e:Exception){
            Log.i("ki8888",e.message.toString())
        }

        holder.video_image.setOnClickListener {
            iTopinfluencer.onItemRollsAndShortVideos(item.id.toString())
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class MostPopularVideosViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        var video_image:ImageView
        var name:TextView
        var views:TextView
        init {
            name= itemView.findViewById(R.id.name)
            views= itemView.findViewById(R.id.views)
            video_image= itemView.findViewById(R.id.video_image)
        }

    }
}