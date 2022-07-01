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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.facebook.appevents.internal.AppEventUtility.getRootView
import com.locatocam.app.Activity.OnlineOrderingHelpActivity
import com.locatocam.app.R
import com.locatocam.app.data.responses.company.SubItem
import com.locatocam.app.views.login.ActivityLogin
import com.locatocam.app.views.settings.PostReelsApprovalActivity
import com.locatocam.app.views.settings.SettingsActivity
import com.locatocam.app.views.settings.adapters.aboutUs.PrivacyPolicyActivity
import com.locatocam.app.views.settings.adapters.aboutUs.TermsConditionActivity
import com.locatocam.app.views.settings.addressBook.MyAddressActivity
import com.locatocam.app.views.settings.bookmark.BookMarkActivity
import com.locatocam.app.views.settings.favOrders.FavouiteOrdersActivity
import com.locatocam.app.views.settings.influencerDashboard.SettingSubMenuActivity
import com.locatocam.app.views.settings.myPostReelsApprovalPending.MyPostReelsApprovalPendingActivity
import com.locatocam.app.views.settings.viewApprovals.ViewApprovalActivity

class CompanyMenuSubItemAdapter (private val list: List<SubItem>, private val context: Context) : RecyclerView.Adapter<CompanyMenuSubItemAdapter.viewHolder>(){
    private lateinit var companyDetails : com.locatocam.app.data.responses.company.UserDetails
    constructor(list: List<SubItem>,
                context: Context,
                companyDetails : com.locatocam.app.data.responses.company.UserDetails):
            this(list,context){
        this.companyDetails = companyDetails;
    }
    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {

        val title = itemView.findViewById<TextView>(R.id.item_name)
        val countText = itemView.findViewById<TextView>(R.id.countText)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CompanyMenuSubItemAdapter.viewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.settings_menu_item_layout, parent, false)

        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: CompanyMenuSubItemAdapter.viewHolder, position: Int) {
        holder.title.text = list[position].Title
        Log.e("MenuSubItemAdapter",list[position].Count.toString())
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
            if(title.equals("Merchant Dashboard")) {
                val intent = Intent(context, SettingSubMenuActivity::class.java)
                intent.putExtra("type",title)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
            else if(title.equals("Logout")){
                popupLogout(holder.title.getRootView().getContext())
            }
            else if(title.equals("Share Page Link")){
                val message: String = "https://loca-toca.com/Login/index?si="+companyDetails.influencer_code
                val share = Intent(Intent.ACTION_SEND)
                share.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                share.type = "text/plain"
                share.putExtra(Intent.EXTRA_TEXT, message)
                context.applicationContext.startActivity(Intent.createChooser(share, "Share").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
            else if(title.equals("Post Reels Approvals")){
                val intent = Intent(context, PostReelsApprovalActivity::class.java)
                intent.putExtra("userID",companyDetails.user_id)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
            else if(title.equals("My Post or Reels Approval Pending")){
            val intent = Intent(context, MyPostReelsApprovalPendingActivity::class.java)
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
            else if(title.equals("BookMark")){
                var intent=Intent(context, BookMarkActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                context?.startActivity(intent)
            }
            else if(title.equals("Onine Ordering Help")){
                var intent=Intent(context, OnlineOrderingHelpActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                context?.startActivity(intent)
            }
            else if(title.equals("About Us")){
                aboutUs(holder.title.getRootView().getContext())
            }
            else if(title.equals("Privacy Policy")){
                var intent=Intent(context, PrivacyPolicyActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                context?.startActivity(intent)
            }
            else if(title.equals("Terms and Condition")){
                var intent=Intent(context, TermsConditionActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                context?.startActivity(intent)
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
    fun aboutUs(context: Context) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.popup_aboutus)
        dialog.setCanceledOnTouchOutside(false)
        val close = dialog.findViewById<View>(R.id.close) as ImageView
        close.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}

