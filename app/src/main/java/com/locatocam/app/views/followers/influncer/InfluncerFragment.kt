package com.locatocam.app.views.followers.influencer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.locatocam.app.R

class InfluncerFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Toast.makeText(requireActivity(), "Infuncer", Toast.LENGTH_SHORT).show()

        return inflater.inflate(R.layout.fragment_infuncer, container, false)
    }


}