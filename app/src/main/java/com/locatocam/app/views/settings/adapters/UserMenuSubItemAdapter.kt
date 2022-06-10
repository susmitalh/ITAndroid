package com.locatocam.app.views.settings.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.locatocam.app.R
import com.locatocam.app.data.responses.company.SubItem

class UserMenuSubItemAdapter (private val list: List<com.locatocam.app.data.responses.user_model.SubItem>, private val context: Context) : RecyclerView.Adapter<UserMenuSubItemAdapter.viewHolder>(){
    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {

        val title = itemView.findViewById<TextView>(R.id.item_name)
        val countText = itemView.findViewById<TextView>(R.id.countText)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserMenuSubItemAdapter.viewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.settings_menu_item_layout, parent, false)

        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: UserMenuSubItemAdapter.viewHolder, position: Int) {
        holder.title.text = list[position].Title
        Log.e("MenuSubItemAdapter",list[position].Count.toString())

        if(list[position].Countable){
            holder.countText.text = "("+list[position].Count+")"
            Log.e("Countable",list[position].Count.toString())

        }else{
            holder.countText.visibility = View.GONE
        }
        Log.e("MenuSubItemAdapter",list[position].Title)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}