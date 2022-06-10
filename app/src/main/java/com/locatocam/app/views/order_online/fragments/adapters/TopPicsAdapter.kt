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
import com.locatocam.app.data.responses.top_pics.Data
import com.locatocam.app.views.order_online.fragments.SingleClickEvent
import de.hdodenhof.circleimageview.CircleImageView


class TopPicsAdapter(private val items:List<Data>, private val clickEvents: SingleClickEvent):
    RecyclerView.Adapter<TopPicsAdapter.TopInfluencerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopInfluencerViewHolder {
        var view:View = LayoutInflater.from(parent.context).inflate(R.layout.row_top_pics_item_layout, parent, false)
        return TopInfluencerViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopInfluencerViewHolder, position: Int) {
        val item = items[position]

        item.apply {
            holder.name.text = name
            Glide.with(holder.image.context)
                .load(image_url)
                .into(holder.image)
        }


        holder.name.setOnClickListener {
            clickEvents.onclickItem(item.id)
        }

    }


    override fun getItemCount(): Int {
        return items.size
    }

    class TopInfluencerViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        var name:TextView
        var image:CircleImageView
        init {
            name= itemView.findViewById(com.locatocam.app.R.id.name)
            image= itemView.findViewById(com.locatocam.app.R.id.image)
        }

    }
}