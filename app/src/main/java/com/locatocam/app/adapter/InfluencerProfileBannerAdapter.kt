package com.locatocam.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.locatocam.app.R
import com.locatocam.app.data.responses.user_details.Logo
import de.hdodenhof.circleimageview.CircleImageView

class InfluencerProfileBannerAdapter(var logodataList: List<Logo>?) : RecyclerView.Adapter<InfluencerProfileBannerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      var view=LayoutInflater.from(parent.context).inflate(R.layout.influencer_profile_banner_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (logodataList?.get(position)?.banner.equals("")){
            holder.banner_layout.visibility=View.GONE
            holder.profile_layout.visibility=View.VISIBLE
            Glide.with(holder.profile_image_layout.context)
                .load(logodataList?.get(position)?.logo)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .circleCrop()
                .into(holder.profile_image_layout)

        }else {

            holder.banner_layout.visibility = View.VISIBLE
            holder.profile_layout.visibility = View.GONE

            Glide.with(holder.bannerImage.context)
                .load(logodataList?.get(position)?.banner)
                .into(holder.bannerImage)

            Glide.with(holder.profile_image.context)
                .load(logodataList?.get(position)?.logo)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .circleCrop()
                .into(holder.profile_image)
        }
    }

    override fun getItemCount(): Int {
       return logodataList!!.size
    }

    class ViewHolder (itemView: View):RecyclerView.ViewHolder(itemView){

        var bannerImage: AppCompatImageView =itemView.findViewById(R.id.profile_banner)
        var profile_image:CircleImageView=itemView.findViewById(R.id.profile_image)
        var profile_image_layout:CircleImageView=itemView.findViewById(R.id.profile_image_layout)
        var banner_layout: ConstraintLayout =itemView.findViewById(R.id.banner_layout)
        var profile_layout: ConstraintLayout =itemView.findViewById(R.id.profile_layout)

    }
}