package com.locatocam.app.views.settings.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.locatocam.app.R
import com.locatocam.app.data.responses.followers.*
import com.locatocam.app.data.responses.settings.ViewBlockUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
class ViewBlockedUserAdapter(val data:List<ViewBlockUserData>,private val context: Context):
    RecyclerView.Adapter<ViewBlockedUserAdapter.ItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        var view:View= LayoutInflater.from(parent.context).inflate(R.layout.row_layout_view_blocked_user,parent,false,)

        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        var item=data.get(position)
            holder.name.setText(item.user_name)
            holder.type.setText(item.last_active_on)

            Glide.with(holder.itemView.context)
                .load(item.user_photo)
                .centerCrop()
                .into(holder.image);
        holder.unblock.setOnClickListener {

        }

    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ItemViewHolder(view: View):RecyclerView.ViewHolder(view){
        var name:TextView=view.findViewById(R.id.name)
        var type:TextView=view.findViewById(R.id.type)
        var image:ImageView=view.findViewById(R.id.imageView)
        var unblock:Button=view.findViewById(R.id.unblock)

    }

}