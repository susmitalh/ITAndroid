package com.locatocam.app.views.createrolls;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.jiajunhui.xapp.medialoader.MediaLoader;
import com.jiajunhui.xapp.medialoader.bean.AudioItem;
import com.jiajunhui.xapp.medialoader.bean.AudioResult;
import com.jiajunhui.xapp.medialoader.callback.OnAudioLoaderCallBack;
import com.locatocam.app.R;
import com.locatocam.app.views.createrolls.adapters.AudioListAdapterAll;
import com.locatocam.app.views.createrolls.audio_trimmer.SoundRangeBar;
import com.locatocam.app.views.createrolls.models.GalleryVideoItem;

import java.util.ArrayList;
import java.util.List;

import needle.Needle;


// this is the class which will add the selected soung to the created video
public class ListSoundActivity extends AppCompatActivity {

    RecyclerView recyc;
    AudioListAdapterAll adapter;
    ProgressBar progress_bar;
    ImageButton back;
    //TextView header_text;
    public MediaPlayer mediaPlayer;
    public LottieAnimationView animationView;
    public SoundRangeBar soundRangeBar;
    public  long audio_start=0;
    public LinearLayout audio_select;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_gallery_activity);
        init();
        //header_text.setText("Select audio");

        soundRangeBar=findViewById(R.id.soundrange);
        soundRangeBar.animate();

        soundRangeBar.setOnRangeListener(new SoundRangeBar.OnRangeListener() {
            @Override
            public void onRangeStart(long start, long max) {
                Log.i("values3333",String.valueOf(start)+"--"+String.valueOf(max));
                audio_start=start;
                if(mediaPlayer!=null){
                    mediaPlayer.seekTo((int) start*1000);
                    audio_start=(int) start*1000;
                   /* mediaPlayer.reset();
                    mediaPlayer*/
                }
            }

        });

        soundRangeBar.setSoundEffectsEnabled(true);
    }

    public void trimAndSave(String sound_name,String sound_uri){
        Intent intent=new Intent();
        intent.putExtra("sound_name",sound_name);
        intent.putExtra("sound_uri",sound_uri);
        intent.putExtra("start",String.valueOf(audio_start));
        setResult(Activity.RESULT_OK,intent);
        finish();
    }
    public void setrange(long vi){
        soundRangeBar.setMax(vi/1000);
    }
    @Override
    protected void onStart() {
        super.onStart();
        loadAllVideos();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
    }

    private void init(){
        mediaPlayer = new MediaPlayer();
        recyc=findViewById(R.id.recyclerview);
        //header_text=findViewById(R.id.header_text);
        audio_select=findViewById(R.id.audio_select);
        //recyc.setLayoutManager(new GridLayoutManager(this, 3));
        progress_bar=findViewById(R.id.progress_bar);
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        recyc.setLayoutManager(new LinearLayoutManager(this));

    }

    private void loadAllVideos(){
        progress_bar.setVisibility(View.VISIBLE);
        Needle.onBackgroundThread().execute(new Runnable() {
            @Override
            public void run() {
                MediaLoader.getLoader().loadAudios(ListSoundActivity.this, new OnAudioLoaderCallBack() {
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

                        adapter=new AudioListAdapterAll(items, ListSoundActivity.this);
                        recyc.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        progress_bar.setVisibility(View.GONE);
                    }
                });
            }
        });


    }

    /*private void saveRingtone(final int finish) {
        double startTime = audioWaveform.pixelsToSeconds(mStartPos);
        double endTime = audioWaveform.pixelsToSeconds(mEndPos);
        final int startFrame = audioWaveform.secondsToFrames(startTime);
        final int endFrame = audioWaveform.secondsToFrames(endTime - 0.04);
        final int duration = (int) (endTime - startTime + 0.5);



        // Save the sound file in a background thread
        Thread mSaveSoundFileThread = new Thread() {
            public void run() {
                // Try AAC first.
                String outPath = makeRingtoneFilename("AUDIO_TEMP", ".mp3");
                if (outPath == null) {
                    Log.e(" >> ", "Unable to find unique filename");
                    return;
                }
                File outFile = new File(outPath);
                try {
                    // Write the new file
                    mRecordedSoundFile.WriteFile(outFile, startFrame, endFrame - startFrame);
                } catch (Exception e) {
                    // log the error and try to create a .wav file instead
                    if (outFile.exists()) {
                        outFile.delete();
                    }
                    e.printStackTrace();
                }


                final String finalOutPath = outPath;
                Runnable runnable = new Runnable() {
                    public void run() {
                        afterSavingRingtone("AUDIO_TEMP",
                                finalOutPath,
                                duration, finish);
                    }
                };
                mHandler.post(runnable);
            }
        };
        mSaveSoundFileThread.start();
    }

    private String makeRingtoneFilename(CharSequence title, String extension) {
        String subDir;
        String externalRootDir = Environment.getExternalStorageDirectory().getPath();
        if (!externalRootDir.endsWith("/")) {
            externalRootDir += "/";
        }
        subDir = "media/audio/music/";
        String parentDir = externalRootDir + subDir;

        // Create the parent directory
        File parentDirFile = new File(parentDir);
        parentDirFile.mkdirs();

        // If we can't write to that special path, try just writing
        // directly to the sdcard
        if (!parentDirFile.isDirectory()) {
            parentDir = externalRootDir;
        }

        // Turn the title into a filename
        String filename = "";
        for (int i = 0; i < title.length(); i++) {
            if (Character.isLetterOrDigit(title.charAt(i))) {
                filename += title.charAt(i);
            }
        }

        // Try to make the filename unique
        String path = null;
        for (int i = 0; i < 100; i++) {
            String testPath;
            if (i > 0)
                testPath = parentDir + filename + i + extension;
            else
                testPath = parentDir + filename + extension;

            try {
                RandomAccessFile f = new RandomAccessFile(new File(testPath), "r");
                f.close();
            } catch (Exception e) {
                // Good, the file didn't exist
                path = testPath;
                break;
            }
        }

        return path;
    }*/
}
