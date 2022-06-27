package com.locatocam.app.views.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.locatocam.app.R
import com.locatocam.app.data.responses.notification.Data
import java.util.*

class NotificationAdapter(val data:MutableList<Data>):
    RecyclerView.Adapter<NotificationAdapter.ItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        var view:View= LayoutInflater.from(parent.context).inflate(R.layout.row_layout_view_activity,parent,false,)

        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        var item=data.get(position)

            holder.name.setText(item.title)
            holder.type.setText(item.notification)
            holder.time.setText(item.time)

            Glide.with(holder.itemView.context)
                .load("https://loca-toca.com/assets/logo/notification.png")
                .centerCrop()
                .into(holder.image);

    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ItemViewHolder(view: View):RecyclerView.ViewHolder(view){
        var name:TextView=view.findViewById(R.id.name)
        var type:TextView=view.findViewById(R.id.type)
        var time:TextView=view.findViewById(R.id.time)
        var image:ImageView=view.findViewById(R.id.imageView)

    }
}