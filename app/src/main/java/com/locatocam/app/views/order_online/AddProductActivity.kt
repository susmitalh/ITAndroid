package com.locatocam.app.views.order_online

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.locatocam.app.data.responses.resp_products_new.Item
import com.locatocam.app.databinding.ActivityAddProductBinding
import com.locatocam.app.network.Status
import com.locatocam.app.repositories.AddProductRepository
import com.locatocam.app.viewmodels.AddProductViewModel
import com.locatocam.app.views.order_online.adapter.ItemListingAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import com.locatocam.app.MyApp.Companion.context
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.views.order_online.fragments.AddonVarientFragment
import kotlinx.coroutines.runBlocking


class AddProductActivity : OrderOnlineBaseActivity(),ProductItemClick {
    lateinit var binding:ActivityAddProductBinding
    lateinit var viewmodel:AddProductViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAddProductBinding.inflate(layoutInflater,baseBinding.container,true)
        var repository= AddProductRepository(application)
        var factory= AddProductViewModelFactory(repository)
        viewmodel= ViewModelProvider(this,factory).get(AddProductViewModel::class.java)
        val storeID = intent.extras!!.getString("store_id")
        if (storeID != null) {
            setObservers(storeID)
        }
        setOnClickListeners()
        //setContentView(binding.root)
    }

    fun setObservers(storeID : String) {
        var layoutManager = LinearLayoutManager(this)
        binding.recyc.setLayoutManager(layoutManager)

        lifecycleScope.launch {
            viewmodel.getProducts(storeID,SharedPrefEnc.getPref(context,"selected_lat"), SharedPrefEnc.getPref(context,"selected_lng"))
            viewmodel.items.collect {
                when (it.status) {
                    Status.SUCCESS -> {
                        Glide.with(this@AddProductActivity)
                            .load(it.data?.brand_details?.banner?.image)
                            .into(binding.thumbnile)

                        var adapter = it.data?.let { it1 ->
                            ItemListingAdapter(it1.menu, this@AddProductActivity)

                        }



                        binding.recyc.setAdapter(adapter)
                        Log.i("ki999","Succes")

                    }
                    Status.LOADING -> {
                        Log.i("ki999","Loading")
                    }
                    Status.ERROR -> {
                        Log.e("addProducts",it.message.toString())

                        Toast.makeText(
                            this@AddProductActivity,
                            it.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewmodel.cart.collect {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.let { cartList ->
                            baseBinding.items.text = cartList.sumOf { it.quantity }.toString()+" Items"
                            baseBinding.total.text=  "₹ "+cartList.sumOf { (it.quantity*it.rate)-it.discount/100*(it.quantity*it.rate) }.toString()

                            var ttl=cartList.sumOf { it.discount/100*(it.quantity*it.rate) }
                            if (ttl>0){
                                baseBinding.strikeTotal.text=  "₹ "+cartList.sumOf { it.quantity*it.rate }.toString()
                                baseBinding.strikeTotal.setPaintFlags(binding.strikeTotal.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
                            }else{
                                baseBinding.strikeTotal.visibility=View.GONE
                            }

                        }

                        Log.e("cartData", it.data.toString())
                    }
                    Status.LOADING -> {
                        Log.i("ki9rfvv99", "lading")

                    }
                    Status.ERROR -> {
                        Log.e("ki9rfvv99", it.message.toString())
                        Toast.makeText(
                            this@AddProductActivity,
                            it.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }


        viewmodel.varientAddedPosition.observe(this, Observer {
            binding.recyc.adapter?.notifyItemChanged(it)
        })
    }

    fun setOnClickListeners(){
        binding.menu.setOnClickListener {
            if (binding.menuContainer.visibility==View.VISIBLE){
                binding.menuContainer.visibility=View.GONE
            }else{
                binding.menuContainer.visibility=View.VISIBLE
                showPopupWindow()
            }
        }

        binding.addonVarientContainer.setOnClickListener {
            if (binding.addonVarientContainer.visibility==View.VISIBLE){
                binding.addonVarientContainer.visibility=View.GONE
                binding.menu.visibility=View.VISIBLE
            }else{
                binding.addonVarientContainer.visibility=View.VISIBLE
            }
        }

        binding.menuContainer.setOnClickListener {
            binding.menuContainer.visibility=View.GONE
        }
    }
    override fun addItem(item: Item) {
        viewmodel.addItem(item)
    }

    override fun increeseItem(item: Item) {
        viewmodel.updateItem(item)
    }

    override fun decreeseItem(item: Item) {
        if (item.quantity>0){
            viewmodel.updateItem(item)
        }else{
            viewmodel.deleteItem(item)
        }
    }

    override fun addItemCustomizable(item: Item) {
        binding.menu.visibility=View.GONE
        binding.addonVarientContainer.visibility=View.VISIBLE
        var fragmemt=AddonVarientFragment(item)
        val fm =supportFragmentManager
        val ft = fm.beginTransaction()
        ft.replace(binding.addonVarientContentLayout.id, fragmemt)
        ft.commit()
    }

    private fun showPopupWindow() {
        lifecycleScope.launch {
            viewmodel.categories.collect {
                when (it.status) {
                    Status.SUCCESS -> {
                        binding.contentLayout.removeAllViews()
                        it.data?.let { categories->
                            var i=0
                            categories.forEach { cat->
                                runBlocking {
                                    var tv=TextView(this@AddProductActivity)
                                    tv.text=cat
                                    tv.setTextColor(Color.BLACK)
                                    val params: LinearLayout.LayoutParams =
                                        LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                        )
                                    params.setMargins(0, 15, 0, 0)
                                    tv.setLayoutParams(params)
                                    tv.textSize=12.0f
                                    tv.setOnClickListener {
                                        var y = binding.recyc.getChildAt(i).getY();
                                        binding.scrlmain.post(Runnable {
                                            binding.scrlmain.fling(0)
                                            binding.scrlmain.smoothScrollTo(0, y.toInt())
                                        })
                                    }
                                    Log.i("rt444",cat)
                                    binding.contentLayout.addView(tv)
                                    i++
                                }

                            }
                        }

                        Log.i("ki9rfvv99", "data")
                    }
                    Status.LOADING -> {
                        Log.i("ki9rfvv99", "lading")

                    }
                    Status.ERROR -> {
                        Log.e("ki9rfvv99", it.message.toString())
                        Toast.makeText(
                            this@AddProductActivity,
                            it.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }


}