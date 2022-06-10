package com.locatocam.app.views.createrolls.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.locatocam.app.R;
import com.locatocam.app.views.createrolls.ListSoundActivity;
import com.locatocam.app.views.createrolls.models.GalleryVideoItem;


import java.io.IOException;
import java.util.List;

public class AudioListAdapterAll extends RecyclerView.Adapter<AudioListAdapterAll.GalleryViewHolder> {
    private List<GalleryVideoItem> videoItems;
    public ListSoundActivity videoRecorder;

    public AudioListAdapterAll(List<GalleryVideoItem> videoItems, ListSoundActivity videoRecorder) {
        this.videoItems=videoItems;
        this.videoRecorder=videoRecorder;
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.row_layout_audio_item,parent,false);
        GalleryViewHolder vh=new GalleryViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, @SuppressLint("RecyclerView") int position) {

            holder.audio_name.setText(videoItems.get(position).getName());
            holder.play_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Uri myUri = Uri.parse(videoItems.get(position).getPath());



                    try {
                        videoRecorder.mediaPlayer.stop();
                        videoRecorder.mediaPlayer.reset();
                        if(videoRecorder.animationView!=null){
                            videoRecorder.animationView.setVisibility(View.INVISIBLE);
                        }

                        // mediaPlayer.setDataSource(String.valueOf(myUri));
                        videoRecorder.mediaPlayer.setDataSource(holder.main_lay.getContext(),myUri);
                        videoRecorder.setrange(videoItems.get(position).getLength());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                    try {
                        videoRecorder.mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                    videoRecorder.mediaPlayer.seekTo(2000);
                    videoRecorder.mediaPlayer.start();
                    videoRecorder.audio_select.setVisibility(View.VISIBLE);
                    holder.equaliser.setVisibility(View.VISIBLE);
                    videoRecorder.animationView=holder.equaliser;
                }
            });

            holder.select_audio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (videoRecorder.audio_start>0){
                            videoRecorder.trimAndSave(videoItems.get(position).getName(),videoItems.get(position).getPath());
                    }else {
                        Intent intent=new Intent();
                        intent.putExtra("sound_name",videoItems.get(position).getName());
                        intent.putExtra("sound_uri",videoItems.get(position).getPath());
                        intent.putExtra("start",String.valueOf(0));
                        videoRecorder.setResult(Activity.RESULT_OK,intent);
                        videoRecorder.finish();
                    }

                }
            });
    }

    @Override
    public int getItemCount() {
        return videoItems.size();
    }

    class GalleryViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView audio_name;
        public LinearLayout main_lay;
        public Button select_audio;
        public LottieAnimationView equaliser;
        public ImageButton play_button;
        public GalleryViewHolder(View v) {
            super(v);
            imageView=v.findViewById(R.id.image);
            audio_name=v.findViewById(R.id.audio_name);
            main_lay=v.findViewById(R.id.main_lay);
            select_audio=v.findViewById(R.id.select_audio);
            equaliser=v.findViewById(R.id.eq_animation);
            play_button=v.findViewById(R.id.play_button);
        }
    }
}
