package com.locatocam.app.adapter

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.locatocam.app.ModalClass.PlayPostData
import com.locatocam.app.views.PlayPost.PlayPostFragment
import com.locatocam.app.views.PlayPost.UpdateData

class PlayPostAdapter (fragment: FragmentActivity, val dataList: MutableList<PlayPostData> = mutableListOf(),var updatedata:UpdateData) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun createFragment(position: Int): Fragment {
        Log.e("TAG", "createFragment: " )
        return PlayPostFragment.newInstance(dataList[position],updatedata)
        notifyDataSetChanged()
    }

}