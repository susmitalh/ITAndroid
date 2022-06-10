package com.locatocam.app.views.home.header

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.locatocam.app.R
import com.locatocam.app.data.responses.top_influencers.Data
import com.locatocam.app.data.responses.user_details.BrandDetail
import de.hdodenhof.circleimageview.CircleImageView

class TopBrandsAdapter(private val items:List<BrandDetail>, private val iTopinfluencer: IHeaderEvents):
    RecyclerView.Adapter<TopBrandsAdapter.TopInfluencerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopInfluencerViewHolder {
        var view:View = LayoutInflater.from(parent.context).inflate(R.layout.row_layout_top_influencer, parent, false)
        return TopInfluencerViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopInfluencerViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.brand_name
        holder.posts.text = ""

        Glide.with(holder.Image.context)
            .load(item.brand_logo)
            .fitCenter()
            .into(holder.Image);

        holder.Image.setOnClickListener {
            //iTopinfluencer.onItemClick(item.inf_id.toString(),item.inf_code.toString())
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