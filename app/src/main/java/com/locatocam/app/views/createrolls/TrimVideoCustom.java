package com.locatocam.app.views.createrolls;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.locatocam.app.R;
import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

public class TrimVideoCustom extends AppCompatActivity {
    RangeSeekBar<Long> seekBar;
    VideoView videoView;
    ImageButton trim_all;
    long trim_start=0;
    long trim_end=0;
    TextView time_total;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trim_video_custom);

        videoView=findViewById(R.id.videoview);
        trim_all=findViewById(R.id.trim_all);
        time_total=findViewById(R.id.time_total);

       // Uri uri= Uri.parse(getIntent().getStringExtra("video_url"));

        Uri uri= Uri.parse(getIntent().getStringExtra("video_url"));
        videoView.setVideoURI(uri);
        videoView.start();



        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(this, uri);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInMillisec = Long.parseLong(time );
        retriever.release();

        seekBar = findViewById(R.id.rangeSeekbar);
        seekBar.setRangeValues(Long.valueOf(1), timeInMillisec/1000);
        time_total.setText(String.valueOf(timeInMillisec/1000)+" Sec");


        seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Long>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Long minValue, Long maxValue) {
               // Log.i("range_selected444",String.valueOf(minValue)+"--"+String.valueOf(maxValue));
                videoView.seekTo((int)(minValue*1000));
                trim_start=minValue*1000;
                trim_end=maxValue*1000;

                time_total.setText(String.valueOf(maxValue-minValue)+" Sec");
                if(!videoView.isPlaying()){
                    videoView.start();

                }
            }
        });
        //timelineView.setVideoURI(uri);

        trim_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 Intent intent=new Intent();
                 intent.putExtra("trim_start",String.valueOf(trim_start));
                 intent.putExtra("trim_end",String.valueOf(trim_end));
                 intent.putExtra("videouri",uri.toString());
                 setResult(RESULT_OK,intent);
                 finish();
               // Log.i("resutlff",uri.toString());
            }
        });
    }



}
