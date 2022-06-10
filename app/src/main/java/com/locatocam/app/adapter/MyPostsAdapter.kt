package com.locatocam.app.adapter

import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.locatocam.app.Activity.PlayPostActivity
import com.locatocam.app.Activity.ViewMyPostActivity
import com.locatocam.app.ModalClass.MyPostData
import com.locatocam.app.ModalClass.MyPostsPost
import com.locatocam.app.R
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.views.home.test.SimpleEvents
import com.squareup.picasso.Picasso

class MyPostsAdapter(
    var activity: ViewMyPostActivity,
    var dataListPosts: ArrayList<MyPostsPost>?,
    var simpleEvents: SimpleEvents,
    var myPostData: MyPostData
) : RecyclerView.Adapter<MyPostsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       var view:View=LayoutInflater.from(parent.context).inflate(R.layout.my_posts_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.progress.visibility=View.VISIBLE

        if (SharedPrefEnc.getPref(activity,"user_id").equals(myPostData.userId)){
            holder.options.visibility=View.VISIBLE
        }


        Glide.with(holder.postImage.context).load(dataListPosts?.get(position)?.screenshot)
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable?>, isFirstResource: Boolean): Boolean {
                    holder.progress.setVisibility(View.GONE)
                    Picasso.with(activity)
                        .load(dataListPosts?.get(position)?.screenshot)
                        .into(holder.postImage)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any,
                    target: Target<Drawable?>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.progress.setVisibility(View.GONE)
                    return false
                }
            })
//            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .placeholder(R.drawable.ic_placeholder)
            .into(holder.postImage)
        holder.postImage.setOnClickListener {
            val intent = Intent(activity, PlayPostActivity::class.java)
            intent.putExtra("influencer_code", myPostData.influencerCode)
            intent.putExtra("post_id", dataListPosts?.get(position)?.postId)
            activity.startActivity(intent)
        }

        holder.viewCount.setText(" "+dataListPosts?.get(position)?.views)

        holder.options.setOnClickListener {
            val popupMenu = PopupMenu(activity, holder.options)

            popupMenu.menuInflater.inflate(R.menu.action_manu_post, popupMenu.menu)

            popupMenu.menu.getItem(0).isVisible = false
            popupMenu.setOnMenuItemClickListener { menuItem -> // Toast message on menu item clicked
                when (menuItem.itemId) {
                    R.id.report -> {
                        Toast.makeText(activity, "report", Toast.LENGTH_SHORT).show()
                    }
                    R.id.trash -> simpleEvents.trash(dataListPosts?.get(position)?.postId!!, position)
                }
                true
            }
            popupMenu.show()
        }


    }

    override fun getItemCount(): Int {
        return dataListPosts!!.size
    }

    class ViewHolder (itemView:View):RecyclerView.ViewHolder(itemView){

        var postImage:ImageView=itemView.findViewById(R.id.post_image)
        var options:ImageView=itemView.findViewById(R.id.options_mypost)
        var viewCount:TextView=itemView.findViewById(R.id.viewCount)
        var progress:ProgressBar=itemView.findViewById(R.id.progressBar)

    }
}