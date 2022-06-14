package com.locatocam.app.views.home.test;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.locatocam.app.ModalClass.AddShare;
import com.locatocam.app.ModalClass.AddView;
import com.locatocam.app.R;
import com.locatocam.app.data.responses.feed.BestSeller;
import com.locatocam.app.data.responses.feed.Data;
import com.locatocam.app.data.responses.feed.OffersDetail;
import com.locatocam.app.data.responses.feed.TopBrandDetail;
import com.locatocam.app.di.module.NetworkModule;
import com.locatocam.app.network.WebApi;
import com.locatocam.app.security.SharedPrefEnc;
import com.locatocam.app.views.home.HomeFragment;
import com.locatocam.app.views.home.header.HeaderFragment;

import net.minidev.json.JSONObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SimpleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int TYPE_HEADER = 0;
    private int TYPE_VIDEO = 1;
    private int TYPE_BANNER = 2;
    private int TYPE_TOP_BRAND = 3;
    private int TYPE_EMPTY = 4;
    private int TYPE_SELLER = 5;
    private int TYPE_BRAND_OFFERS = 6;
    public List<Data> mediaList;
    SimpleEvents simpleEvents;
    Context context;
    public static String viewCount;
    PostCountData postCountData;
    Follow follow;
    public static Boolean userClick = true;

    public SimpleAdapter(Context context, List<Data> mediaList, SimpleEvents simpleEvents, PostCountData postCountData, Follow follow) {
        this.mediaList = mediaList;
        this.simpleEvents = simpleEvents;
        notifyDataSetChanged();
        this.context = context;
        this.postCountData = postCountData;
        this.follow = follow;

    }

    public void addAll(List<Data> newitems) {
        this.mediaList.addAll(newitems);
        notifyDataSetChanged();
    }

    public void notifyDataChanged() {
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_VIDEO) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_exoplayer_basic, parent, false);
            return new SimpleExoPlayerViewHolder(view);
        } else if (viewType == TYPE_BANNER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_banner, parent, false);
            return new VHBanner(view);
        } else if (viewType == TYPE_TOP_BRAND) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_top_brandsr, parent, false);
            return new VHTopbrands(view);
        } else if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_page_header_layout, parent, false);
            return new VHHeader(view);
        } else if (viewType == TYPE_SELLER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_best_seller_item, parent, false);
            return new VHBestSeller(view);
        } else if (viewType == TYPE_BRAND_OFFERS) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_top_brand_offers_item, parent, false);
            return new VHTopBrandOffers(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_empty, parent, false);
            return new VHHeader(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        if (holder instanceof SimpleExoPlayerViewHolder) {
            userClick=true;
            ((SimpleExoPlayerViewHolder) holder).bind(mediaList.get(position), simpleEvents, position, postCountData, follow);


            String userid = SharedPrefEnc.getPref(context, "user_id");
            Log.e("TAGgg", "onBindViewHolder: " + userid);

           /* WebApi apiInterface = NetworkModule.Companion.getClient().create(WebApi.class);
            Log.e("TAGScroll", "onItemIsFissrstVisibleItem: "+position);

            if (lastCount!=position){
                lastCount=position;
                Log.e("TAGScroll", "onItemIsFisssssrstVisibleItem: "+lastCount);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", mediaList.get(position).getPost_id());
                jsonObject.put("view_type", mediaList.get(position).getType());
                jsonObject.put("user_id", userid);

                apiInterface.getAddView(jsonObject).enqueue(new Callback<AddView>() {
                    @Override
                    public void onResponse(Call<AddView> call, Response<AddView> response) {
                        postCountData.viewCount(response.body().getData().getViewsCount(),position);

                    }

                    @Override
                    public void onFailure(Call<AddView> call, Throwable t) {

                    }
                });
            }*/


        } else if (holder instanceof VHBanner) {
            Data item = mediaList.get(position);
            Glide.with(((VHBanner) holder).thumbnile.getContext())
                    .load(item.getBanner_image())
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .thumbnail(0.1f)
                    .into(((VHBanner) holder).thumbnile);
            Log.e("TAG", "onBindViewHoldecffr: "+item.getBanner_brand_active() );

            if (item.getBanner_brand_active()==0){
                ((VHBanner) holder).bannerHide.setVisibility(View.VISIBLE);
                if (item.getBanner_next_starting().equals("")){
                    ((VHBanner) holder).hide_text_banner.setText("Currently not accepting orders");

                }else {
                    ((VHBanner) holder).hide_text_banner.setText(item.getBanner_next_starting());
                }
            }else {
                ((VHBanner) holder).bannerHide.setVisibility(View.GONE);
            }

            ((VHBanner) holder).brand_name.setText(item.getBanner_brand_name());
            ((VHBanner) holder).location_name.setText(item.getBanner_location());
            ((VHBanner) holder).cusine.setText(item.getBanner_cuisine());
            ((VHBanner) holder).distance.setText(item.getBanner_km());
            ((VHBanner) holder).open_hours.setText(item.getBanner_time());
            ((VHBanner) holder).ratings.setText(item.getBanner_ratings());

        } else if (holder instanceof VHTopbrands) {
            Data item = mediaList.get(position);
            ViewGroup mLinearLayout = (ViewGroup) ((VHTopbrands) holder).item_holder;
            ViewGroup mScroll = (ViewGroup) ((VHTopbrands) holder).brand_layout_scroll;
            mLinearLayout.removeAllViews();
            mScroll.scrollTo(0,0);
            for (TopBrandDetail td : item.getTop_brand_details()) {
                View layout2 = LayoutInflater.from(((VHTopbrands) holder).item_holder.getContext()).inflate(R.layout.row_layout_top_brands, mLinearLayout, false);
                TextView name = (TextView) layout2.findViewById(R.id.name);
                CircleImageView imageView = (CircleImageView) layout2.findViewById(R.id.profile);

                Glide.with(imageView.getContext())
                        .load(td.getBrand_logo())
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .circleCrop()
                        .into(imageView);

                name.setText(td.getBrand_name());
                mLinearLayout.addView(layout2);
                Log.e("TAG", "onBindViewHolderTopBrand: " + td.getBrand_name());
            }

        } else if (holder instanceof VHBestSeller) {
            Data item = mediaList.get(position);
            ViewGroup mLinearLayout = (ViewGroup) ((VHBestSeller) holder).seller_item_holder;
            mLinearLayout.removeAllViews();

            for (BestSeller bestSeller : item.getBest_seller()) {
                View layout = LayoutInflater.from(((VHBestSeller) holder).seller_item_holder.getContext()).inflate(R.layout.bset_seller_item, mLinearLayout, false);
                ImageView sellerImage = layout.findViewById(R.id.seller_image);
                TextView sellerPercent = layout.findViewById(R.id.seller_percent);
                TextView sellerBrandName = layout.findViewById(R.id.seller_brand_name);
                TextView sellerItemName = layout.findViewById(R.id.seller_item_name);

                Glide.with(sellerImage.getContext())
                        .load(bestSeller.getImage())
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .circleCrop()
                        .into(sellerImage);
                sellerPercent.setText(bestSeller.getPercentage().toString());
                sellerBrandName.setText(bestSeller.getBrand_name());
                sellerItemName.setText(bestSeller.getItem_name());


                mLinearLayout.addView(layout);
            }
        } else if (holder instanceof VHTopBrandOffers) {
            Data item = mediaList.get(position);
            ViewGroup mLinearLayout = (ViewGroup) ((VHTopBrandOffers) holder).top_brand_offer_holder;
            mLinearLayout.removeAllViews();

            for (OffersDetail offersDetail : item.getOffers_details()) {
                View layout = LayoutInflater.from(((VHTopBrandOffers) holder).top_brand_offer_holder.getContext()).inflate(R.layout.top_brand_offer_item, mLinearLayout, false);
                ImageView offerSmallImage = layout.findViewById(R.id.offer_image_small);
                ImageView offerImage = layout.findViewById(R.id.offer_image);
                TextView offerDiscount1 = layout.findViewById(R.id.offer_dicount_1);
                TextView offer_min_order = layout.findViewById(R.id.offer_min_order);
                TextView offerName = layout.findViewById(R.id.offer_name);
                TextView offerDiscount = layout.findViewById(R.id.offer_discount2);
                TextView hide_text_banner_offers = layout.findViewById(R.id.hide_text_banner_offers);
                RelativeLayout banner_offers_hide = layout.findViewById(R.id.banner_offers_hide);

                if (offersDetail.getOffers_brand_active()==0){
                    banner_offers_hide.setVisibility(View.VISIBLE);
                    if (offersDetail.getOffers_next_starting().equals("")){
                        hide_text_banner_offers.setText("Currently not accepting orders");

                    }else {
                        hide_text_banner_offers.setText(item.getBanner_next_starting());
                    }
                }else {
                    banner_offers_hide.setVisibility(View.GONE);
                }

                Glide.with(offerSmallImage.getContext())
                        .load(offersDetail.getOffers_image())
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .circleCrop()
                        .into(offerSmallImage);
                Glide.with(offerSmallImage.getContext())
                        .load(offersDetail.getOffers_image())
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .circleCrop()
                        .into(offerImage);
                offerDiscount1.setText(offersDetail.getOffers_perc());
                offer_min_order.setText(offersDetail.getOffers_perc_desc());
                offerName.setText(offersDetail.getOffers_brand_name());
                offerDiscount.setText(offersDetail.getOffers_perc());

                mLinearLayout.addView(layout);
            }
        } else {
           /* Glide.with(((VHBanner)holder).profile_image.getContext())
                    .load(item.getProfile_pic())
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(((VHBanner)holder).profile_image);



            ((VHBanner)holder).datetime.setText(item.getPost_date_time());
            ((VHBanner)holder).name.setText(item.getProfile_name());
            ((VHBanner)holder).profile_follow_count.setText(" "+item.getProfile_follow_count());

            ((VHBanner)holder).description.setText(item.getDescription());
            ((VHBanner)holder).sub_header.setText(item.getSubheader());
            ((VHBanner)holder).header.setText(item.getHeader());
            ((VHBanner)holder).like.setText(" "+item.getLikes_count());
            ((VHBanner)holder).comment.setText(" "+item.getComments_count());
            ((VHBanner)holder).views.setText(" "+item.getViews_count());
            ((VHBanner)holder).shares.setText(" "+item.getShares_count());*/

        }


    }


    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        } else if (mediaList.get(position).getType().equals("banner")) {
            return TYPE_BANNER;
        } else if (mediaList.get(position).getType().equals("post")) {
            return TYPE_VIDEO;
        } else if (mediaList.get(position).getType().equals("top_brand")) {
            return TYPE_TOP_BRAND;
        } else if (mediaList.get(position).getType().equals("best_seller")) {
            return TYPE_SELLER;
        } else if (mediaList.get(position).getType().equals("brand_offers")) {
            return TYPE_BRAND_OFFERS;
        } else {
            return TYPE_EMPTY;
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    class VHHeader extends RecyclerView.ViewHolder {
        FrameLayout fragment_content;

        public VHHeader(@NonNull View itemView) {
            super(itemView);
            Log.e("onResume", String.valueOf(simpleEvents.isHeaderAdded()));
            if (!simpleEvents.isHeaderAdded()) {
                Log.e("TAG", "VHHeader: inside if");
                fragment_content = itemView.findViewById(R.id.fragment_container);
                if (fragment_content != null) {
                    Log.e("TAG", "VHHeader: inside if : " + fragment_content);
                    HeaderFragment fragment = new HeaderFragment();
                    FragmentManager fm = ((AppCompatActivity) itemView.getContext()).getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.fragment_container, fragment);
                    ft.commit();
                    Log.e("TAG", "VHHeader: inside if : loded");
                    simpleEvents.addHeader();
                }
            }

        }
    }

    class VHBanner extends RecyclerView.ViewHolder {
        ImageView thumbnile;
        TextView brand_name;
        TextView location_name;
        TextView cusine;
        TextView distance;
        TextView open_hours;
        TextView ratings,hide_text_banner;
        RelativeLayout bannerHide;

        public VHBanner(@NonNull View itView) {
            super(itView);
            thumbnile = itView.findViewById(R.id.thumbnile);

            brand_name = itView.findViewById(R.id.brand_name);
            location_name = itView.findViewById(R.id.location_name);
            cusine = itView.findViewById(R.id.cusine);
            distance = itView.findViewById(R.id.distance);
            open_hours = itView.findViewById(R.id.open_hours);
            ratings = itView.findViewById(R.id.ratings);
            bannerHide = itView.findViewById(R.id.banner_hide);
            hide_text_banner = itView.findViewById(R.id.hide_text_banner);
        }
    }

    class VHTopbrands extends RecyclerView.ViewHolder {

        LinearLayout item_holder;
        HorizontalScrollView brand_layout_scroll;

        public VHTopbrands(@NonNull View itView) {
            super(itView);
            item_holder = itView.findViewById(R.id.item_holder);
            brand_layout_scroll = itView.findViewById(R.id.brand_layout_scroll);
        }
    }

    class VHBestSeller extends RecyclerView.ViewHolder {

        LinearLayout seller_item_holder;

        public VHBestSeller(@NonNull View itView) {
            super(itView);
            seller_item_holder = itView.findViewById(R.id.brand_item_holder);
        }
    }

    class VHTopBrandOffers extends RecyclerView.ViewHolder {

        LinearLayout top_brand_offer_holder;

        public VHTopBrandOffers(@NonNull View itView) {
            super(itView);
            top_brand_offer_holder = itView.findViewById(R.id.tip_brand_offer_holder);
        }
    }
}
