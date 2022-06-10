package com.locatocam.app.views.comments

import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.locatocam.app.R
import com.locatocam.app.data.responses.comments.Data
import de.hdodenhof.circleimageview.CircleImageView


class CommentsAdapter(private val items:List<Data>):
    RecyclerView.Adapter<CommentsAdapter.TopInfluencerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopInfluencerViewHolder {
        var view:View = LayoutInflater.from(parent.context).inflate(R.layout.row_layout_comments, parent, false)
        return TopInfluencerViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopInfluencerViewHolder, position: Int) {
        val item = items[position]
        holder.message.text = Html.fromHtml("<b>"+item.profile_name+"</b> <br></br>"+item.comment)
        holder.time.text = item.time_ago

//        Log.i("lo999",item.comment)
        Glide.with(holder.profile.context)
            .load(item.profile_pic)
            .centerCrop()
            .into(holder.profile);
    }


    override fun getItemCount(): Int {
        return items.size
    }

    class TopInfluencerViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        var message:TextView
        var time:TextView
        var profile:CircleImageView
        init {
            message= itemView.findViewById(com.locatocam.app.R.id.message)
            profile= itemView.findViewById(com.locatocam.app.R.id.userimage)
            time= itemView.findViewById(com.locatocam.app.R.id.time)

        }

    }
}