package com.locatocam.app.views.view_activity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.locatocam.app.R
import com.locatocam.app.data.responses.followers.*
import com.locatocam.app.data.responses.my_activity.Data
import java.util.*

class ViewActivityAdapter(val data:List<Data>):
    RecyclerView.Adapter<ViewActivityAdapter.ItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        var view:View= LayoutInflater.from(parent.context).inflate(R.layout.row_layout_view_activity,parent,false,)

        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        var item=data.get(position)

            holder.name.setText(item.name)
            holder.type.setText(item.text)
            holder.time.setText(item.time)

            Glide.with(holder.itemView.context)
                .load(item.logo)
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