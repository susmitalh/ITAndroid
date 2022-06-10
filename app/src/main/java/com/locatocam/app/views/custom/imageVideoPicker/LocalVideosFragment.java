package com.locatocam.app.views.custom.imageVideoPicker;


import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.locatocam.app.R;

import java.util.ArrayList;
import java.util.List;

import needle.Needle;


public class LocalVideosFragment extends Fragment {

    RecyclerView recyc;
    GalleryListAdapterVideo adapter;
    ProgressBar progress_bar;


    public LocalVideosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_loc_image, container, false);
        init(v);
        return v;
    }


    private void init(View v) {
        recyc = v.findViewById(R.id.recyclerview);
        recyc.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        progress_bar = v.findViewById(R.id.progress_bar);

    }
    @Override
    public void onPause() {
        super.onPause();
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
    }

    public void getAudioFiles() {
        progress_bar.setVisibility(View.VISIBLE);
        //looping through all rows and adding to list

        Needle.onBackgroundThread().execute(new Runnable() {
            @Override
            public void run() {
                List<GalItemX> items=new ArrayList<>();
                ContentResolver contentResolver = getActivity().getContentResolver();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String orderBy = MediaStore.Video.VideoColumns.DATE_MODIFIED + " DESC";

                Cursor cursor = contentResolver.query(uri, null, null, null, orderBy);

                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        @SuppressLint("Range") String url = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));

                        //Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(url, MediaStore.Video.Thumbnails.MICRO_KIND);
                        GalItemX item=new GalItemX(url);
                        items.add(item);
                    } while (cursor.moveToNext());
                }
                adapter = new GalleryListAdapterVideo(items, (ListGalleryImVdoActivity) getActivity());
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
}
