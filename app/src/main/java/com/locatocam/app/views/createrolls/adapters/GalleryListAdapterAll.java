package com.locatocam.app.views.createrolls.adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.locatocam.app.R;
import com.locatocam.app.views.createrolls.ListGalleryActivity;
import com.locatocam.app.views.createrolls.Preview_Video_A;
import com.locatocam.app.views.createrolls.Variables;
import com.locatocam.app.views.createrolls.models.GalleryVideoItem;

import java.io.File;
import java.util.List;

public class GalleryListAdapterAll extends RecyclerView.Adapter<GalleryListAdapterAll.GalleryViewHolder> {
    private List<GalleryVideoItem> videoItems;
    public ListGalleryActivity videoRecorder;
    public GalleryListAdapterAll(List<GalleryVideoItem> videoItems, ListGalleryActivity videoRecorder) {
        this.videoItems=videoItems;
        this.videoRecorder=videoRecorder;
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.row_layout_video_item_tile,parent,false);
        GalleryViewHolder vh=new GalleryViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, @SuppressLint("RecyclerView") int position) {

        if(videoItems.get(position).getImage()==null){
            try{
                Glide.with(holder.imageView.getContext())
                        .asBitmap()
                        .load(new File(videoItems.get(position).getPath()).getPath())
                        .into(holder.imageView);
            }catch (Exception e){
                Log.i("ee3ddc",e.getMessage());
            }
        }

            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(videoRecorder, Preview_Video_A.class);
                    intent.putExtra("dir","");
                    Variables.outputfile2=videoItems.get(position).getPath();

                    videoRecorder.startActivityForResult(intent,24);
                }
            });

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
