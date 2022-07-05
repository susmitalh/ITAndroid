package com.locatocam.app.views.settings.adapters

import android.content.Context
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.caverock.androidsvg.SVG
import com.locatocam.app.R
import com.locatocam.app.data.responses.changeInfluencer.Data
import com.locatocam.app.views.settings.settingInterface.ChangeInfluencer

class ChangeInfluencerAdapter (private val list: List<Data>, private val context: Context,
     private val click: ChangeInfluencer
) : RecyclerView.Adapter<ChangeInfluencerAdapter.viewHolder>(){
    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {

        val imgFacebook = itemView.findViewById<ImageView>(R.id.imgFacebook)
        val facebook=itemView.findViewById<TextView>(R.id.facebook)
        val imgInstagram = itemView.findViewById<ImageView>(R.id.imgInstagram)
        val instagram=itemView.findViewById<TextView>(R.id.instagram)
        val imgYoutube = itemView.findViewById<ImageView>(R.id.imgYoutube)
        val youtube=itemView.findViewById<TextView>(R.id.youtube)
        val imgtwitter = itemView.findViewById<ImageView>(R.id.imgtwitter)
        val twitter=itemView.findViewById<TextView>(R.id.twitter)
        val imglinkedin = itemView.findViewById<ImageView>(R.id.imglinkedin)
        val linkedin=itemView.findViewById<TextView>(R.id.linkedin)
        val btn_changeInfluencer = itemView.findViewById<Button>(R.id.btn_changeInfluencer)
        val emailId = itemView.findViewById<TextView>(R.id.emailId)
        val name = itemView.findViewById<TextView>(R.id.name)
        val imagview=itemView.findViewById<ImageView>(R.id.imagview)
        val layoutFacebook=itemView.findViewById<LinearLayout>(R.id.layoutFacebook)
        val layoutInstagram=itemView.findViewById<LinearLayout>(R.id.layoutInstagram)
        val layoutYoutube=itemView.findViewById<LinearLayout>(R.id.layoutYoutube)
        val layoutTwitter=itemView.findViewById<LinearLayout>(R.id.layoutTwitter)
        val layoutLinkedin=itemView.findViewById<LinearLayout>(R.id.layoutLinkedin)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChangeInfluencerAdapter.viewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.row_change_influencer_layout, parent, false)

        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: ChangeInfluencerAdapter.viewHolder, position: Int) {
           holder.name.text=list[position].inf_name
           holder.emailId.text=list[position].inf_email
           Glide.with(context)
            .load(Uri.parse(list[position].inf_photo))
            .into(holder.imagview)
            list[position].inf_social_details?.forEach { that ->
            when (that.social_name) {
                "Facebook" -> {
                    val svg = SVG.getFromString(that.icon)
                    val drawable = PictureDrawable(svg.renderToPicture())
                    Glide.with(context).load(drawable).into(holder.imgFacebook)
                    holder.facebook.text = " " + that.follower
                }
                "Instagram" -> {
                    val svg = SVG.getFromString(that.icon)
                    val drawable = PictureDrawable(svg.renderToPicture())
                    Glide.with(context).load(drawable).into(holder.imgInstagram)

                    holder.instagram.text = " " + that.follower
                }
                "Youtube" -> {
                    val svg = SVG.getFromString(that.icon)
                    val drawable = PictureDrawable(svg.renderToPicture())
                    Glide.with(context).load(drawable).into(holder.imgYoutube)
                    holder.youtube.text = " " + that.follower
                }
                "Twitter" -> {
                    val svg = SVG.getFromString(that.icon)
                    val drawable = PictureDrawable(svg.renderToPicture())
                    Glide.with(context).load(drawable).into(holder.imgtwitter)
                    holder.twitter.text = " " + that.follower

                }
                "linkedin"->{
                    if (that.icon!=null) {
                        val svg = SVG.getFromString(that.icon)
                        val drawable = PictureDrawable(svg.renderToPicture())
                        Glide.with(context).load(drawable).into(holder.imglinkedin)
                    }
                    if (that.follower.equals("")||that.follower.equals(null)){
                        holder.layoutLinkedin.visibility=View.GONE
                    }else {
                        holder.layoutLinkedin.visibility=View.VISIBLE
                        holder.linkedin.text = " " + that.follower
                    }

                }
            }
        }
        holder.btn_changeInfluencer.setOnClickListener {
            click.changeInfluencer(list[position].inf_code)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}