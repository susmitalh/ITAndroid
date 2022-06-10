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
import com.locatocam.app.data.responses.popular_videos.Data
import de.hdodenhof.circleimageview.CircleImageView

class MostPopularVideosAdapter(private val items:List<Data>, private val iTopinfluencer: IHeaderEvents):
    RecyclerView.Adapter<MostPopularVideosAdapter.MostPopularVideosViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MostPopularVideosViewHolder {
        var view:View = LayoutInflater.from(parent.context).inflate(R.layout.row_layout_most_popular_videos, parent, false)
        return MostPopularVideosViewHolder(view)
    }

    override fun onBindViewHolder(holder: MostPopularVideosViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.inf_name
        holder.views.text = item.post_views
        holder.company.text = item.brand_name

        Glide.with(holder.image.context)
            .load(item.inf_photo)
            .into(holder.image)

        try {
            Glide.with(holder.video_image.context)
                .load(item.post_screenshot)
                .into(holder.video_image)

        }catch (e:Exception){
            Log.i("ki8888",e.message.toString())
        }
        holder.video_image.setOnClickListener {
            iTopinfluencer.onItemMostPopularVideos(item.inf_id.toString(),item.inf_code.toString())
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    class MostPopularVideosViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        var image:CircleImageView
        var video_image:ImageView
        var name:TextView
        var views:TextView
        var company:TextView
        init {
            image= itemView.findViewById(R.id.profile)
            name= itemView.findViewById(R.id.name)
            views= itemView.findViewById(R.id.views)
            video_image= itemView.findViewById(R.id.video_image)
            company= itemView.findViewById(R.id.company)
        }

    }
}