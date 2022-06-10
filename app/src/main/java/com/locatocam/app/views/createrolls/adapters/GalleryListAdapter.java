package com.locatocam.app.views.createrolls.adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.locatocam.app.R;
import com.locatocam.app.views.createrolls.ListGalleryActivity;
import com.locatocam.app.views.createrolls.Preview_Video_A;
import com.locatocam.app.views.createrolls.Variables;
import com.locatocam.app.views.createrolls.VideoRecorder;
import com.locatocam.app.views.createrolls.models.GalleryVideoItem;

import java.util.List;


public class GalleryListAdapter extends RecyclerView.Adapter<GalleryListAdapter.GalleryViewHolder> {
    private List<GalleryVideoItem> videoItems;
    public VideoRecorder videoRecorder;
    public GalleryListAdapter(List<GalleryVideoItem> videoItems, VideoRecorder videoRecorder) {
        this.videoItems=videoItems;
        this.videoRecorder=videoRecorder;
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.row_layout_video_item,parent,false);
        GalleryViewHolder vh=new GalleryViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();

        if(videoItems.get(position).getPath().equals("open_gallery")){
            holder.imageView.setImageResource(R.drawable.ic_gallery_icon);
            holder.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            holder.imageView.setPadding(20,20,20,20);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(videoRecorder, ListGalleryActivity.class);
                    videoRecorder.startActivityForResult(intent,24);
                }
            });
        }else {
            holder.imageView.setImageBitmap(videoItems.get(position).getImage());
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Intent intent=new Intent(videoRecorder, FinalPreviewActivity.class);
                    Intent intent=new Intent(videoRecorder, Preview_Video_A.class);
                    intent.putExtra("dir","");
                    Variables.outputfile2=videoItems.get(position).getPath();
                    /*intent.setAction(Intent.ACTION_SEND);
                    intent.setType("video/mp4");
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(videoItems.get(position).getPath()));*/
                    videoRecorder.startActivityForResult(intent,24);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return videoItems.size();
    }

    class GalleryViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public GalleryViewHolder(View v) {
            super(v);
            imageView=v.findViewById(R.id.image);
        }
    }
}
