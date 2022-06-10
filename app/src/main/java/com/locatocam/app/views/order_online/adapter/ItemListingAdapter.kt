package com.locatocam.app.views.order_online.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.locatocam.app.R
import com.locatocam.app.data.responses.resp_products_new.Menu
import com.locatocam.app.views.order_online.ProductItemClick


class ItemListingAdapter(private val items:List<Menu>, private val clickEvents: ProductItemClick):
    RecyclerView.Adapter<ItemListingAdapter.TopInfluencerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopInfluencerViewHolder {
        var view:View = LayoutInflater.from(parent.context).inflate(R.layout.row_layout_product_header, parent, false)
        return TopInfluencerViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopInfluencerViewHolder, position: Int) {
        val item = items[position]

        item.apply {
            holder.category_name.text = category_name
            holder.itemCount.text = "${items.size} Items"
        }

        var layoutManager=LinearLayoutManager(holder.contentarea.context)
        holder.contentarea.layoutManager=layoutManager
        holder.contentarea.adapter=ItemAdapter(item.items,clickEvents)

        holder.root.setOnClickListener {
            holder.contentarea.let {
                if(it.visibility==View.VISIBLE){
                    it.visibility=View.GONE
                    holder.arro_image.setImageResource(R.drawable.ic_baseline_keyboard_arrow_right_24)
                    holder.itemCount.visibility=View.VISIBLE
                }else{
                    it.visibility=View.VISIBLE
                    holder.arro_image.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
                    holder.itemCount.visibility=View.GONE
                }
            }
        }

    }


    fun getAll()=items

    override fun getItemCount(): Int {
        return items.size
    }

    class TopInfluencerViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        var itemCount:TextView
        var category_name:TextView
        var image:ImageView
        var arro_image:ImageView
        var contentarea:RecyclerView
        var root:RelativeLayout
        init {
            itemCount= itemView.findViewById(R.id.itemCount)
            category_name= itemView.findViewById(R.id.category_name)
            image= itemView.findViewById(R.id.arro_image)
            contentarea= itemView.findViewById(R.id.contentarea)
            root= itemView.findViewById(R.id.root)
            arro_image= itemView.findViewById(R.id.arro_image)
        }

    }
}