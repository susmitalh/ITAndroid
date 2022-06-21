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
import com.locatocam.app.data.ApprovedPosts
import com.locatocam.app.data.PendingPost
import com.locatocam.app.data.responses.settings.Approved.Detail
import com.locatocam.app.views.settings.CompanyRejectedClickEvents

class ComapnyRejectedApprovalsAdapter (private val list: List<com.locatocam.app.data.responses.settings.companyRejected.Detail>, private val context: Context,
private val companyRejectedClickEvents: CompanyRejectedClickEvents) : RecyclerView.Adapter<ComapnyRejectedApprovalsAdapter.viewHolder>() {

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var textPostedBy: TextView = itemView.findViewById(R.id.textPostedBy)
        var textPostedOn: TextView = itemView.findViewById(R.id.textPostedOn)
        var textPostType: TextView = itemView.findViewById(R.id.textPostType)
        var textHeadline: TextView = itemView.findViewById(R.id.textHeadline)
        var textSubHeader: TextView = itemView.findViewById(R.id.textSubHeader)
        var textBrand: TextView = itemView.findViewById(R.id.textBrand)
        var textContent: TextView = itemView.findViewById(R.id.textContent)
        var imagePostedFile: ImageView = itemView.findViewById(R.id.imagePostedFile)
        var textapprovedBy:TextView=itemView.findViewById(R.id.textapprovedBy)
        var textappirovedTime:TextView=itemView.findViewById(R.id.textappirovedTime)
        var textBrandStatus:TextView=itemView.findViewById(R.id.textBrandStatus)
        var btn_view: AppCompatButton =itemView.findViewById(R.id.btn_view)
        var btn_repost: AppCompatButton =itemView.findViewById(R.id.btn_repost)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.rejected_post_item_layout, parent, false)

        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {

        holder.textBrand.text = list[position].brand
        holder.textPostedBy.text = list[position].posted_by
        holder.textPostedOn.text = list[position].posted_on
        holder.textPostType.text = list[position].file_type
        holder.textHeadline.text = list[position].header
        holder.textSubHeader.text = list[position].sub_header
        holder.textContent.text = list[position].description
        holder.textapprovedBy.text=list[position].rejected_by
        holder.textappirovedTime.text=list[position].rejected_time
        holder.textBrandStatus.text=list[position].rejected_reason
        Glide.with(context)
            .load(Uri.parse(list[position].file))
            .into(holder.imagePostedFile);
        holder.btn_view.setOnClickListener {
            companyRejectedClickEvents.Rejectedview(holder.btn_view,list[position],"rejected","rolls")
        }
        holder.btn_repost.setOnClickListener {
            companyRejectedClickEvents.RejectedRepost(holder.btn_repost,list[position],"rejected","rolls")
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}