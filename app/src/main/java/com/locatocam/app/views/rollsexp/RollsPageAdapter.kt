package com.locatocam.app.views.rollsexp

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.locatocam.app.data.responses.playrolls.RollsData
import com.locatocam.app.views.PlayPost.UpdateData

class RollsPageAdapter(
    fragment: FragmentActivity,
    val dataList: MutableList<RollsData> = mutableListOf(),
    var updatedata: UpdateData
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun createFragment(position: Int): Fragment {
        return StoryViewFragment.newInstance(dataList[position],updatedata)
    }
}