package com.locatocam.app.views.followers.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.locatocam.app.R
import com.locatocam.app.data.responses.followers.*
import com.locatocam.app.views.followers.interfaceFollow.Follower
import java.util.*

class InfluencerFollowersAdapter(val data:List<Any>,private val click: Follower):
    RecyclerView.Adapter<InfluencerFollowersAdapter.ItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        var view:View= LayoutInflater.from(parent.context).inflate(
            R.layout.row_layout_followers_1,
            parent,
            false
        )

        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        var item=data.get(position)
        if (item is InfluencerFollower){
            holder.name.setText(item.name)
            holder.type.setText(item.user_type)

            Glide.with(holder.itemView.context)
                .load(item.photo)
                .centerCrop()
                .into(holder.image);
            holder.follow.text=item.follow_status
            holder.follow.setOnClickListener {
                click.follow("influencer","follow",item.user_id.toInt())
            }
        }else if (item is InfluencerFollowing){
            holder.name.setText(item.name)
            holder.type.setText(item.user_type)

            Glide.with(holder.itemView.context)
                .load(item.photo)
                .centerCrop()
                .into(holder.image);
        }else if (item is BrandFollower){
            holder.name.setText(item.name)
            holder.type.setText(item.user_type)

            Glide.with(holder.itemView.context)
                .load(item.photo)
                .centerCrop()
                .into(holder.image)
            holder.follow.setOnClickListener {
                click.follow("brand","follow",item.user_id.toInt())
            }
            holder.follow.text=item.follow_status
        }else if (item is BrandFollowing){
            holder.name.setText(item.name)
            holder.type.setText(item.user_type)

            Glide.with(holder.itemView.context)
                .load(item.photo)
                .centerCrop()
                .into(holder.image);
        }else if (item is PeopleFollower){
            holder.name.setText(item.name)
            holder.type.setText(item.user_type)

            Glide.with(holder.itemView.context)
                .load(item.photo)
                .centerCrop()
                .into(holder.image)
            holder.follow.text=item.follow_status
            holder.follow.setOnClickListener {
                click.follow("people","follow",item.user_id.toInt())
            }
        }else if (item is PeopleFollowing){
            holder.name.setText(item.name)
            holder.type.setText(item.user_type)

            Glide.with(holder.itemView.context)
                .load(item.photo)
                .centerCrop()
                .into(holder.image);
        }



    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ItemViewHolder(view: View):RecyclerView.ViewHolder(view){
        var name:TextView=view.findViewById(R.id.name)
        var type:TextView=view.findViewById(R.id.type)
        var image:ImageView=view.findViewById(R.id.imageView)
        var follow:TextView=view.findViewById(R.id.follow)

    }
}

