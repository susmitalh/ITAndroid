package com.locatocam.app.views.createrolls;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.locatocam.app.R;


public class TrimVideoFFmpg extends AppCompatActivity  {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trim_video_ffmpg);
        String dir=getExternalCacheDir().toString()+"/temp/";


    }


}
