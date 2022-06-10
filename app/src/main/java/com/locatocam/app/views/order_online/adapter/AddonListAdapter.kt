package com.locatocam.app.views.order_online.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.Resource
import com.locatocam.app.R
import com.locatocam.app.data.responses.resp_products_new.Data
import com.locatocam.app.data.responses.resp_products_new.Item
import com.locatocam.app.data.responses.resp_products_new.Menu
import com.locatocam.app.db.entity.Addon
import com.locatocam.app.db.entity.Varient
import com.locatocam.app.views.order_online.AddonItemClick
import com.locatocam.app.views.order_online.ProductItemClick
import com.locatocam.app.views.order_online.VarientItemClick
import com.locatocam.app.views.order_online.fragments.SingleClickEvent
import de.hdodenhof.circleimageview.CircleImageView


class AddonListAdapter(private val varient:List<Addon>, private val clickEvents: AddonItemClick):
    RecyclerView.Adapter<AddonListAdapter.TopInfluencerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopInfluencerViewHolder {
        var view:View = LayoutInflater.from(parent.context).inflate(R.layout.row_layout_item_varient, parent, false)
        return TopInfluencerViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopInfluencerViewHolder, position: Int) {
        val item = varient[position]

        item.apply {
            holder.itemName.text = item_name
            holder.itemPrice.text = "₹ ${rate}"
            holder.total_amt.text = "₹ ${rate*quantity}"
            holder.itemcount.text = "₹ ${quantity}"
        }

        holder.increese.setOnClickListener {
            clickEvents.increeseItem(item)
        }
        holder.decreese.setOnClickListener {
            if ( item.quantity>0){
                clickEvents.decreeseItem(item)
            }
        }

    }


    override fun getItemCount(): Int {
        return varient.size
    }

    class TopInfluencerViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        var itemPrice:TextView
        var itemName:TextView
        var total_amt:TextView

        var decreese:Button
        var increese:Button
        var itemcount:TextView

        init {
            itemPrice= itemView.findViewById(R.id.itemPrice)
            itemName= itemView.findViewById(R.id.itemName)
            total_amt= itemView.findViewById(R.id.total_amt)

            decreese= itemView.findViewById(R.id.decreese)
            increese= itemView.findViewById(R.id.increese)
            itemcount= itemView.findViewById(R.id.itemcount)
        }

    }
}