package com.locatocam.app.utility

import android.app.Dialog
import android.content.Context
import android.view.View
import com.locatocam.app.R
import pl.droidsonroids.gif.GifImageView

class Loader(var context: Context) {
    companion object{
        lateinit var dialog: Dialog
    }

    fun showLoader() {
        dialog = Dialog(context, R.style.AppTheme_Dialog)
        val view = View.inflate(context, R.layout.progressdialog_item, null)
        dialog.setContentView(view)
        dialog.setCancelable(true)
        val progressbar: GifImageView = dialog.findViewById(R.id.img_loader)!!
        dialog.show()
    }

    fun hideLoader() {
            if (dialog != null) {
                dialog.dismiss()
            }

    }
}