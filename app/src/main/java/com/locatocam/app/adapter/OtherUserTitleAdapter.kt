package com.locatocam.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.locatocam.app.R
import com.locatocam.app.data.responses.user_details.BrandDetail

class OtherUserTitleAdapter(var brandDetails: List<BrandDetail>?) : RecyclerView.Adapter<OtherUserTitleAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view= LayoutInflater.from(parent.context).inflate(R.layout.other_user_title_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.othe_title_txt.setText(brandDetails?.get(position)?.brand_name)
    }

    override fun getItemCount(): Int {
        return brandDetails!!.size
    }

    class ViewHolder (itemView: View):RecyclerView.ViewHolder(itemView){
        var othe_title_txt: TextView =itemView.findViewById(R.id.othe_title_txt)
    }
}