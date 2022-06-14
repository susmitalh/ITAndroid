package com.locatocam.app.views.home.test;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.locatocam.app.Activity.PlayPostActivity;
import com.locatocam.app.ModalClass.AddShare;
import com.locatocam.app.ModalClass.Follow;
import com.locatocam.app.MyApp;
import com.locatocam.app.R;
import com.locatocam.app.data.responses.feed.Data;
import com.locatocam.app.di.module.NetworkModule;
import com.locatocam.app.network.WebApi;
import com.locatocam.app.reportpost.ReportPostActivity;
import com.locatocam.app.security.SharedPrefEnc;
import com.locatocam.app.views.MainActivity;
import com.locatocam.app.views.comments.CommentsActivity;
import com.locatocam.app.views.home.HomeFragment;
import com.locatocam.app.views.home.OtherProfileWithFeedFragment;
import com.locatocam.app.views.home.header.HeaderFragment;
import com.locatocam.app.views.home.header.IHeaderEvents;
import com.skyhope.showmoretextview.ShowMoreTextView;

import net.minidev.json.JSONObject;


import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import im.ene.toro.ToroPlayer;
import im.ene.toro.ToroUtil;
import im.ene.toro.exoplayer.ExoPlayerViewHelper;
import im.ene.toro.exoplayer.Playable;
import im.ene.toro.media.PlaybackInfo;
import im.ene.toro.widget.Container;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SimpleExoPlayerViewHolder extends RecyclerView.ViewHolder implements ToroPlayer {


    @Nullable
    public static ExoPlayerViewHelper helper;
    @Nullable
    private Uri mediaUri;
    public static boolean volumeMute;
    ProgressiveMediaSource mediaSource;
    public static TextView postShareBtn;
    String userId;
    WebApi apiInterface;
    PlayerView playerView;
    ImageButton volumebt;
    ImageButton options,msg_img;
    String follow_process;

    IHeaderEvents iHeaderEvents;

    CircleImageView profile_image;
    LinearLayout brandDetail;
    TextView name, datetime, profile_follow_count;
    TextView feed_description;
    TextView sub_header;
    TextView header;
    TextView like;
    TextView comment;
    TextView views, postShareText;
    TextView shares, brandName, brandUnfollow, profileUnfollow, fileSizeText;
    ImageView thumbnile, brandFollow, profileFollow;
    RelativeLayout profile_follow_layout, brand_follow_layout;
    CacheDataSourceFactory cacheDataSourceFactory;
    Cache simpleCache = MyApp.Companion.getSimpleCache();
    MyApp app = (MyApp) itemView.getContext().getApplicationContext();

    SimpleExoPlayerViewHolder(View itemView) {
        super(itemView);


        playerView = itemView.findViewById(R.id.player);
        volumebt = itemView.findViewById(R.id.volume);
        name = itemView.findViewById(R.id.name);
        postShareBtn = itemView.findViewById(R.id.post_share);

        profile_image = itemView.findViewById(R.id.profile_image);
        datetime = itemView.findViewById(R.id.datetime);
        profile_follow_count = itemView.findViewById(R.id.profile_follow_count);
        thumbnile = itemView.findViewById(R.id.thumbnile);
        options = itemView.findViewById(R.id.options);
        feed_description = itemView.findViewById(R.id.feed_description);


        sub_header = itemView.findViewById(R.id.sub_header);
        header = itemView.findViewById(R.id.header);
        like = itemView.findViewById(R.id.like);
        comment = itemView.findViewById(R.id.comment);
        views = itemView.findViewById(R.id.views);
        shares = itemView.findViewById(R.id.shares);
        msg_img = itemView.findViewById(R.id.msg_img);



       /* if (!volumeMute){
            Log.e("TAGMute", "default if: "+volumeMute );
            volumebt.setImageResource(R.drawable.ic_volume_off_grey_24dp);
            volumeMute=false;
        }else {
            Log.e("TAGMute", "default else: "+volumeMute );
            volumebt.setImageResource(R.drawable.ic_volume_up_grey_24dp);
            volumeMute=true;

        }*/

        brandName = itemView.findViewById(R.id.brand_name);
        brandFollow = itemView.findViewById(R.id.brand_follow);
        brandUnfollow = itemView.findViewById(R.id.brand_unfollow);
        brandDetail = itemView.findViewById(R.id.brand_detail);
        profileFollow = itemView.findViewById(R.id.profile_follow);
        profileUnfollow = itemView.findViewById(R.id.profile_unfollow);
        fileSizeText = itemView.findViewById(R.id.file_size_text);
        postShareText = itemView.findViewById(R.id.post_share_text);
        profile_follow_layout = itemView.findViewById(R.id.profile_follow_layout);
        brand_follow_layout = itemView.findViewById(R.id.brand_follow_layout);


        Log.e("TAGfgg", "SimpleExoPlayerViewHolder: ");


        if (HomeFragment.Companion.getOrder_visiblity() == true) {
            postShareBtn.setVisibility(View.GONE);
        } else {
            postShareBtn.setVisibility(View.VISIBLE);
        }

        cacheDataSourceFactory = new CacheDataSourceFactory(simpleCache,
                new DefaultHttpDataSourceFactory(
                        Util.getUserAgent(shares.getContext(),
                                "exo"))
        );


        volumebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!volumeMute) {
                    Log.e("TAGMute", "SimpleExoPlayerViewHolder if: " + volumeMute);
                    volumebt.setImageResource(R.drawable.ic_volume_up_grey_24dp);
                    if (helper != null)
                        helper.setVolume(1.0f);
                    volumeMute = true;
                } else {
                    Log.e("TAGMute", "SimpleExoPlayerViewHolder else: " + volumeMute);
                    volumebt.setImageResource(R.drawable.ic_volume_off_grey_24dp);
                    if (helper != null)
                        helper.setVolume(0.0f);
                    volumeMute = false;
                }
            }
        });


    }


    // called from Adapter to setup the media
    void bind(Data item, SimpleEvents simpleEvents, int position, PostCountData postCountData, com.locatocam.app.views.home.test.Follow follow) {


        if (item != null) {


            mediaUri = Uri.parse(item.getFile());
            // val uri = Uri.parse(linkUrl)
            userId = SharedPrefEnc.getPref(app, "user_id");
            apiInterface = NetworkModule.Companion.getClient().create(WebApi.class);

            Log.e("TAG", "binddd: "+item.getUser_id());

            if (userId.equals(item.getUser_id())){
                profile_follow_layout.setVisibility(View.GONE);
                msg_img.setVisibility(View.GONE);
            }else{
                profile_follow_layout.setVisibility(View.VISIBLE);
                msg_img.setVisibility(View.VISIBLE);
            }

            Glide.with(profile_image.getContext())
                    .load(item.getProfile_pic())
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .thumbnail(0.1f)
                    .into(profile_image);


            profile_image.setOnClickListener(v->{
                if (SimpleAdapter.userClick==true) {
                    HeaderFragment.Companion.onItemClick(item.getUser_id(), item.getProfile_influencer_code());
                }
            });
            name.setOnClickListener(v->{
                if (SimpleAdapter.userClick==true) {
                    HeaderFragment.Companion.onItemClick(item.getUser_id(), item.getProfile_influencer_code());
                }
            });

            Glide.with(thumbnile.getContext())
                    .load(item.getScreenshot())
                    .thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(thumbnile);


            datetime.setText(item.getPost_date_time());
            name.setText(item.getProfile_name());
            profile_follow_count.setText("  " + item.getProfile_follow_count());

            String description = item.getDescription();
            feed_description.setText(description);
            int a = feed_description.getLineCount();

            Log.e("TAG", "bindfffffff: " + description);
            if (a >= 3) {
                makeTextViewResizable(feed_description, 3, "Read More" +
                        "", true);
            }
            Log.e("TAG", "bindd: " + a);



/*
            description.setShowingLine(3);
            description.addShowMoreText("Read More");
            description.setShowMoreColor(Color.BLACK);
            description.setShowLessTextColor(Color.BLACK);
            description.addShowMoreText("Read Less");
*/


            sub_header.setText(item.getSubheader());
            header.setText(item.getHeader());
            like.setText(" " + item.getLikes_count());

            views.setText(" " + item.getViews_count());
            shares.setText(" " + item.getShares_count());
            brandName.setText(item.getBrand_name());
            fileSizeText.setText(item.getFile_size());

            if (item.getBrand_follow().equals("1")) {
                brandFollow.setVisibility(View.VISIBLE);
                brandUnfollow.setVisibility(View.GONE);
            } else {
                brandFollow.setVisibility(View.GONE);
                brandUnfollow.setVisibility(View.VISIBLE);
            }
            if (item.getProfile_follow().equals("1")) {
                profileFollow.setVisibility(View.VISIBLE);
                profileUnfollow.setVisibility(View.GONE);
            } else {
                profileFollow.setVisibility(View.GONE);
                profileUnfollow.setVisibility(View.VISIBLE);
            }
            Log.e("TAG", "bindaa: " + item.getProfile_name() + "  " + item.getProfile_follow());


            profile_follow_layout.setOnClickListener(v -> {


                Log.e("TAG", "bindcheck: " + follow_process);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("user_id", userId);
                jsonObject.put("follow_type", "influencer");
                jsonObject.put("follow_process", item.getProfile_follow().equals("1") ? "unfollow" : "follow");
                jsonObject.put("follower_id", item.getUser_id());
                Log.e("TAG", "bindfollow: " + userId + "  influencer  " + follow_process + "  " + item.getUser_id());
                apiInterface.getFollow(jsonObject).enqueue(new Callback<Follow>() {
                    @Override
                    public void onResponse(Call<Follow> call, Response<Follow> response) {
                        if (response.body().getStatus().equals("true")) {
                            if (item.getProfile_follow().equals("1")) {
                                follow.follow(position, "0");
                            } else {
                                follow.follow(position, "1");
                            }
                            Toast.makeText(app, "success", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Follow> call, Throwable t) {

                    }
                });

            });

            brand_follow_layout.setOnClickListener(v -> {
                brandFollowApi(item, position, follow);
            });


            if (item.getBrand_id().equals("0") || item.getBrand_id().equals("")) {
                brandDetail.setVisibility(View.GONE);
            } else if (item.getProfile_login_type().equals("company") || HeaderFragment.loginType.equals("company")) {
                brandDetail.setVisibility(View.GONE);
            } else {
                brandDetail.setVisibility(View.VISIBLE);
            }
            Log.e("TAGddd", "1: " + item.getViews_count());
            Log.e("TAGddd", "2: " + SimpleAdapter.viewCount);


            if (item.getFile_extension_type().equals("video")) {
                volumebt.setVisibility(View.VISIBLE);
            } else {
                volumebt.setVisibility(View.GONE);
            }

            Log.i("kioooo", item.getLiked());
            if (item.getLiked().equals("1")) {
                like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_liked, 0, 0, 0);
            } else {
                like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like, 0, 0, 0);
            }
           /* if (CommentsActivity.Companion.getCommentNo()==true){
                comment.setText(String.valueOf(Integer.parseInt(item.getComments_count()) + 1));
            }*/
            comment.setText(" " + item.getComments_count());


            comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(shares.getContext(), CommentsActivity.class);
                    intent.putExtra("postid", item.getPost_id());
                    intent.putExtra("userid", SharedPrefEnc.getPref(app, "user_id"));
                    intent.putExtra("position", position);
                    intent.putExtra("commentType", "post");
                    shares.getContext().startActivity(intent);
                }
            });


            playerView.getVideoSurfaceView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(playerView.getContext(), PlayPostActivity.class);
                    intent.putExtra("influencer_code", item.getProfile_influencer_code());
                    intent.putExtra("post_id", item.getPost_id());
                    playerView.getContext().startActivity(intent);
                }
            });


            postShareText.setOnClickListener(v -> {
                sharePost(item);
            });


            shares.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sharePost(item);
                }
            });

            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (item.getLiked().equals("0")) {
                        item.setLiked("1");
                        try {
                            item.setLikes_count(String.valueOf(Integer.parseInt(item.getLikes_count()) + 1));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        like.setText(" " + item.getLikes_count());
                        like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_liked, 0, 0, 0);
                        //cal like api
                        simpleEvents.like("like", item.getPost_id());
                    } else {
                        item.setLiked("0");
                        try {
                            item.setLikes_count(String.valueOf(Integer.parseInt(item.getLikes_count()) - 1));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        like.setText(" " + item.getLikes_count());
                        like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like, 0, 0, 0);
                        //cal unlike like api
                        simpleEvents.like("unlike", item.getPost_id());
                    }

                }
            });

            options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Initializing the popup menu and giving the reference as current context
                    PopupMenu popupMenu = new PopupMenu(options.getContext(), options);

                    // Inflating popup menu from popup_menu.xml file
                    popupMenu.getMenuInflater().inflate(R.menu.action_manu_post, popupMenu.getMenu());
                    Log.e("TAG", "onClickitemMenu: " + item.getUser_id());
                    if (item.getUser_id().equals(SharedPrefEnc.getPref(options.getContext(), "user_id"))) {
                        popupMenu.getMenu().getItem(1).setVisible(true);
                    } else {
                        popupMenu.getMenu().getItem(1).setVisible(false);
                    }
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            // Toast message on menu item clicked
                            switch (menuItem.getItemId()) {
                                case R.id.report:
                                    Intent intent = new Intent(shares.getContext(), ReportPostActivity.class);
                                    intent.putExtra("postid", item.getPost_id());
                                    shares.getContext().startActivity(intent);
                                    break;
                                case R.id.trash:
                                    simpleEvents.trash(item.getPost_id(), position);

                                    break;
                            }

                            return true;
                        }
                    });
                    // Showing the popup menu
                    popupMenu.show();
                }
            });
        }
    }


    private void brandFollowApi(Data item, int position, com.locatocam.app.views.home.test.Follow follow) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user_id", userId);
        jsonObject.put("follow_type", "brand");
        jsonObject.put("follow_process", item.getBrand_follow().equals("1") ? "unfollow" : "follow");
        jsonObject.put("follower_id", item.getBrand_id());
        Log.e("TAG", "bindfollow: " + userId + "  influencer  " + follow_process + "  " + item.getUser_id());
        apiInterface.getFollow(jsonObject).enqueue(new Callback<Follow>() {
            @Override
            public void onResponse(Call<Follow> call, Response<Follow> response) {
                if (response.body().getStatus().equals("true")) {
                    if (item.getBrand_follow().equals("1")) {
                        follow.brandFollow(position, "0");
                    } else {
                        follow.brandFollow(position, "1");
                    }
                    Toast.makeText(app, "success", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Follow> call, Throwable t) {

            }
        });
    }

    private void sharePost(Data item) {

        String message = "https://loca-toca.com/Login/index?si=" + item.getProfile_influencer_code() + "&id=" + item.getPost_id();
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, message);
        comment.getContext().startActivity(Intent.createChooser(share, "Share"));

        Log.e("TAG", "onCliffck: " + HeaderFragment.Companion.getUserid() + "  " + item.getPost_id() + "  " + item.getType());


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", item.getPost_id());
        jsonObject.put("share_type", item.getType());
        jsonObject.put("user_id", HeaderFragment.Companion.getUserid());

        apiInterface.getAddShare(jsonObject).enqueue(new Callback<AddShare>() {
            @Override
            public void onResponse(Call<AddShare> call, Response<AddShare> response) {
                Log.e("TAG", "onRespoxxnse: " + response.body().getMessage());
                shares.setText(" " + response.body().getData().getShareCount());

            }

            @Override
            public void onFailure(Call<AddShare> call, Throwable t) {
                Toast.makeText(app, "Item Share Fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public View getPlayerView() {
        return playerView;
    }

    @NonNull
    @Override
    public PlaybackInfo getCurrentPlaybackInfo() {
        return helper != null ? helper.getLatestPlaybackInfo() : new PlaybackInfo();
    }

    @Override
    public void initialize(@NonNull Container container, @Nullable PlaybackInfo playbackInfo) {

        if (helper == null) {
            helper = new ExoPlayerViewHelper(this, mediaUri, null, app.getConfig());


            helper.addEventListener(new Playable.EventListener() {
                @Override
                public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {
                }

                @Override
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                }

                @Override
                public void onLoadingChanged(boolean isLoading) {
                    if (!isLoading) {
                        //thumbnile.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    if (playWhenReady) {

                        if (helper != null) {
                            try {
                                if (volumeMute) {
                                    Log.e("TAGMute", "onplayState if: " + volumeMute);
                                    helper.setVolume(1.0f);
                                    volumebt.setImageResource(R.drawable.ic_volume_up_grey_24dp);


                                } else {
                                    Log.e("TAGMute", "on playstate else: " + volumeMute);
                                    helper.setVolume(0.0f);
                                    volumebt.setImageResource(R.drawable.ic_volume_off_grey_24dp);

                                }

                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                }

                @Override
                public void onRepeatModeChanged(int repeatMode) {

                }

                @Override
                public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {

                }

                @Override
                public void onPositionDiscontinuity(int reason) {

                }

                @Override
                public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

                }

                @Override
                public void onSeekProcessed() {

                }

                @Override
                public void onMetadata(Metadata metadata) {

                }

                @Override
                public void onCues(List<Cue> cues) {

                }

                @Override
                public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {

                }

                @Override
                public void onRenderedFirstFrame() {
                    thumbnile.setVisibility(View.GONE);
                }
            });

//            if(helper!= null){
//
//                try{
//                    helper.setVolume(0.0f);
//                }catch (NullPointerException exception){
//                    exception.printStackTrace();
//                }
//
//
//            }
        }
        helper.initialize(container, playbackInfo);
    }

    @Override
    public void release() {
        if (helper != null) {
            helper.release();
            helper = null;
        }
    }

    @Override
    public void play() {
        if (helper != null) {
            helper.play();
        }


    }

    @Override
    public void pause() {
        if (helper != null) {
            helper.pause();
            thumbnile.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public boolean isPlaying() {

        return helper != null && helper.isPlaying();
    }

    @Override
    public boolean wantsToPlay() {

        return ToroUtil.visibleAreaOffset(this, itemView.getParent()) >= 0.65;
    }

    @Override
    public int getPlayerOrder() {
        return getAdapterPosition();
    }

    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    int lineEndIndex = tv.getLayout().getLineEnd(0);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    Log.e("TAG", "bindfffffff onGlobalLayout maxLine == 0 : " + text);
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    Log.e("TAG", "bindfffffff onGlobalLayout maxLine > 0 : " + text);
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else {
                    int lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                    Log.e("TAG", "bindfffffff onGlobalLayout else : " + text);
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                }
            }
        });

    }

    private static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
                                                                            final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {


            ssb.setSpan(new MySpannable(false) {
                @Override
                public void onClick(View widget) {
                    if (viewMore) {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, -1, "Read Less", false);
                    } else {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, 3, "... Read More", true);
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;

    }

}