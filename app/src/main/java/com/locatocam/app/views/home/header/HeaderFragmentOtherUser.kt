package com.locatocam.app.views.home.header

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.locatocam.app.adapter.InfluencerProfileBannerAdapter
import com.locatocam.app.adapter.OtherUserTitleAdapter
import com.locatocam.app.databinding.FragmentHeaderOtherProfileBinding
import com.locatocam.app.repositories.HeaderRepository
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.utility.OnViewPagerListener
import com.locatocam.app.utility.ViewPagerLayoutManager
import com.locatocam.app.viewmodels.HeaderViewModel
import com.locatocam.app.views.MainActivity
import com.locatocam.app.views.home.HomeFragment
import com.locatocam.app.views.home.OtherProfileWithFeedFragment
import com.locatocam.app.views.rollsexp.RollsExoplayerActivity


class HeaderFragmentOtherUser(val userid: String) : Fragment(), IHeaderEvents {


    lateinit var binding: FragmentHeaderOtherProfileBinding
    lateinit var viewModel: HeaderViewModel

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
            var pos:Int=0
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
            viewModel.getMostPopularVideos(it.data!!.influencer_code.toString())

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
            } else {
                binding.orderDelivery.visibility = View.GONE
                binding.orderDineIn.visibility = View.GONE
                binding.orderPickup.visibility = View.GONE
            }

            if (it.data.user_id.equals(SharedPrefEnc.getPref(context, "user_id"))) {
                binding.follow.visibility = View.GONE
                binding.message.visibility = View.GONE
            }



            it.data.social_details?.forEach { that ->
                when (that.social_name) {
                    "Facebook" -> {
                        binding.facebook.text = " " + that.follower
                        binding.facebook.setOnClickListener {
                            var intent = Intent(Intent.ACTION_VIEW, that.link?.toUri())
                            startActivity(intent)
                        }
                    }
                    "Instagram" -> {
                        binding.instagram.text = " " + that.follower
                        binding.instagram.setOnClickListener {
                            var intent = Intent(Intent.ACTION_VIEW, that.link?.toUri())
                            startActivity(intent)
                        }
                    }
                    "Youtube" -> {
                        binding.youtube.text = " " + that.follower
                        binding.youtube.setOnClickListener {
                            val uri = Uri.parse(that.link)
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://" + uri)))
                        }
                    }
                    "Twitter" -> {
                        binding.twitter.text = " " + that.follower
                        binding.twitter.setOnClickListener {
                            val uri = Uri.parse(that.link)
                            startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("http://" + uri)
                                )
                            )
                        }
                    }
                    /* "linkedin"->{

                         if (that.follower.isNullOrEmpty()){
                             binding.linkedin.visibility=View.GONE
                         }
                         binding.linkedin.text=" "+ that.follower

                     }*/
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


            var layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            binding.topBrands.setLayoutManager(layoutManager)

            var adapter = TopBrandsAdapter(it.data.top_or_our_brands?.brand_details!!, this)
            binding.topBrands.setAdapter(adapter)

        })
        viewModel.getUserDetails()


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

        binding.myLocation.setOnClickListener {
//            Navigation.findNavController(binding.myLocation).navigate(R.id.action_homeFragment_to_locationSearchFragment

            var parnt = requireActivity() as MainActivity
            parnt.showLocationPopup()


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
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity is MainActivity) {
            var act = activity as MainActivity
            act.viewModel.address_text.observe(requireActivity(), {
                binding.myLocation.text = it
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


}