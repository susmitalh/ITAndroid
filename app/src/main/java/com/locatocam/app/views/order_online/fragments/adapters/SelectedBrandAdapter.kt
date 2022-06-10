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
import com.locatocam.app.data.responses.selected_brands.Data
import com.locatocam.app.views.order_online.fragments.SingleClickEvent


class SelectedBrandAdapter(private val items:List<Data>, private val clickEvents: SingleClickEvent):
    RecyclerView.Adapter<SelectedBrandAdapter.TopInfluencerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopInfluencerViewHolder {
        var view:View = LayoutInflater.from(parent.context).inflate(R.layout.row_selected_brand_item_layout, parent, false)
        return TopInfluencerViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopInfluencerViewHolder, position: Int) {
        val item = items[position]

        item.apply {
            holder.name.text = name
        }
        //holder.name.text = item.name
        holder.timings.text=item.opening_hours_time
        holder.cuisine.text=item.cuisine
        holder.distance.text=item.distance.toString()+" km"


        Glide.with(holder.image.context)
            .load(item.image_url)
            .into(holder.image)

        holder.main_lay.setOnClickListener {
           clickEvents.onclickItem(item.store_id)
        }

    }


    override fun getItemCount(): Int {
        return items.size
    }

    class TopInfluencerViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        var timings:TextView
        var cuisine:TextView
        var distance:TextView
        var name:TextView
        var image:ImageView
        var main_lay:LinearLayout
        init {
            timings= itemView.findViewById(com.locatocam.app.R.id.timings)
            cuisine= itemView.findViewById(com.locatocam.app.R.id.cuisine)
            distance= itemView.findViewById(com.locatocam.app.R.id.distance)
            name= itemView.findViewById(com.locatocam.app.R.id.name)
            image= itemView.findViewById(com.locatocam.app.R.id.image)
            main_lay= itemView.findViewById(com.locatocam.app.R.id.main_lay)
        }

    }
}