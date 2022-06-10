package com.locatocam.app.views.settings.adapters

import android.content.Context
import android.net.Uri
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.adapters.TextViewBindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.locatocam.app.R
import com.locatocam.app.data.responses.user_model.Document
import com.locatocam.app.data.responses.user_model.SocialDetail
import com.locatocam.app.viewmodels.SettingsViewModel

class SocialDetailsAdapter (private val list: List<SocialDetail>, private val context: Context,private val viewModel: SettingsViewModel) : RecyclerView.Adapter<SocialDetailsAdapter.viewHolder>(){
    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {

        val label = itemView.findViewById<TextView>(R.id.fbLabel)
        val idText = itemView.findViewById<TextView>(R.id.idText)
        val link = itemView.findViewById<TextView>(R.id.fbLinkText)
        val followers = itemView.findViewById<TextView>(R.id.followersText)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SocialDetailsAdapter.viewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.social_data_layout, parent, false)

        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: SocialDetailsAdapter.viewHolder, position: Int) {

        holder.label.text = list[position].name
        holder.link.text = list[position].link
        holder.followers.text = list[position].follower
        holder.idText.text = list[position].id

        viewModel.setSocialDetailsList(com.locatocam.app.data.requests.reqUserProfile.SocialDetail(
            link = holder.link.text.toString(),
            id = holder.idText.text.toString(),
            follower = holder.followers.text.toString(),
            name = holder.label.text.toString()
        ))

        holder.followers.setOnFocusChangeListener { view, b ->

            if (!b){

                if (!holder.link.text.equals(list[position].link) || holder.followers.text.equals(list[position].follower)){

                    viewModel.addSocialData(com.locatocam.app.data.requests.reqUserProfile.SocialDetail(
                        link = holder.link.text.toString(),
                        id = holder.idText.text.toString(),
                        follower = holder.followers.text.toString(),
                        name = holder.label.text.toString()
                    ),position)

                }

            }


        }


        holder.link.setOnFocusChangeListener { view, b ->

            if (!b){

                if (!holder.link.text.equals(list[position].link) || holder.followers.text.equals(list[position].follower)){

                    viewModel.addSocialData(com.locatocam.app.data.requests.reqUserProfile.SocialDetail(
                        link = holder.link.text.toString(),
                        id = holder.idText.text.toString(),
                        follower = holder.followers.text.toString(),
                        name = holder.label.text.toString()
                    ),position)

                }

            }


        }


    }

    override fun getItemCount(): Int {
        return list.size
    }
}