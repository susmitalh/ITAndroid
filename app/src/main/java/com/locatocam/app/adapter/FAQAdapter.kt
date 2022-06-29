package com.locatocam.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.facebook.FacebookSdk.getApplicationContext
import com.locatocam.app.Activity.OnlineOrderingHelpActivity
import com.locatocam.app.ModalClass.FAQData
import com.locatocam.app.R


class FAQAdapter(var context: OnlineOrderingHelpActivity, var dataList: List<FAQData>) :RecyclerView.Adapter<FAQAdapter.ViewHolder>(){
    lateinit var animation: Animation

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view:View = LayoutInflater.from(parent.context).inflate(R.layout.faq_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtQue.text=dataList.get(position).question
        holder.txtAns.text=dataList.get(position).answer

        holder.layoutSHowTxt.setOnClickListener {



            if (holder.layoutHideTxt.visibility.equals(View.VISIBLE)){
                holder.layoutHideTxt.visibility=View.GONE
                animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_to_right)
                animation.setRepeatCount(0);
                animation.fillAfter=true
                holder.imgMore.startAnimation(animation)
            }else{
                holder.layoutHideTxt.visibility=View.VISIBLE
                animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_to_left)
                animation.setRepeatCount(0);
                animation.fillAfter=true
                holder.imgMore.startAnimation(animation)
            }
        }
    }

    override fun getItemCount(): Int {
        return  dataList.size
    }

    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {

     var layoutSHowTxt:LinearLayout=itemView.findViewById(R.id.layoutSHowTxt)
     var imgMore:ImageView=itemView.findViewById(R.id.imgMore)
     var txtQue:TextView=itemView.findViewById(R.id.txtQue)
     var layoutHideTxt:LinearLayout=itemView.findViewById(R.id.layoutHideTxt)
     var txtAns:TextView=itemView.findViewById(R.id.txtAns)

    }
}