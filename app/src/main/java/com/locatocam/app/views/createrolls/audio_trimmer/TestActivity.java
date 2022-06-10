package com.locatocam.app.views.createrolls.audio_trimmer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.locatocam.app.R;


public class TestActivity extends AppCompatActivity implements View.OnClickListener {

    SoundRangeBar soundRangeBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        soundRangeBar=findViewById(R.id.soundrange);
        soundRangeBar.animate();

        soundRangeBar.setOnRangeListener(new SoundRangeBar.OnRangeListener() {
            @Override
            public void onRangeStart(long start, long max) {
                Log.i("values3333",String.valueOf(start)+"--"+String.valueOf(max));
            }
        });

        soundRangeBar.setSoundEffectsEnabled(true);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.upload:
                break;
            case R.id.back:
                finish();
                break;
        }
    }


}
