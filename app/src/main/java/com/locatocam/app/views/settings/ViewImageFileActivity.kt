package com.locatocam.app.views.settings

import android.R.attr
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.locatocam.app.databinding.ActivityViewImageFileBinding
import com.squareup.picasso.Picasso
import java.lang.reflect.Array.get
import android.R.attr.data
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import com.bumptech.glide.request.target.Target
import com.locatocam.app.MyApp.Companion.context
import com.locatocam.app.views.MainActivity


class ViewImageFileActivity : AppCompatActivity() {
    lateinit var binding: ActivityViewImageFileBinding
    var fileUrl: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewImageFileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bundle: Bundle? = intent.extras
        bundle?.let {
            bundle.apply {
                fileUrl = getString("file")
            }

        }
        Glide.with(applicationContext)
            .load(Uri.parse(fileUrl))
            .into(binding.imageView)

        binding.home.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        binding.back.setOnClickListener {
            finish()
        }

    }
}