package com.locatocam.app.views.home.header

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.caverock.androidsvg.SVG
import com.locatocam.app.Activity.OtherProfileWithFeedActivity
import com.locatocam.app.R
import com.locatocam.app.adapter.InfluencerProfileBannerAdapter
import com.locatocam.app.adapter.OtherUserTitleAdapter
import com.locatocam.app.databinding.FragmentHeaderOtherProfileBinding
import com.locatocam.app.repositories.HeaderRepository
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.utility.OnViewPagerListener
import com.locatocam.app.utility.ViewPagerLayoutManager
import com.locatocam.app.viewmodels.HeaderViewModel
import com.locatocam.app.views.home.HomeFragment
import com.locatocam.app.views.home.OtherProfileWithFeedFragment
import com.locatocam.app.views.rollsexp.RollsExoplayerActivity

import android.widget.RadioButton
import kotlinx.android.synthetic.main.row_layout_userblock_reason.*
import android.widget.RadioGroup
import com.locatocam.app.MyApp
import com.locatocam.app.data.requests.ReqAddUserBlock
import com.locatocam.app.views.MainActivity


class HeaderFragmentOtherUser(val userid: String) : Fragment(), IHeaderEvents {

companion object {
    lateinit var binding: FragmentHeaderOtherProfileBinding
}
    lateinit var viewModel: HeaderViewModel
    lateinit var  dialog:Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        /*if (binding == null) {
            val parent = view?.parent as ViewGroup
            parent?.removeView(view)
        }*/
        binding = FragmentHeaderOtherProfileBinding.inflate(layoutInflater)
        MainActivity.activity.finish()

        var repository = HeaderRepository(userid, requireActivity().application)
        var factory = HeaderViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory).get(HeaderViewModel::class.java)

        setObsevers()
        setClickListeners()
        refreshAll()

        return binding.root
    }

    fun setObsevers() {
        viewModel.topInfluencer.observe(viewLifecycleOwner, {


        })

        viewModel.userDetails.observe(viewLifecycleOwner, {
            var maxposition = it.data?.logo?.size
            var layoutManagerProfile = ViewPagerLayoutManager(requireActivity(), 0)
            var pos=0
            layoutManagerProfile.mOnViewPagerListener = object :OnViewPagerListener{
                override fun onInitComplete() {
                }

                override fun onPageRelease(z: Boolean, i: Int) {
                }

                override fun onPageSelected(i: Int, z: Boolean) {
                    pos=i
                }

            }
            var layoutManagerTitle = ViewPagerLayoutManager(requireActivity(), 0)
            binding.profileImageRecycler.setLayoutManager(layoutManagerTitle)
            var profileAdapter = InfluencerProfileBannerAdapter(it.data?.logo)
            binding.profileImageRecycler.adapter = profileAdapter

            OtherProfileWithFeedFragment.binding.titleRecycler.layoutManager=layoutManagerProfile
            var otherUserAdapter=OtherUserTitleAdapter(it.data?.top_or_our_brands?.brand_details)
            OtherProfileWithFeedFragment.binding.titleRecycler.adapter=otherUserAdapter

            binding.leftBannerImage.setOnClickListener {
                if (pos > 0) {
                    binding.profileImageRecycler.smoothScrollToPosition(pos - 1)
                    OtherProfileWithFeedFragment.binding.titleRecycler.smoothScrollToPosition(pos - 1)
                    pos--
                }
            }
            binding.rightBannerImage.setOnClickListener {
                if (pos < maxposition?.minus(1)!!) {
                    binding.profileImageRecycler.smoothScrollToPosition(pos + 1)
                    OtherProfileWithFeedFragment.binding.titleRecycler.smoothScrollToPosition(pos + 1)
                    pos++
                }
            }

            if (it.data?.logo?.get(0)?.banner.equals("")) {
                binding.rightBannerImage.visibility = View.GONE
                binding.leftBannerImage.visibility = View.GONE
                OtherProfileWithFeedFragment.binding.titleLogo.visibility=View.VISIBLE
                OtherProfileWithFeedFragment.binding.titleRecycler.visibility=View.GONE
            } else {
                binding.topBrandTxt.setText("Our Brands")
                binding.mostPopularCard.visibility = View.GONE
                binding.rollsShortCard.visibility = View.GONE
                binding.tiViewAll.visibility=View.GONE
                binding.socialLayout.visibility=View.GONE
                OtherProfileWithFeedFragment.binding.titleLogo.visibility=View.GONE
                OtherProfileWithFeedFragment.binding.titleRecycler.visibility=View.VISIBLE
            }
            /*  Glide.with(this)
                .load(it.data?.logo?.get(0)?.logo)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .circleCrop()
                .into(binding.profilepic)*/
            HeaderFragment.infcode=it.data!!.influencer_code.toString()
            viewModel.getMostPopularVideos()

            binding.userName.text = "  " + it.data!!.name
            binding.phone.text = "  " + it.data.phone
            binding.email.text = "  " + it.data.email
            binding.address.text = "  " + it.data.address
            binding.posts.text = "  " + it.data.post + " Posts"
            binding.likes.text = "  " + it.data.likes + " Likes"
            binding.comments.text = "  " + it.data.comments + " Likes"
            binding.views.text = "  " + it.data.views + " Views"
            binding.joinedon.text = "  " + it.data.created
            binding.status.text = "  " + it.data.about

            binding.share.tag = it.data.influencer_code

            if (HomeFragment.orderType == true) {
                binding.orderText.visibility = View.VISIBLE
                binding.orderList.visibility = View.VISIBLE
            } else {
                binding.orderText.visibility = View.GONE
                binding.orderList.visibility = View.GONE
            }
            if (HomeFragment.order_visiblity == true) {
                binding.orderDelivery.visibility = View.VISIBLE
                binding.orderDineIn.visibility = View.VISIBLE
                binding.orderPickup.visibility = View.VISIBLE

                HeaderFragment.binding.orderDelivery.visibility = View.VISIBLE
                HeaderFragment.binding.orderDineIn.visibility = View.VISIBLE
                HeaderFragment.binding.orderPickup.visibility = View.VISIBLE


            } else {
                binding.orderDelivery.visibility = View.GONE
                binding.orderDineIn.visibility = View.GONE
                binding.orderPickup.visibility = View.GONE

                HeaderFragment.binding.orderDelivery.visibility = View.GONE
                HeaderFragment.binding.orderDineIn.visibility = View.GONE
                HeaderFragment.binding.orderPickup.visibility = View.GONE
            }

            if (it.data.user_id.equals(SharedPrefEnc.getPref(context, "user_id"))) {
                binding.follow.visibility = View.GONE
                binding.message.visibility = View.GONE
            }
            if (it.data.social_details!!.isEmpty()){
                binding.socialLayout.visibility=View.GONE
            }


            it.data.social_details?.forEach { that ->

                when (that.social_name) {
                    "Facebook" -> {
                        val svg = SVG.getFromString(that.icon)
                        val drawable = PictureDrawable(svg.renderToPicture())
                        Glide.with(this).load(drawable).into(binding.imgFacebook)


                        binding.facebook.text = " " + that.follower
                        binding.layoutFacebook.setOnClickListener {
                            var intent = Intent(Intent.ACTION_VIEW, that.link?.toUri())
                            startActivity(intent)
                        }
                    }
                    "Instagram" -> {

                        val svg = SVG.getFromString(that.icon)
                        val drawable = PictureDrawable(svg.renderToPicture())
                        Glide.with(this).load(drawable).into(binding.imgInstagram)

                        binding.instagram.text = " " + that.follower
                        binding.layoutInstagram.setOnClickListener {
                            var intent = Intent(Intent.ACTION_VIEW, that.link?.toUri())
                            startActivity(intent)
                        }
                    }
                    "Youtube" -> {
                        val svg = SVG.getFromString(that.icon)
                        val drawable = PictureDrawable(svg.renderToPicture())
                        Glide.with(this).load(drawable).into(binding.imgYoutube)

                        binding.youtube.text = " " + that.follower
                        binding.layoutYoutube.setOnClickListener {
                            val uri = Uri.parse(that.link)
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://" + uri)))
                        }
                    }
                    "Twitter" -> {


                        val svg = SVG.getFromString(that.icon)
                        val drawable = PictureDrawable(svg.renderToPicture())
                        Glide.with(this).load(drawable).into(binding.imgtwitter)

                        binding.twitter.text = " " + that.follower

                        binding.layoutTwitter.setOnClickListener {
                            val uri = Uri.parse(that.link)
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://" + uri)))
                        }

                    }
                     "linkedin"->{
                         if (that.icon!=null) {
                             val svg = SVG.getFromString(that.icon)
                             val drawable = PictureDrawable(svg.renderToPicture())
                             Glide.with(this).load(drawable).into(binding.imglinkedin)
                         }

                         if (that.follower.equals("")||that.follower.equals(null)){
                             binding.layoutLinkedin.visibility=View.GONE
                         }else {
                             binding.layoutLinkedin.visibility=View.VISIBLE
                             binding.linkedin.text = " " + that.follower
                             binding.layoutLinkedin.setOnClickListener {
                                 val uri = Uri.parse(that.link)
                                 startActivity(
                                     Intent(
                                         Intent.ACTION_VIEW,
                                         Uri.parse("http://" + uri)
                                     )
                                 )
                             }
                         }

                     }
                }
            }

            binding.follow.setText(it.data.follow_status)

            var ur = it
            binding.follow.setOnClickListener {
                if (binding.follow.text.toString().equals("Following")) {
                    viewModel.follow(ur.data?.user_id?.toInt()!!, "unfollow")
                    binding.follow.setText("Follow")
                } else {
                    viewModel.follow(ur.data?.user_id?.toInt()!!, "follow")
                    binding.follow.setText("Following")
                }
            }


            var layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            binding.topBrands.setLayoutManager(layoutManager)

                var adapter = TopBrandsAdapter(it.data.top_or_our_brands?.brand_details!!, this, it.data.logo)
                binding.topBrands.setAdapter(adapter)



        })
        viewModel.getUserDetails(userid)


        viewModel.mostPopularVideos.observe(viewLifecycleOwner, {

            var layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            binding.mostPopVideos.setLayoutManager(layoutManager)

            var adapter = MostPopularVideosAdapter(it, this)
            binding.mostPopVideos.setAdapter(adapter)
        })


        viewModel.rollsAndShortvdos.observe(viewLifecycleOwner, {

            var layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            binding.rollsVideos.setLayoutManager(layoutManager)

            var adapter = RollsAndShortVideosAdapter(it, this)
            binding.rollsVideos.setAdapter(adapter)
        })
    }


    fun refreshAll() {
        viewModel.getTopInfluencersV(userid, "top")
        //viewModel.getMostPopularVideos()
        viewModel.getRollsAndShortVideos(OtherProfileWithFeedFragment.inf_code)
    }


    fun setClickListeners() {
        binding.myLocation.text = " "+HomeFragment.add.toString()
        binding.myLocation.setOnClickListener {
//            Navigation.findNavController(binding.myLocation).navigate(R.id.action_homeFragment_to_locationSearchFragment

//            var parnt = requireActivity() as OtherProfileWithFeedFragment
//            parnt.showLocation()
//            OtherProfileWithFeedFragment.binding.locationView.visibility = View.VISIBLE
           OtherProfileWithFeedFragment.instants.locationShow()


        }

        binding.tiViewAll.setOnClickListener {
            var intent = Intent(requireActivity(), TopInfluencers::class.java)
            startActivity(intent)
        }

        binding.share.setOnClickListener {
            val message: String = "https://loca-toca.com/Main/index?si=" + binding.share.tag
            val share = Intent(Intent.ACTION_SEND)
            share.type = "text/plain"
            share.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(Intent.createChooser(share, "Share"))
        }
        binding.showHide.setOnClickListener {
            if (binding.moreinfolayout.visibility == View.VISIBLE) {
                binding.showHide.text = " Show more info"
                binding.moreinfolayout.visibility = View.GONE
            } else {
                binding.moreinfolayout.visibility = View.VISIBLE
                binding.showHide.text = " Hide more info"
            }
        }

        binding.follow.setOnClickListener {

        }
        binding.blockpopup.setOnClickListener {
            showPopup(binding.blockpopup)
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity is OtherProfileWithFeedActivity) {
            var act = activity as OtherProfileWithFeedActivity
            act.viewModel.address_text.observe(requireActivity(), {
                binding.myLocation.text = " "+HomeFragment.add.toString()
                HeaderFragment.binding.myLocation.text= " "+it
            })
        }

    }

    override fun onItemClick(user_id: String, inf_code: String) {
    }

    override fun onItemMostPopularVideos(user_id: String, inf_code: String) {

        /* val bundle = bundleOf("user_id" to user_id,"inf_code" to inf_code)
         Navigation
             .findNavController(binding.root)
             .navigate(R.id.action_homeFragment_to_otherProfileWithFeedFragment,bundle)*/
    }


    override fun onItemRollsAndShortVideos(firstid: String) {
        var intent = Intent(requireActivity(), RollsExoplayerActivity::class.java)
        intent.putExtra("firstid", firstid)
        intent.putExtra("inf_code", OtherProfileWithFeedFragment.inf_code)
        startActivity(intent)
    }

    override fun onBrandSearchClick(searchId: String?, userId: String?) {
        TODO("Not yet implemented")
    }

    fun showPopup(v:View) {
        val popup = PopupMenu(requireActivity(),v)
        val inflater: MenuInflater = popup.getMenuInflater()
        inflater.inflate(R.menu.user_block_manu, popup.getMenu())
        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

            when (item!!.itemId) {
                R.id.block -> {
                    repostConfirmApproval()
                }
            }

            true
        })
        popup.show()
    }

    fun repostConfirmApproval() {
        dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.popup_block_reason)
        dialog.setCanceledOnTouchOutside(false)
        val yes = dialog.findViewById<View>(R.id.yes) as Button
        val no = dialog.findViewById<View>(R.id.no) as Button
        val radioGroup=dialog.findViewById<View>(R.id.radioGroup) as RadioGroup
        viewModel.getblockUserReasons()
        viewModel.userBlockReason.observe(viewLifecycleOwner, {
            radioGroup.removeAllViews()
            for(item in it){
                val linearLayout = dialog.findViewById<LinearLayout>(R.id.container)
                val radioButton1 = RadioButton(requireActivity())
                radioButton1.layoutParams= LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
                radioButton1.text=item.reason
                radioButton1.id =item.id.toInt()
                println(item.reason)
                radioGroup.addView(radioButton1)
            }

        })
        var text: String=""
        radioGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { rg, checkedId ->
            for (i in 0 until rg.childCount) {
                val btn = rg.getChildAt(i) as RadioButton
                if (btn.id == checkedId) {
                   text=btn.id.toString()
                    // do something with text
                    return@OnCheckedChangeListener
                }
            }
        })

        no.setOnClickListener { dialog.dismiss() }
        yes.setOnClickListener {
            confirmation(text.toInt())
            Log.i("mcmmmcm",text)
            //dialog.dismiss()
        }
        dialog.show()
    }
    fun confirmation(reasonId:Int) {
        val dialog1 = Dialog(requireActivity())
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog1.setContentView(R.layout.popup_confirmation)
        dialog1.setCanceledOnTouchOutside(false)
        val tittle = dialog1.findViewById<View>(R.id.tittle) as TextView
        val yes = dialog1.findViewById<View>(R.id.yes) as Button
        val no = dialog1.findViewById<View>(R.id.no) as Button
        tittle.text="Are you sure want to block this user?"
        no.setOnClickListener { dialog1.dismiss() }
        yes.setOnClickListener {
            val userId:String=SharedPrefEnc.getPref(MyApp.context,"user_id")
            var request = ReqAddUserBlock(
                userid.toInt(), reasonId,
                userId.toInt())
            viewModel.addblockUserReasons(request)
            viewModel.addUserBlock.observe(viewLifecycleOwner, {
                if(it.message.equals("User blocked.")) {
                    var intent = Intent(requireActivity(), MainActivity::class.java)
                    intent.putExtra("lat", MainActivity.instances.viewModel.lat)
                    intent.putExtra("lng", MainActivity.instances.viewModel.lng)
                    intent.putExtra("address", MainActivity.instances.viewModel.add)
                    startActivity(intent)
                    dialog1.dismiss()
                    dialog.dismiss()
                }
            })
        }
        dialog1.show()
    }
    fun getUserBlockReasonList(){
        viewModel.userBlockReason.observe(viewLifecycleOwner, {

        })
    }

}