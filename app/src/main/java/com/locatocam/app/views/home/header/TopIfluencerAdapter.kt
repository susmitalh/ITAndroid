package com.locatocam.app.views.home.header

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.locatocam.app.R
import com.locatocam.app.data.responses.top_influencers.Data
import de.hdodenhof.circleimageview.CircleImageView

class TopIfluencerAdapter(private val items:List<Data>,private val iTopinfluencer: IHeaderEvents):
    RecyclerView.Adapter<TopIfluencerAdapter.TopInfluencerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopInfluencerViewHolder {
        var view:View = LayoutInflater.from(parent.context).inflate(R.layout.row_layout_top_influencer, parent, false)
        return TopInfluencerViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopInfluencerViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.inf_name
        holder.posts.text = "${item.inf_post_count} Posts"

        Glide.with(holder.Image.context)
            .load(item.inf_photo)
            .fitCenter()
            .into(holder.Image);

        holder.Image.setOnClickListener {
            iTopinfluencer.onItemClick(item.inf_id.toString(),item.inf_code.toString())
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    class TopInfluencerViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        var Image:CircleImageView
        var name:TextView
        var posts:TextView
        init {
            Image= itemView.findViewById(R.id.profile)
            name= itemView.findViewById(R.id.name)
            posts= itemView.findViewById(R.id.posts)

        }

    }
}