package com.locatocam.app.views.custom.imageVideoPicker;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.locatocam.app.R;

import java.io.File;
import java.util.List;

public class GalleryListAdapterVideo extends RecyclerView.Adapter<GalleryListAdapterVideo.GalleryViewHolder> {
    private List<GalItemX> videoItems;
    public ListGalleryImVdoActivity activity;
    public GalleryListAdapterVideo(List<GalItemX> videoItems, ListGalleryImVdoActivity activity) {
        this.videoItems=videoItems;
        this.activity=activity;
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

        if(videoItems.get(position).getUri()!=null){
            try{
                Glide.with(holder.imageView.getContext())
                        .asBitmap()
                        .load(new File(Uri.parse(videoItems.get(position).getUri()).getPath()))
                        .into(holder.imageView);
            }catch (Exception e){
                Log.i("ee3ddc",e.getMessage());
            }
        }

            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.save(videoItems.get(position).getUri(),"video");
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
