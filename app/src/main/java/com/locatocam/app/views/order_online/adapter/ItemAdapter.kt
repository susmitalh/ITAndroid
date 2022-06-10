package com.locatocam.app.views.order_online.adapter

import android.graphics.Color
import android.util.Log
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
import com.locatocam.app.views.order_online.ProductItemClick
import com.locatocam.app.views.order_online.fragments.SingleClickEvent
import de.hdodenhof.circleimageview.CircleImageView


class ItemAdapter(private val items:List<Item>, private val clickEvents: ProductItemClick):
    RecyclerView.Adapter<ItemAdapter.TopInfluencerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopInfluencerViewHolder {
        var view:View = LayoutInflater.from(parent.context).inflate(R.layout.row_layout_item_product, parent, false)
        return TopInfluencerViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopInfluencerViewHolder, position: Int) {
        val item = items[position]

        item.apply {
            holder.itemName.text = name
            holder.itemPrice.text = "₹ ${price}"
            holder.cuisine.text = item_tags
            Glide.with(holder.image.context)
                .load(image_url)
                .into(holder.image)
            holder.itemcount.text=quantity.toString()
            if (item.quantity>0){
                holder.add.visibility=View.GONE
                holder.add_remove_container.visibility=View.VISIBLE
            }else{
                holder.add.visibility=View.VISIBLE
                holder.add_remove_container.visibility=View.GONE
            }

            if (item.variants!=null || item.addons.size>0){
                holder.customizabletag.visibility=View.VISIBLE
            }else{
                holder.customizabletag.visibility=View.GONE
            }
        }

        holder.add.setOnClickListener {
            Log.e("addw",item.name)

            if ( item.variants!=null || item.addons.size>0){
//                Log.e("variants available",item.name)

                clickEvents.addItemCustomizable(item)
            }else{
                clickEvents.addItem(item.apply { quantity++ })
                holder.itemcount.text=item.quantity.toString()
                holder.add.visibility=View.GONE
                holder.add_remove_container.visibility=View.VISIBLE
            }

        }
        holder.increese.setOnClickListener {
            if ( item.variants!=null || item.addons.size>0){
                Log.e("varients",item.name)
//                clickEvents.addItemCustomizable(item)
            }else{
                item.quantity ++
                clickEvents.increeseItem(item)
                holder.itemcount.text=item.quantity.toString()
            }

        }
        holder.decreese.setOnClickListener {
            if ( item.variants!=null || item.addons.size>0){
                clickEvents.addItemCustomizable(item)
            }else {
                clickEvents.decreeseItem(item.apply { quantity-- })
                if (item.quantity <= 0) {
                    holder.add.visibility = View.VISIBLE
                    holder.add_remove_container.visibility = View.GONE
                } else {
                    holder.add.visibility = View.GONE
                    holder.add_remove_container.visibility = View.VISIBLE
                }
                holder.itemcount.text = item.quantity.toString()
            }
        }

       when(item.dish_type) {
           "veg"->{
               holder.veg_non_icon.setImageResource(R.drawable.ic_veg_icon)
               holder.itemName.setTextColor(Color.parseColor("#AC0000"))
           }
           "egg"->{
               holder.veg_non_icon.setImageResource(R.drawable.ic_egg_icon)
               holder.itemName.setTextColor(Color.parseColor("#FFC107"))
           }
           else->{
               holder.veg_non_icon.setImageResource(R.drawable.ic_non_veg_icon)
               holder.itemName.setTextColor(Color.parseColor("#000000"))
           }
        }


    }


    override fun getItemCount(): Int {
        return items.size
    }

    class TopInfluencerViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        var itemPrice:TextView
        var itemName:TextView
        var cuisine:TextView
        var add:TextView
        var add_remove_container:LinearLayout
        var veg_non_icon:ImageView
        var image:ImageView

        var decreese:Button
        var increese:Button
        var itemcount:TextView
        var customizabletag:TextView

        init {
            itemPrice= itemView.findViewById(R.id.itemPrice)
            itemName= itemView.findViewById(R.id.itemName)
            cuisine= itemView.findViewById(R.id.cuisine)
            add= itemView.findViewById(R.id.add)
            customizabletag= itemView.findViewById(R.id.customizabletag)

            veg_non_icon= itemView.findViewById(R.id.veg_non_icon)
            image= itemView.findViewById(R.id.image)
            add_remove_container= itemView.findViewById(R.id.add_remove_container)
            decreese= itemView.findViewById(R.id.decreese)
            increese= itemView.findViewById(R.id.increese)
            itemcount= itemView.findViewById(R.id.itemcount)
        }

    }
}