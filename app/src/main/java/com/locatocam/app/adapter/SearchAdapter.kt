package com.locatocam.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.locatocam.app.R
import com.locatocam.app.data.responses.SearchModal.DataSeach
import com.locatocam.app.data.responses.SearchModal.RespSearch
import de.hdodenhof.circleimageview.CircleImageView

class SearchAdapter(var dataList: ArrayList<DataSeach>?) :RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    lateinit var context: Context
//    var dataList: ArrayList<DataSeach> = ArrayList()

    class ViewHolder(ItemView: View):RecyclerView.ViewHolder(ItemView) {
        var searchImg: CircleImageView =itemView.findViewById(R.id.search_img )
        var searchName: TextView =itemView.findViewById(R.id.search_name )
        var searchBrand: TextView =itemView.findViewById(R.id.search_brand )


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context=parent.context
        var view= LayoutInflater.from(context).inflate(R.layout.search_brand_list_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(dataList?.get(position)?.logo).into(holder.searchImg)
        holder.searchBrand.setText(dataList?.get(position)?.userType)
        holder.searchName.setText(dataList?.get(position)?.name)
    }

    override fun getItemCount(): Int {
        return dataList!!.size
    }

    fun updateList(dataList: ArrayList<DataSeach>?) {

        this.dataList=dataList
        notifyDataSetChanged()
    }
}