package com.locatocam.app.views.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.locatocam.app.R
import com.locatocam.app.data.responses.FeedItem
import com.bumptech.glide.RequestManager

import android.widget.ProgressBar

class FeedsAdapter(private val mList: MutableList<FeedItem>,private val feedEvents: FeedEvents,var reqManager: RequestManager) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_HEADER = 0
    private val TYPE_ITEM = 1
    private val TYPE_IMAGE = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_ITEM) {
            //inflate your layout and pass it to view holder
            var view:View =LayoutInflater.from(parent.context).inflate(R.layout.row_layout_feed, parent, false)
            return VideoPlayerViewHolder(view,reqManager)

        } else if (viewType == TYPE_HEADER) {
            //inflate your layout and pass it to view holder
            var view:View =LayoutInflater.from(parent.context).inflate(R.layout.home_page_header_layout, parent, false)
            return VHHeader(view)

        }
        throw  RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");

    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is VideoPlayerViewHolder) {
            val ItemsViewModel = mList[position]
            holder.title.text = ItemsViewModel.name

            holder.parent.setTag(holder);
            this.reqManager
                .load(ItemsViewModel.thumbnile)
                .into(holder.thumbnail);
            //cast holder to VHItem and set data
        } else if (holder is VHHeader) {
            //cast holder to VHHeader and set data for header.
        }


    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    override fun getItemViewType(position: Int): Int {


        return if (isPositionHeader(position)) TYPE_HEADER else TYPE_ITEM
    }

    private fun isPositionHeader(position: Int): Boolean {
        return position == 0
    }

    // Holds the views for adding it to image and text
    public class VideoPlayerViewHolder(ItemView: View,var reqManager: RequestManager) : RecyclerView.ViewHolder(ItemView) {
        lateinit var media_container: FrameLayout
        lateinit var title: TextView
        lateinit var thumbnail:ImageView
        lateinit var volumeControl:ImageView
        lateinit var progressBar: ProgressBar
        lateinit var parent: View
        lateinit var requestManager:RequestManager

        init {
             media_container= itemView.findViewById(R.id.media_container)
             title = itemView.findViewById(R.id.title)
             thumbnail=  itemView.findViewById(R.id.thumbnail)
             volumeControl=  itemView.findViewById(R.id.volume_control)
             progressBar = itemView.findViewById(R.id.progressBar)
             parent =  itemView.findViewById(R.id.parent)
            requestManager=reqManager
        }

    }

    internal class VHHeader(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

    }
}