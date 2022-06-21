package com.locatocam.app.views.settings.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.locatocam.app.R
import com.locatocam.app.data.responses.company.MenuDetail

class CompanyMenuAdapter (private val list: List<MenuDetail>, private val context: Context) : RecyclerView.Adapter<CompanyMenuAdapter.viewHolder>(){
    private lateinit var companyDetails : com.locatocam.app.data.responses.company.UserDetails
    constructor(list: List<MenuDetail>,
                context: Context,
                companyDetails : com.locatocam.app.data.responses.company.UserDetails):
            this(list,context){
        this.companyDetails = companyDetails;
    }
    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {

        val title = itemView.findViewById<TextView>(R.id.menu_title)
        val sublist = itemView.findViewById<RecyclerView>(R.id.menu_items)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CompanyMenuAdapter.viewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.menu_details_layout, parent, false)

        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: CompanyMenuAdapter.viewHolder, position: Int) {


        //Log.e("Sub_items",list[position].Sub_items.toString())
        if (!list[position].Sub_items.isNullOrEmpty()){
            holder.title.text = list[position].Title
            Log.e("MenuAdapter",list[position].Title)
            holder.sublist.layoutManager = LinearLayoutManager(context)
            holder.sublist.itemAnimator = DefaultItemAnimator()
            val menuAdapter =  CompanyMenuSubItemAdapter(list[position].Sub_items,context,companyDetails)
            holder.sublist.adapter = menuAdapter
        }else{
            holder.title.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}