package com.locatocam.app.views.settings.foodOrders

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.locatocam.app.R
import com.locatocam.app.data.responses.yourOrder.Data
import com.locatocam.app.data.responses.yourOrder.Item

class YourOrdersAdapter (private val list: List<Data>, private val context: Context) : RecyclerView.Adapter<YourOrdersAdapter.viewHolder>(){
    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {

        val txtOrderId = itemView.findViewById<TextView>(R.id.txtOrderId)
        val brandName = itemView.findViewById<TextView>(R.id.brandName)
        val address = itemView.findViewById<TextView>(R.id.address)
        val help = itemView.findViewById<TextView>(R.id.help)
        val amount = itemView.findViewById<TextView>(R.id.amount)
        val status = itemView.findViewById<TextView>(R.id.status)
        val repatOrder = itemView.findViewById<TextView>(R.id.repatOrder)

        val items = itemView.findViewById<RecyclerView>(R.id.items)
        val imageView=itemView.findViewById<ImageView>(R.id.imageView)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): YourOrdersAdapter.viewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.your_orders_layout, parent, false)

        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: YourOrdersAdapter.viewHolder, position: Int) {
            holder.txtOrderId.text = "Order ID: "+list[position].order_id
            holder.brandName.text=list[position].brand_name
            holder.address.text=list[position].location
            holder.amount.text=": "+list[position].amount
            holder.status.text=list[position].status
            Glide.with(holder.itemView.context)
                .load(list[position].logo)
                .centerCrop()
                .into(holder.imageView)


            holder.items.layoutManager = LinearLayoutManager(context)
            holder.items.itemAnimator = DefaultItemAnimator()
            val orderItemAdapter =  YourOrdersItemAdapter(list[position].items,context)
            holder.items.adapter = orderItemAdapter


    }

    override fun getItemCount(): Int {
        return list.size
    }
}