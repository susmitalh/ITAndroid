package com.locatocam.app.views.order_online.fragments.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.locatocam.app.R
import com.locatocam.app.data.responses.offers_for_you.Data
import com.locatocam.app.views.order_online.AddProductActivity
import com.locatocam.app.views.order_online.fragments.SingleClickEvent
import com.locatocam.app.views.settings.adapters.UserMenuSubItemAdapter

class OffersAdapter(private val list: List<com.locatocam.app.data.responses.brand_list.Data>,  private val clickEvents: SingleClickEvent) : RecyclerView.Adapter<OffersAdapter.viewHolder>(){

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
        val title = itemView.findViewById<TextView>(R.id.brand_name)
        val cuisine = itemView.findViewById<TextView>(R.id.cusine)
        val place = itemView.findViewById<TextView>(R.id.location_name)
        val distance = itemView.findViewById<TextView>(R.id.distance)
        val open_hours = itemView.findViewById<TextView>(R.id.open_hours)
        val banner = itemView.findViewById<ImageView>(R.id.thumbnile)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OffersAdapter.viewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_offer, parent, false)
        Log.e("onBindViewHolder",list[0].name.toString())

        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: OffersAdapter.viewHolder, position: Int) {
        holder.title.text = list[position].name
        holder.cuisine.text = list[position].cuisine
        holder.place.text = list[position].secondline
        holder.distance.text = list[position].distance.toString()
        holder.open_hours.text = list[position].opening_hours
        Glide.with(holder.banner.context)
            .load(list[position].image_url)
            .into(holder.banner)

        holder.itemView.setOnClickListener {
           clickEvents.onclickItem(list[position].store_id)
        }
        Log.e("onBindViewHolder",list[position].image_url)

    }

    override fun getItemCount(): Int {
        return list.size
    }
}