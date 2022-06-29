package com.locatocam.app.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.locatocam.app.Activity.OtherProfileWithFeedActivity
import com.locatocam.app.R
import com.locatocam.app.data.responses.SearchModal.DataSeach
import com.locatocam.app.views.home.header.IHeaderEvents
import de.hdodenhof.circleimageview.CircleImageView

class SearchAdapter(var dataList: ArrayList<DataSeach>?, private val iHeaderEvents: IHeaderEvents) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    lateinit var context: Context
//    var dataList: ArrayList<DataSeach> = ArrayList()

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        var searchImg: CircleImageView = itemView.findViewById(R.id.search_img)
        var searchName: TextView = itemView.findViewById(R.id.search_name)
        var searchBrand: TextView = itemView.findViewById(R.id.search_brand)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        var view =
            LayoutInflater.from(context).inflate(R.layout.search_brand_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(dataList?.get(position)?.logo).into(holder.searchImg)
        holder.searchBrand.setText(dataList?.get(position)?.userType)
        holder.searchName.setText(dataList?.get(position)?.name)
        holder.itemView.setOnClickListener {


            if (dataList?.get(position)?.userType.equals("Brand")) {
                iHeaderEvents.onBrandSearchClick(
                    dataList?.get(position)?.userType,
                    dataList?.get(position)?.userId
                )
            } else {
                val intent = Intent(context, OtherProfileWithFeedActivity::class.java)
                intent.putExtra("user_id", dataList?.get(position)?.userId)
                intent.putExtra("inf_code", dataList?.get(position)?.influencerCode)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList!!.size
    }

    fun updateList(dataList: ArrayList<DataSeach>?) {

        this.dataList = dataList
        notifyDataSetChanged()
    }
}