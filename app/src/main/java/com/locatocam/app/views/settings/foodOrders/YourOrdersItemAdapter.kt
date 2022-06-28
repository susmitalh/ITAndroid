package com.locatocam.app.views.settings.foodOrders

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.locatocam.app.R
import com.locatocam.app.data.responses.company.SubItem
import com.locatocam.app.data.responses.yourOrder.Data
import com.locatocam.app.data.responses.yourOrder.Item
import com.locatocam.app.views.login.ActivityLogin
import com.locatocam.app.views.settings.influencerDashboard.InfluencerSOPActivity
import com.locatocam.app.views.settings.influencerDashboard.SettingSubMenuActivity
import com.locatocam.app.views.settings.influencerDashboard.ViewBlockUserActivity
import com.locatocam.app.views.settings.myPostReelsApprovalPending.MyPostReelsApprovalPendingActivity
import com.locatocam.app.views.settings.viewApprovals.ViewApprovalActivity

class YourOrdersItemAdapter (private val list: List<Item>, private val context: Context) : RecyclerView.Adapter<YourOrdersItemAdapter.viewHolder>(){
    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {

        val item = itemView.findViewById<TextView>(R.id.item)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): YourOrdersItemAdapter.viewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.your_orders_item_layout, parent, false)

        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: YourOrdersItemAdapter.viewHolder, position: Int) {
        var itemName:String=list[position].item_name
        var qty:String=list[position].qty.toString()
        var sym:String="X"
        val res=concat(qty,sym,itemName)
        holder.item.text =res
    }

    override fun getItemCount(): Int {
        return list.size
    }
    fun concat(vararg string: String): String {
        val sb = StringBuilder()
        for (s in string) {
            sb.append(s)
        }

        return sb.toString()
    }

}