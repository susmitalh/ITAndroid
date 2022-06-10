package com.locatocam.app.views.comments

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.locatocam.app.databinding.ActivityCommentsBinding
import com.locatocam.app.repositories.CommentsRepository
import com.locatocam.app.viewmodels.CommentsViewModel

class CommentsActivity : AppCompatActivity() {
    companion object {
        var commentNo: Boolean = false;
        lateinit var activity: CommentsActivity;
    }

    lateinit var binding: ActivityCommentsBinding
    lateinit var viewmodel: CommentsViewModel
    var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this;
        position = intent.getIntExtra("position", 0)
        var repository = CommentsRepository(application)
        var factory = CommentsViewModelFactory(repository)

        viewmodel = ViewModelProvider(this, factory).get(CommentsViewModel::class.java)

        viewmodel.post_id = intent.getStringExtra("postid").toString()
        viewmodel.userId = intent.getStringExtra("userid").toString()
        viewmodel.commentType = intent.getStringExtra("commentType").toString()
        setObserverts()
        setOnClickListeners()
    }

    fun setObserverts() {
        viewmodel.comments.observe(this, { comments ->
            var layoutManager = LinearLayoutManager(this)
            binding.commentslist.setLayoutManager(layoutManager)

            var adapter = CommentsAdapter(comments)
            binding.commentslist.setAdapter(adapter)
        })
        viewmodel.getComments()

        viewmodel.save_status.observe(this, {
            if (it) {
                viewmodel.getComments()
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun setOnClickListeners() {
        binding.back.setOnClickListener {
            finish()
        }

        binding.comment.setOnClickListener {
            if (!binding.comment.text.toString().equals("")) {
                viewmodel.addComments(binding.message.text.toString(), position)
                binding.message.setText("")
            }
        }
    }
}