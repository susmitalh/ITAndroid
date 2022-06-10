package com.locatocam.app.views.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.locatocam.app.R
import com.locatocam.app.data.responses.top_influencers.Data
import de.hdodenhof.circleimageview.CircleImageView

class AutoCompleteAdapter(private val items:List<Locationitem>, private val clickEvents: ClickEvents):
    RecyclerView.Adapter<AutoCompleteAdapter.TopInfluencerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopInfluencerViewHolder {
        var view:View = LayoutInflater.from(parent.context).inflate(R.layout.row_layout_autocomplete, parent, false)
        return TopInfluencerViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopInfluencerViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.name

        holder.name.setOnClickListener {
            clickEvents.onClickLocationItem(item)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class TopInfluencerViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        var name:TextView
        init {
            name= itemView.findViewById(R.id.autocomplete_text)

        }

    }
}