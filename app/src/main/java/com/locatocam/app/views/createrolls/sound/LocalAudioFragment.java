package com.locatocam.app.views.createrolls.sound;


import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.jiajunhui.xapp.medialoader.MediaLoader;
import com.jiajunhui.xapp.medialoader.bean.AudioItem;
import com.jiajunhui.xapp.medialoader.bean.AudioResult;
import com.jiajunhui.xapp.medialoader.callback.OnAudioLoaderCallBack;
import com.locatocam.app.R;
import com.locatocam.app.views.createrolls.ListSoundWebActivity;
import com.locatocam.app.views.createrolls.adapters.AudioListAdapterlocal;
import com.locatocam.app.views.createrolls.audio_trimmer.SoundRangeBar;
import com.locatocam.app.views.createrolls.models.GalleryVideoItem;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import needle.Needle;


public class LocalAudioFragment extends Fragment {

    RecyclerView recyc;
    AudioListAdapterlocal adapter;
    ProgressBar progress_bar;

    public MediaPlayer mediaPlayer;
    public LottieAnimationView animationView;
    public SoundRangeBar soundRangeBar;
    public long audio_start = 0;
    public LinearLayout audio_select;

    ProgressBar progressBar;
    Button select_audio;
    public TextView audio_name;
    String final_uri="";
    public LocalAudioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_web_audio, container, false);
        init(v);

        soundRangeBar = v.findViewById(R.id.soundrange);
        soundRangeBar.animate();

        soundRangeBar.setOnRangeListener(new SoundRangeBar.OnRangeListener() {
            @Override
            public void onRangeStart(long start, long max) {
                Log.i("values3333", String.valueOf(start) + "--" + String.valueOf(max));
                audio_start = start;
                if (mediaPlayer != null) {
                    mediaPlayer.seekTo((int) start * 1000);
                    audio_start = (int) start * 1000;
                   /* mediaPlayer.reset();
                    mediaPlayer*/
                }
            }

        });

        select_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListSoundWebActivity activity=(ListSoundWebActivity)getActivity();
                activity.trimAndSave(audio_name.getText().toString(),final_uri, String.valueOf(audio_start));
            }
        });
        soundRangeBar.setSoundEffectsEnabled(true);
        return v;
    }


    @Override
    public void onPause() {
        super.onPause();
        mediaPlayer.stop();
    }

    public void setrange(long vi) {
        soundRangeBar.setVisibility(View.GONE);
        soundRangeBar.setMax(vi / 1000);
        soundRangeBar.setVisibility(View.VISIBLE);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //loadAllVideos();

    }

    @Override
    public void onStart() {
        super.onStart();
        getAudioFiles();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mediaPlayer.stop();
    }



    private void init(View v) {
        mediaPlayer = new MediaPlayer();
        recyc = v.findViewById(R.id.recyclerview);
        audio_select = v.findViewById(R.id.audio_select);
        //recyc.setLayoutManager(new GridLayoutManager(this, 3));
        progress_bar = v.findViewById(R.id.progress_bar);

        recyc.setLayoutManager(new LinearLayoutManager(getActivity()));

        select_audio=v.findViewById(R.id.select_audio);
        audio_name=v.findViewById(R.id.audio_name);
    }


    private void loadAllVideos(){
        progress_bar.setVisibility(View.VISIBLE);
        Needle.onBackgroundThread().execute(new Runnable() {
            @Override
            public void run() {
                MediaLoader.getLoader().loadAudios(getActivity(), new OnAudioLoaderCallBack() {
                    @Override
                    public void onResult(AudioResult result) {
                        List<GalleryVideoItem> items=new ArrayList<>();
                        for(AudioItem audioItem:result.getItems()){
                            Log.i("errffvvvv",audioItem.getPath()+" "+audioItem.getDisplayName());
                            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(audioItem.getPath(), MediaStore.Video.Thumbnails.MICRO_KIND);
                            GalleryVideoItem item=new GalleryVideoItem(audioItem.getPath(),audioItem.getDisplayName(),bitmap);
                            item.setLength(audioItem.getDuration());
                            items.add(item);
                        }

                        adapter = new AudioListAdapterlocal(items, LocalAudioFragment.this);
                        recyc.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
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
                ContentResolver contentResolver = getActivity().getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

                Cursor cursor = contentResolver.query(uri, null, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    do {

                        @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                        @SuppressLint("Range") String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                        @SuppressLint("Range") String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                        @SuppressLint("Range") String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(url, MediaStore.Video.Thumbnails.MICRO_KIND);
                        GalleryVideoItem item=new GalleryVideoItem(url,title,bitmap);
                        item.setLength(Long.parseLong(duration));
                        items.add(item);
                    } while (cursor.moveToNext());
                }
                adapter = new AudioListAdapterlocal(items, LocalAudioFragment.this);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyc.setAdapter(adapter);
                        progress_bar.setVisibility(View.GONE);
                    }
                });

            }
        });






    }

    public void downloadAndPlay(String uri,ProgressBar progressBr,long range) {
        progress_bar=progressBr;
        progressBr.setVisibility(View.GONE);

        final_uri=uri;
        Uri myUri = Uri.parse(uri);
        try {
            mediaPlayer.stop();
            mediaPlayer.reset();


            mediaPlayer.setDataSource(getActivity(),myUri);
            setrange(range);
        } catch (IOException e) {
            e.printStackTrace();
        }catch (IllegalStateException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (IllegalStateException e) {
            e.printStackTrace();
        }
        mediaPlayer.seekTo(2000);
        mediaPlayer.start();
        audio_select.setVisibility(View.VISIBLE);
    }


}
