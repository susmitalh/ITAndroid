package com.locatocam.app.views.settings.addressBook

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.locatocam.app.R
import com.locatocam.app.data.responses.address.Data
import com.locatocam.app.security.SharedPrefEnc


class MyAddressAdapter(
    private val list: List<Data>,
    private val context: Context,
    var clickEditAddress: ClickEditAddress
) : RecyclerView.Adapter<MyAddressAdapter.viewHolder>(){
    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {

        val name = itemView.findViewById<TextView>(R.id.name)
        val mobileNo = itemView.findViewById<TextView>(R.id.mobileNo)
        val hub = itemView.findViewById<TextView>(R.id.hub)
        val address = itemView.findViewById<TextView>(R.id.address)
        val btn_edit = itemView.findViewById<AppCompatButton>(R.id.btn_edit)
        val btn_remove = itemView.findViewById<AppCompatButton>(R.id.btn_remove)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyAddressAdapter.viewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.row_my_address_layout, parent, false)

        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: MyAddressAdapter.viewHolder, position: Int) {
            holder.name.text= SharedPrefEnc.getPref(context, "name")
            holder.mobileNo.text=SharedPrefEnc.getPref(context, "mobile")
            var location:String=list[position].location!!
            var addressSave:String=list[position].address_save_as!!
            holder.hub.text=location+"("+addressSave+")"
            holder.address.text=list[position].customer_address

        holder.btn_edit.setOnClickListener {
                clickEditAddress.edtAddressSetting(list.get(position))
        }
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