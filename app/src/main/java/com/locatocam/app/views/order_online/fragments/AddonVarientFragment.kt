package com.locatocam.app.views.order_online.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.locatocam.app.R
import com.locatocam.app.data.responses.resp_products_new.Item
import com.locatocam.app.data.responses.resp_products_new.ItemX
import com.locatocam.app.data.responses.resp_products_new.ItemXX
import com.locatocam.app.data.responses.resp_products_new.Varients
import com.locatocam.app.databinding.FragmentAddonVarientBinding
import com.locatocam.app.databinding.FragmentTopBrandsBinding
import com.locatocam.app.databinding.FragmentTopPicsBinding
import com.locatocam.app.db.entity.Addon
import com.locatocam.app.db.entity.Varient
import com.locatocam.app.network.Status
import com.locatocam.app.repositories.AddProductRepository
import com.locatocam.app.repositories.OrderOnlineRepository
import com.locatocam.app.viewmodels.AddProductViewModel
import com.locatocam.app.viewmodels.OrderOnlineViewModel
import com.locatocam.app.views.order_online.AddProductViewModelFactory
import com.locatocam.app.views.order_online.AddonItemClick
import com.locatocam.app.views.order_online.VarientItemClick
import com.locatocam.app.views.order_online.adapter.AddonListAdapter
import com.locatocam.app.views.order_online.adapter.VarientListAdapter
import com.locatocam.app.views.order_online.fragments.adapters.TopBrandsAdapter
import com.locatocam.app.views.order_online.fragments.adapters.TopPicsAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.helpers.Util
import kotlin.random.Random

class AddonVarientFragment(var item: Item) : Fragment(),SingleClickEvent,VarientItemClick,AddonItemClick {

    lateinit var viewModel: AddProductViewModel
    lateinit var binding:FragmentAddonVarientBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentAddonVarientBinding.inflate(layoutInflater)
        var repository= AddProductRepository(requireActivity().application)
        var factory= AddProductViewModelFactory(repository)
        viewModel= ViewModelProvider(requireActivity(),factory).get(AddProductViewModel::class.java)

        viewModel.addedQty=1
        initvalues()
        setObservers()
        onclickListerners()
        return binding.root
    }

    fun initvalues() {

        binding.close.setOnClickListener { }

        binding.rootLay.setOnClickListener {  }
        binding.itemName.text = item.name

        if(item.variants!=null && item.variants?.items?.size!! >0){
            Log.e("variants is not empty", item.variants!!.items[0].name)

            binding.varientHeading.text = "Select Varient"
            item.variants?.items?.forEach { varient ->
                val view = LayoutInflater.from(context).inflate(R.layout.row_layout_radio_btn, null)
                var varient_name = view.findViewById<TextView>(R.id.varient_name)
                varient_name.setText(varient.name)
                var rb = view.findViewById<RadioButton>(R.id.a)
                rb.id = (0..1000).random()
                rb.setText("₹" + varient.price)
                rb.setTag(varient)
                binding.radioVarients.addView(view)
            }

            binding.radioVarients.setOnCheckedChangeListener { radioGroupPlus, i ->
                Log.e("variant add", item.variants!!.items[0].name)

                var rb=binding.radioVarients.findViewById<RadioButton>(radioGroupPlus.checkedRadioButtonId)
                Log.i("ju8899",rb.tag.toString())
                var varient =rb.tag as ItemXX
                viewModel.varient_selected=varient
            }

            try{
                var x=binding.radioVarients.getChildAt(0) as RelativeLayout
                if (x!=null){
                    x.children.forEach {
                        if (it is RadioButton){
                            it.isChecked=true
                        }
                    }

                }
            }catch (e:NullPointerException){

            }

            binding.add.setOnClickListener {
                Log.e("variant add", item.variants!!.items[0].name)

                var rb=binding.radioVarients.findViewById<RadioButton>(binding.radioVarients.checkedRadioButtonId)
                if (rb!=null){
                    Log.e("variant add", item.variants!!.items[0].name)

                    viewModel.addVarients(item)
                }else{
                    Toast.makeText(activity,"Select one varient",Toast.LENGTH_LONG).show()
                }

            }
        }

        if(item.addons.isNotEmpty()){
            Log.e("addons is not empty", item.addons[0].group_option_label)

            // binding.varientHeading.text = item.addons?.get(0).group_option_label
            item.addons?.get(0).items.forEach { addon ->

                val view = LayoutInflater.from(context).inflate(R.layout.row_layout_radio_btn, null)
                var varient_name = view.findViewById<TextView>(R.id.varient_name)
                varient_name.setText(addon.name)
                var rb = view.findViewById<RadioButton>(R.id.a)
                rb.id = (0..1000).random()
                rb.setText("₹" + addon.price)
                rb.setTag(addon)
                binding.radioVarients.addView(view)

            }

            binding.radioVarients.setOnCheckedChangeListener { radioGroupPlus, i ->
                Log.e("addon add", item.addons[0].group_option_label)

                var rb=binding.radioVarients.findViewById<RadioButton>(radioGroupPlus.checkedRadioButtonId)
                Log.i("ju8899",rb.tag.toString())
                var addon =rb.tag as ItemX
                viewModel.addon_selected=addon
            }

            try{
                var x=binding.radioVarients.getChildAt(0) as RelativeLayout
                if (x!=null){
                    x.children.forEach {
                        if (it is RadioButton){
                            it.isChecked=true
                        }
                    }

                }
            }catch (e:NullPointerException){
                Log.e("addon add", e.localizedMessage)

            }

            binding.add.setOnClickListener {
                Log.e("addw",item.name)

                if(item.variants!=null && item.variants?.items?.size!! >0){
                    Log.e("setOnClickListener",item.name)

                    var rb=binding.radioVarients.findViewById<RadioButton>(binding.radioVarients.checkedRadioButtonId)
                    if (rb!=null){
                        viewModel.addVarients(item)
                    }else{
                        Toast.makeText(activity,"Select one varient",Toast.LENGTH_LONG).show()
                    }
                }

                if (item.addons.isNotEmpty()){
                    Log.e("addon available",item.name)

                    var rb=binding.radioVarients.findViewById<RadioButton>(binding.radioVarients.checkedRadioButtonId)
                    if (rb!=null){
                        viewModel.addAddon(item)
                    }else{
                        Toast.makeText(activity,"Select one addon",Toast.LENGTH_LONG).show()
                    }
                }
            }
        }


    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
    fun setObservers(){
        if (item.variants!=null){
            lifecycleScope.launch {
                viewModel.getAllVarients(item.id)
                viewModel.varientsld.observe(viewLifecycleOwner,{
                    Log.i("ki9rfvv99", "datacvb")
                    var adapter= VarientListAdapter(it,this@AddonVarientFragment)
                    var layoutManager = LinearLayoutManager(requireActivity())
                    binding.recyc.setLayoutManager(layoutManager)
                    binding.recyc.adapter=adapter
                })

            }
        }

        if (item.addons!=null){
            lifecycleScope.launch {
                viewModel.getAllAddons(item.id)
                viewModel.addons.observe(viewLifecycleOwner, {
                    Log.i("ki9rfvv99", "datacvb")
                    var adapter = AddonListAdapter(it, this@AddonVarientFragment)
                    var layoutManager = LinearLayoutManager(requireActivity())
                    binding.recyc.setLayoutManager(layoutManager)
                    binding.recyc.adapter = adapter
                })
            }
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onclickItem(id: String) {

    }

    fun onclickListerners(){
        binding.increese.setOnClickListener {
            viewModel.addedQty++
            item.quantity++
            binding.itemcount.setText(viewModel.addedQty.toString())
        }

        binding.decreese.setOnClickListener {
            if (viewModel.addedQty>0){
                viewModel.addedQty--
                item.quantity--
                binding.itemcount.setText(viewModel.addedQty.toString())
            }
        }
    }

    override fun addItem(item: Varient) {

    }

    override fun increeseItem(item: Varient) {
        Log.i("ki999",item.varient_name)
        item.quantity++
        Log.e("varient ",item.varient_name)
       viewModel.updatevarientCount(item)
    }

    override fun decreeseItem(item: Varient) {
        item.quantity--
        viewModel.updatevarientCount(item)
    }

    override fun addItemCustomizable(item: Varient) {
        TODO("Not yet implemented")
    }

    override fun addItem(item: Addon) {

    }

    override fun increeseItem(item: Addon) {

    }

    override fun decreeseItem(item: Addon) {

    }
}