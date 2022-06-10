package com.locatocam.app.views.createrolls;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.locatocam.app.R;

import life.knowledge4.videotrimmer.K4LVideoTrimmer;
import life.knowledge4.videotrimmer.interfaces.OnTrimVideoListener;

public class TrimVideoActivity extends AppCompatActivity implements OnTrimVideoListener {


    VideoView videoview;
    K4LVideoTrimmer video_trimmer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trim_video_activity);
        String dir=getExternalCacheDir().toString()+"/temp/";

        video_trimmer=((K4LVideoTrimmer) findViewById(R.id.video_trimmer));

        if(getIntent().getStringExtra("video_url")!=null){
            if(video_trimmer!=null){
                video_trimmer.setMaxDuration(20);
                video_trimmer.setVisibility(View.VISIBLE);
                video_trimmer.setOnTrimVideoListener(this);
                video_trimmer.setVideoURI(Uri.parse(getIntent().getStringExtra("video_url")));
                video_trimmer.setDestinationPath(dir);
            }
        }

    }


    @Override
    public void getResult(Uri uri) {
        Intent intent=new Intent();
        intent.putExtra("edited_uri",uri.toString());
        this.setResult(RESULT_OK,intent);
        finish();
        Log.i("resutlff",uri.toString());

    }

    @Override
    public void cancelAction() {
        finish();
    }
}
