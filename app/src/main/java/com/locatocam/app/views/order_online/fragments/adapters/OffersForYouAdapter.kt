package com.locatocam.app.views.order_online.fragments.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.locatocam.app.R
import com.locatocam.app.data.responses.offers_for_you.Data
import com.locatocam.app.views.order_online.fragments.SingleClickEvent


class OffersForYouAdapter(private val items:List<Data>, private val clickEvents: SingleClickEvent):
    RecyclerView.Adapter<OffersForYouAdapter.TopInfluencerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopInfluencerViewHolder {
        var view:View = LayoutInflater.from(parent.context).inflate(R.layout.row_offers_for_you_item_layout, parent, false)
        return TopInfluencerViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopInfluencerViewHolder, position: Int) {
        val item = items[position]

        item.apply {
            holder.campaign_label.text = campaign_label
            holder.offer.text = campaign_label2
            Glide.with(holder.image.context)
                .load(image)
                .into(holder.image)
        }

        holder.main_lay.setOnClickListener {
           clickEvents.onclickItem(item.id)
        }

    }


    override fun getItemCount(): Int {
        return items.size
    }

    class TopInfluencerViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        var campaign_label:TextView
        var offer:TextView
        var image:ImageView
        var main_lay: LinearLayout
        init {
            campaign_label= itemView.findViewById(com.locatocam.app.R.id.campaign_label)
            offer= itemView.findViewById(com.locatocam.app.R.id.offer)
            image= itemView.findViewById(com.locatocam.app.R.id.image)
            main_lay= itemView.findViewById(com.locatocam.app.R.id.main_lay)
        }

    }
}