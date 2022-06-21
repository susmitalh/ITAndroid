package com.locatocam.app.views.settings.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.locatocam.app.R
import com.locatocam.app.data.responses.settings.Approved.Detail

class ApprovedRollsApprovalsAdapter (private val list: List<Detail>, private val context: Context,private val clickEvents: com.locatocam.app.views.settings.ApprovedClickEvents) : RecyclerView.Adapter<ApprovedRollsApprovalsAdapter.viewHolder>() {

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var textPostedBy: TextView = itemView.findViewById(R.id.textPostedBy)
        var textPostedOn: TextView = itemView.findViewById(R.id.textPostedOn)
        var textPostType: TextView = itemView.findViewById(R.id.textPostType)
        var textHeadline: TextView = itemView.findViewById(R.id.textHeadline)
        //var textSubHeader: TextView = itemView.findViewById(R.id.textSubHeader)
        //var textBrand: TextView = itemView.findViewById(R.id.textBrand)
//        var textContent: TextView = itemView.findViewById(R.id.textContent)
        var imagePostedFile: ImageView = itemView.findViewById(R.id.imagePostedFile)
        var textapprovedBy:TextView=itemView.findViewById(R.id.textapprovedBy)
        var textappirovedTime:TextView=itemView.findViewById(R.id.textappirovedTime)
        var btn_romve: AppCompatButton =itemView.findViewById(R.id.btn_romve)
        var btn_view: AppCompatButton =itemView.findViewById(R.id.btn_view)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.approved_rolls_item_layout, parent, false)

        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {

        holder.textPostedBy.text = list[position].posted_by
        holder.textPostedOn.text = list[position].posted_on
        holder.textPostType.text = list[position].file_type
        holder.textHeadline.text = list[position].description
        holder.textapprovedBy.text=list[position].approved_by
        holder.textappirovedTime.text=list[position].approved_time
        Glide.with(context)
            .load(Uri.parse(list[position].file))
            .into(holder.imagePostedFile);
        holder.btn_romve.setOnClickListener {
            clickEvents.ApprovedReject(holder.btn_romve,list[position],"approved","rolls")
        }
        holder.btn_view.setOnClickListener {
            clickEvents.Approvedview(holder.btn_view,list[position],"approved","rolls")
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}