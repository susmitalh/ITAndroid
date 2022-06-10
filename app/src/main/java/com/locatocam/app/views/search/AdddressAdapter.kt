package com.locatocam.app.views.search

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.locatocam.app.R
import com.locatocam.app.data.responses.address.Data

import android.view.MenuInflater
import android.widget.PopupMenu
import android.widget.Toast
import com.locatocam.app.views.MainActivity


class AdddressAdapter(private val items:List<Data>,private val clickEvents: ClickEvents):
    RecyclerView.Adapter<AdddressAdapter.TopInfluencerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopInfluencerViewHolder {
        var view:View = LayoutInflater.from(parent.context).inflate(R.layout.row_layout_address, parent, false)
        return TopInfluencerViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopInfluencerViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.customer_address
        holder.addressLabel.text = item.address_save_as
        Log.e("TAG", "onBindViewHolder: "+item.latitude )
        holder.name.setOnClickListener {
           clickEvents.onClickAddress(item)
            Log.e("TAG", "onBindViewHolvfgder: "+item.latitude )
        }

        holder.actions.setOnClickListener {
            clickEvents.showPopup(holder.actions)
        }
    }


    override fun getItemCount(): Int {
        return items.size
    }

    class TopInfluencerViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        var name:TextView
        val addressLabel : TextView
        var actions:ImageView
        init {
            name= itemView.findViewById(com.locatocam.app.R.id.name)
            actions= itemView.findViewById(com.locatocam.app.R.id.more)
            addressLabel= itemView.findViewById(R.id.address_label)

        }

    }
}