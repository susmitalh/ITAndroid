package com.locatocam.app.views.settings

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.MediaController
import android.widget.VideoView
import com.locatocam.app.R
import com.locatocam.app.databinding.ActivityViewFileBinding
import com.locatocam.app.views.MainActivity

class ViewFileActivity : AppCompatActivity() {
    lateinit var binding: ActivityViewFileBinding
    var fileUrl: String?=null
    // declaring a null variable for MediaController
    var mediaControls: MediaController? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityViewFileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bundle: Bundle? = intent.extras
        bundle?.let {
            bundle.apply {
                fileUrl = getString("file")
            }

        }

        val mediaController = MediaController(this)
        mediaController.setAnchorView(binding.videoView)
        //specify the location of media file
        val uri = Uri.parse(fileUrl)
        //Setting MediaController and URI, then starting the videoView
        binding.videoView.setMediaController(mediaController)
        binding.videoView.setVideoURI(uri)
        binding.videoView.requestFocus()
        binding.videoView.start()

        binding.home.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        binding.back.setOnClickListener {
            finish()
        }

    }
}