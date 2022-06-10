package com.locatocam.app.views.settings.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.locatocam.app.R
import com.locatocam.app.data.responses.user_model.Document
import com.locatocam.app.viewmodels.SettingsViewModel

class DocumentAdapter(
    private val list: List<Document>,
    private val context: Context,
    val viewmodel: SettingsViewModel
) : RecyclerView.Adapter<DocumentAdapter.viewHolder>(){
    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {

        val docName = itemView.findViewById<TextView>(R.id.docName)
        val docImage = itemView.findViewById<ImageView>(R.id.docImage)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DocumentAdapter.viewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.document_layout, parent, false)

        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: DocumentAdapter.viewHolder, position: Int) {


        //Log.e("Sub_items",list[position].Sub_items.toString())
        holder.docName.text = list[position].doc_name
        holder.docImage.setOnClickListener {

            viewmodel.launchDocPickerForResult(list[position],holder.docImage)

        }
        if (!list[position].doc_location.isNullOrEmpty()){

            Glide.with(context)
                .load(Uri.parse(list[position].doc_location))
                .into(holder.docImage)

        }else{

            Glide.with(context)
                .load(R.mipmap.no_image)
                .into(holder.docImage)

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}