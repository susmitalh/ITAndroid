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
import com.locatocam.app.data.responses.settings.pendingPost.Detail
import com.locatocam.app.views.settings.PendingClickEvents

class PendingRollsApprovalAdapter (private val list: List<Detail>, private val context: Context,private val click: PendingClickEvents) : RecyclerView.Adapter<PendingRollsApprovalAdapter.viewHolder>() {

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var textPostedBy: TextView = itemView.findViewById(R.id.textPostedBy)
        var textPostedOn: TextView = itemView.findViewById(R.id.textPostedOn)
        var textPostType: TextView = itemView.findViewById(R.id.textPostType)
        var textHeadline: TextView = itemView.findViewById(R.id.textHeadline)
        var imagePostedFile: ImageView = itemView.findViewById(R.id.imagePostedFile)
        var btn_Reject: AppCompatButton =itemView.findViewById(R.id.btn_Reject)
        var btn_View: AppCompatButton =itemView.findViewById(R.id.btn_View)
        var btn_approve: AppCompatButton =itemView.findViewById(R.id.btn_approve)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.pending_rolls_item_layout, parent, false)

        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {

        holder.textPostedBy.text = list[position].posted_by
        holder.textPostedOn.text = list[position].posted_on
        holder.textPostType.text = list[position].file_type
        holder.textHeadline.text = list[position].description
        Glide.with(context)
            .load(Uri.parse(list[position].file))
            .into(holder.imagePostedFile)

        holder.btn_Reject.setOnClickListener {
            click.PendingReject(holder.btn_Reject,list[position],"pending","rolls")
        }
        holder.btn_View.setOnClickListener {
            click.Pendingview(holder.btn_View,list[position],"pending","rolls")
        }
        holder.btn_approve.setOnClickListener {
            click.PendingApprove(holder.btn_View,list[position],"pending","rolls")
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}