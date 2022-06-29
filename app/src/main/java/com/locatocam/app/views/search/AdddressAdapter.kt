package com.locatocam.app.views.search

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.locatocam.app.R
import com.locatocam.app.data.responses.address.Data

import android.widget.*


class AdddressAdapter(var items:ArrayList<Data>,private val clickEvents: ClickEvents):
    RecyclerView.Adapter<AdddressAdapter.TopInfluencerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopInfluencerViewHolder {
        var view:View = LayoutInflater.from(parent.context).inflate(R.layout.row_layout_address, parent, false)
        return TopInfluencerViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopInfluencerViewHolder, position: Int) {

        val item = items[position]

        var flatNo=""
        var landmark=""

        if (!item.door_no!!.isEmpty()||!item.door_no.equals("")){
            flatNo=item.door_no+", "
        }else if (!item.cust_landmark!!.isEmpty()||!item.cust_landmark.equals("")){
            landmark=item.cust_landmark+", "
        }

        holder.name.text = flatNo+landmark+item.customer_address
        holder.addressLabel.text = item.address_save_as
        Log.e("TAG", "onBindViewHolder: "+item.latitude )
        holder.name.setOnClickListener {
           clickEvents.onClickAddress(item)
            Log.e("TAG", "onBindViewHolvfgder: "+item.latitude )
        }

        holder.actions.setOnClickListener {
            clickEvents.showPopup(holder.actions,item,position)
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