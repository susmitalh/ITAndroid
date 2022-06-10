package com.locatocam.app.views.createrolls;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jiajunhui.xapp.medialoader.MediaLoader;
import com.jiajunhui.xapp.medialoader.bean.VideoItem;
import com.jiajunhui.xapp.medialoader.bean.VideoResult;
import com.jiajunhui.xapp.medialoader.callback.OnVideoLoaderCallBack;
import com.locatocam.app.R;
import com.locatocam.app.views.createrolls.adapters.GalleryListAdapterAll;
import com.locatocam.app.views.createrolls.models.GalleryVideoItem;

import java.util.ArrayList;
import java.util.List;

import needle.Needle;


// this is the class which will add the selected soung to the created video
public class ListGalleryActivity extends AppCompatActivity {

    RecyclerView recyc;
    GalleryListAdapterAll adapter;
    ProgressBar progress_bar;
    ImageButton back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_video_activity);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //loadAllVideos();
getAudioFiles();
    }

    private void init(){
        recyc=findViewById(R.id.recyclerview);
        recyc.setLayoutManager(new GridLayoutManager(this, 3));
        progress_bar=findViewById(R.id.progress_bar);
        back=findViewById(R.id.back);
        /*back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/
        //recyc.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));

    }

    private void loadAllVideos(){
        progress_bar.setVisibility(View.VISIBLE);
        Needle.onMainThread().execute(new Runnable() {
            @Override
            public void run() {
                MediaLoader.getLoader().loadVideos(ListGalleryActivity.this, new OnVideoLoaderCallBack() {
                    @Override
                    public void onResult(VideoResult result) {
                        List<GalleryVideoItem> items=new ArrayList<>();


                        for(VideoItem video:result.getItems()){
                            if(video.getDuration()<100000){
                                Log.i("errffvvvv",video.getPath()+" "+video.getDisplayName());
                                items.add(new GalleryVideoItem(video.getPath(),null));
                            }

                        }

                        adapter=new GalleryListAdapterAll(items,ListGalleryActivity.this);
                        recyc.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        progress_bar.setVisibility(View.GONE);
                    }
                });
            }
        });

    }


    public void getAudioFiles() {
        progress_bar.setVisibility(View.VISIBLE);
        //looping through all rows and adding to list

        Needle.onBackgroundThread().execute(new Runnable() {
            @Override
            public void run() {
                List<GalleryVideoItem> items=new ArrayList<>();
                ContentResolver contentResolver = getContentResolver();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String orderBy = MediaStore.Images.ImageColumns.DATE_MODIFIED + " DESC";
                Cursor cursor = contentResolver.query(uri, null, null, null, orderBy);

                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                       // String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                        @SuppressLint("Range") String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                        @SuppressLint("Range") String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                       // Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(url, MediaStore.Video.Thumbnails.MICRO_KIND);
                        GalleryVideoItem item=new GalleryVideoItem(url,title,null);
                        if(duration==null){
                            duration="0";
                        }
                        if(Integer.parseInt(duration)<100000 && Integer.parseInt(duration)>0){
                            item.setLength(Long.parseLong(duration));
                            items.add(item);
                        }


                        Log.i("rrr33eerr",url);
                    } while (cursor.moveToNext());
                }
                adapter=new GalleryListAdapterAll(items,ListGalleryActivity.this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyc.setAdapter(adapter);
                        progress_bar.setVisibility(View.GONE);
                    }
                });

            }
        });

    }

    public void back(View v){
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==24){
            setResult(RESULT_OK);
            finish();
        }
    }
}
