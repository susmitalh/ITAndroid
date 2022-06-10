package com.locatocam.app.views.order_online.fragments.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.locatocam.app.R
import com.locatocam.app.data.responses.popular_brands.Data
import com.locatocam.app.views.order_online.fragments.ScreenSlidePageFragment
import com.locatocam.app.views.order_online.fragments.SingleClickEvent
import de.hdodenhof.circleimageview.CircleImageView


class PopularBrandsAdapter(private val items:List<Data>, private val clickEvents: SingleClickEvent):
    RecyclerView.Adapter<PopularBrandsAdapter.TopInfluencerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopInfluencerViewHolder {
        var view:View = LayoutInflater.from(parent.context).inflate(R.layout.row_layout_viewpager, parent, false)
        return TopInfluencerViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopInfluencerViewHolder, position: Int) {
        val item = items[position]

        item.apply {
           /* holder.name.text = name
            Glide.with(holder.image.context)
                .load(image_url)
                .into(holder.image)*/
            val pagerAdapter = ScreenSlidePagerAdapter(holder.pager.context as AppCompatActivity,this)
            holder.pager.adapter = pagerAdapter
        }


        /*holder.name.setOnClickListener {
            clickEvents.onclickItem(item.id)
        }*/

    }


    override fun getItemCount(): Int {
        return items.size
    }

    class TopInfluencerViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        var pager:ViewPager2
        init {
            pager= itemView.findViewById(R.id.pager)
        }

    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity,val datax:Data) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 1

        override fun createFragment(position: Int): Fragment = ScreenSlidePageFragment(datax)
    }
}