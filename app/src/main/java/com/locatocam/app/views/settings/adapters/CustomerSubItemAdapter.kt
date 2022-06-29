package com.locatocam.app.views.settings.adapters

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
import com.locatocam.app.data.responses.customer_model.SubItem
import com.locatocam.app.views.login.ActivityLogin
import com.locatocam.app.views.settings.addressBook.MyAddressActivity
import com.locatocam.app.views.settings.favOrders.FavouiteOrdersActivity
import com.locatocam.app.views.settings.influencerDashboard.ViewBlockUserActivity
import com.locatocam.app.views.settings.myPostReelsApprovalPending.MyPostReelsApprovalPendingActivity
import com.locatocam.app.views.settings.viewApprovals.ViewApprovalActivity

class CustomerSubItemAdapter (private val list: List<SubItem>, private val context: Context) : RecyclerView.Adapter<CustomerSubItemAdapter.viewHolder>(){
    private lateinit var customerDetails : com.locatocam.app.data.responses.customer_model.UserDetails;
    constructor(list: List<SubItem>,
                context: Context,
                customerDetails : com.locatocam.app.data.responses.customer_model.UserDetails):
            this(list,context){
        this.customerDetails = customerDetails;
    }
    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {

        val title = itemView.findViewById<TextView>(R.id.item_name)
        val countText = itemView.findViewById<TextView>(R.id.countText)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CustomerSubItemAdapter.viewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.settings_menu_item_layout, parent, false)

        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomerSubItemAdapter.viewHolder, position: Int) {
        holder.title.text = list[position].Title
        Log.e("MenuSubItemAdapter",list[position].Count.toString())
        /*if(list[position].Countable){
            holder.countText.text = "("+list[position].Count+")"
        }else{
            holder.countText.visibility = View.GONE
        }*/
        /*if(list[position].Countable){
            holder.countText.text = "("+list[position].Count+")"
            if(list[position].Count > 0)
            holder.title.setTextColor(ContextCompat.getColor(context,R.color.red))
            holder.countText.setTextColor(ContextCompat.getColor(context,R.color.red))
        }else{
            holder.countText.visibility = View.GONE
        }*/
        if(list[position].Countable){
            holder.countText.text = "("+list[position].Count+")"
            if(list[position].Count == 0){
                holder.countText.visibility = View.GONE
            }
            else if(list[position].Count > 0)
                holder.title.setTextColor(ContextCompat.getColor(context,R.color.red))
            holder.countText.setTextColor(ContextCompat.getColor(context,R.color.red))
            Log.e("Countable",list[position].Count.toString())
        }
        holder.title.setOnClickListener {
            val title =list[position].Title
            if(title.equals("Logout")){
                popupLogout(holder.title.getRootView().getContext())
            }
            else if(title.equals("Share Page Link")){
                val message: String = "https://loca-toca.com/Login/index?si="+customerDetails.influencer_code
                val share = Intent(Intent.ACTION_SEND)
                share.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                share.type = "text/plain"
                share.putExtra(Intent.EXTRA_TEXT, message)
                context.applicationContext.startActivity(Intent.createChooser(share, "Share").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
            else if(title.equals("My Post Reels Approval Pending")){
                val intent = Intent(context, MyPostReelsApprovalPendingActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
            if(title.equals("View Blocked User")){
                val intent = Intent(context, ViewBlockUserActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
            else if(title.equals("Favourite Orders")){
                val intent = Intent(context, FavouiteOrdersActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
            else if(title.equals("Address Book")){
                val intent = Intent(context, MyAddressActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
        }
        Log.e("MenuSubItemAdapter",list[position].Title)
    }

    override fun getItemCount(): Int {
        return list.size
    }
    fun popupLogout(context: Context) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.popup_logout)
        dialog.setCanceledOnTouchOutside(false)
        val yes = dialog.findViewById<View>(R.id.yes) as Button
        val no = dialog.findViewById<View>(R.id.no) as Button
        no.setOnClickListener { dialog.dismiss() }
        yes.setOnClickListener {
            Toast.makeText(context, "Successfully Logout", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, ActivityLogin::class.java)
            val sharedPreferences: SharedPreferences =
                context.getSharedPreferences("userinfo", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
            dialog.dismiss()
        }
        dialog.show()
    }
}