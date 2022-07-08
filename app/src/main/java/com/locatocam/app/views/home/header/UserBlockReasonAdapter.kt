package com.locatocam.app.views.home.header

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.locatocam.app.R
import com.locatocam.app.data.responses.address.Data
import com.locatocam.app.security.SharedPrefEnc


class UserBlockReasonAdapter(private val list: List<com.locatocam.app.data.responses.userblock_reasons.Data>,
    private val context: Context/*, var clickEditAddress: ClickEditAddress*/
) : RecyclerView.Adapter<UserBlockReasonAdapter.viewHolder>(){
    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {

        val radioButton = itemView.findViewById<RadioButton>(R.id.radioButton)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserBlockReasonAdapter.viewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.row_my_address_layout, parent, false)

        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: UserBlockReasonAdapter.viewHolder, position: Int) {
        holder.radioButton.text=list[position].reason
        holder
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