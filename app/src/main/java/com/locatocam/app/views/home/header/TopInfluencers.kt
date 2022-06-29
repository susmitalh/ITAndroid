package com.locatocam.app.views.home.header

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.locatocam.app.R
import com.locatocam.app.databinding.ActivityTopInfluencersBinding
import com.locatocam.app.repositories.HeaderRepository
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.viewmodels.TopInfViewModel
import pl.droidsonroids.gif.GifImageView

class TopInfluencers : AppCompatActivity(),IHeaderEvents{
    lateinit var binding: ActivityTopInfluencersBinding
    lateinit var viewModel: TopInfViewModel
    lateinit var userid:String
    lateinit var dialog:Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityTopInfluencersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var repository= HeaderRepository("",application)
        var factory= TopInfluencerViewModelFactory(repository)
        viewModel= ViewModelProvider(this,factory).get(TopInfViewModel::class.java)

        userid = SharedPrefEnc.getPref(this.application, "user_id")


        setObservers()
        setOnclickListeners()
    }

    fun setObservers() {
        viewModel.topInfluencer.observe(this,{

            var layoutManager = GridLayoutManager(this, 4)
            binding.recmain.setLayoutManager(layoutManager)

            var adapter = TopIfluencerAdapter(it,this)
            binding.recmain.setAdapter(adapter)
        })
        viewModel.getTopInfluencersV(userid,"all",this)

        binding.search.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                viewModel.performSearch(binding.search.text.toString())
            }
        })

    }

    fun setOnclickListeners(){
        binding.back.setOnClickListener {
            finish()
        }
        binding.home.setOnClickListener {
            finish()
        }
    }

    override fun onItemClick(user_id: String, inf_code: String) {
        var intent=Intent()
        intent.putExtra("user_id",user_id)
        intent.putExtra("inf_code",inf_code)
        this.setResult(RESULT_OK,intent)
        finish()
    }

    override fun onItemMostPopularVideos(user_id: String, inf_code: String) {
    }

    override fun onItemRollsAndShortVideos(firstid: String) {

    }

    override fun onBrandSearchClick(searchId: String?, userId: String?) {

    }

    fun showLoader() {
        dialog = Dialog(this, R.style.AppTheme_Dialog)
        val view = View.inflate(this, R.layout.progressdialog_item, null)
        dialog?.setContentView(view)
        dialog?.setCancelable(true)
        val progressbar: GifImageView = dialog?.findViewById(R.id.img_loader)!!
        dialog?.show()
    }

    fun hideLoader() {
        if (dialog != null) {
            dialog?.dismiss()
        }
    }


}