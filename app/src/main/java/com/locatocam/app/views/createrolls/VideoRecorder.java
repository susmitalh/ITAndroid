package com.locatocam.app.views.createrolls;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.MovieHeaderBox;
import com.daasuu.gpuv.camerarecorder.GPUCameraRecorder;
import com.daasuu.gpuv.composer.GPUMp4Composer;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;
import com.googlecode.mp4parser.util.Matrix;
import com.googlecode.mp4parser.util.Path;
import com.jiajunhui.xapp.medialoader.MediaLoader;
import com.jiajunhui.xapp.medialoader.bean.VideoItem;
import com.jiajunhui.xapp.medialoader.bean.VideoResult;
import com.jiajunhui.xapp.medialoader.callback.OnVideoLoaderCallBack;

import com.locatocam.app.R;
import com.locatocam.app.views.createrolls.adapters.GalleryListAdapter;
import com.locatocam.app.views.createrolls.fragments.Fragment_Callback;
import com.locatocam.app.views.createrolls.fragments.RecordingTimeRang_F;
import com.locatocam.app.views.createrolls.models.GalleryVideoItem;
import com.locatocam.app.views.createrolls.segment_progressbar.ProgressBarListener;
import com.locatocam.app.views.createrolls.segment_progressbar.SegmentedProgressBar;
import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class VideoRecorder extends AppCompatActivity implements View.OnClickListener {

    CameraView cameraView;
    int number=0;

    ArrayList<String> videopaths=new ArrayList<>();

    ImageButton record_image;
    ImageButton done_btn;
    boolean is_recording=false;
    boolean is_flash_on=false;

    ImageButton flash_btn;
    SegmentedProgressBar video_progress;
    LinearLayout camera_options;
    ImageButton rotate_camera,cut_video_btn;



    public static int Sounds_list_Request_code=1;
    TextView add_sound_txt;

    int sec_passed=0;
    long time_in_milis=0;

    TextView countdown_timer_txt,sec_passed_text;
    boolean is_recording_timer_enable;
    int recording_time=3;
    LottieAnimationView rec_animation;
    FrameLayout root_video_view;
    GLSurfaceView sampleGLView ;
    GPUCameraRecorder gpuCameraRecorder;
    LinearLayout effects;
    RecyclerView gallery_view;
    GalleryListAdapter adapter;
    String selected_audio="";
    String dir="";
    int start_location=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_recorder);
        init();

        dir=getExternalCacheDir().toString();
        Intent intent=getIntent();
        if(intent.hasExtra("sound_name")){
            Log.i("12w34e44",intent.getStringExtra("sound_id"));
            add_sound_txt.setText(intent.getStringExtra("sound_name"));
            Variables.Selected_sound_id=intent.getStringExtra("sound_id");
            PreparedAudio();
        }


        rec_animation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Start_or_Stop_Recording();
            }
        });

        countdown_timer_txt=findViewById(R.id.countdown_timer_txt);


        initlize_Video_progress();

       /* Log.i("iu8888888",Variables.root);
        Log.i("iu8888888",getExternalMediaDirs().toString());*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            check_permissions();
        }else {

            Functions.make_directry(dir+Variables.app_folder);
            Functions.make_directry(dir+Variables.draft_app_folder);
        }


        loadAllVideos();

    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==2){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Functions.make_directry(dir+Variables.app_folder);
                Functions.make_directry(dir+Variables.draft_app_folder);

            }

        }
    }

    public void Start_or_Stop_Recording(){
        Functions.make_directry(dir+Variables.app_folder);
        Functions.make_directry(dir+Variables.draft_app_folder);
        if (!is_recording && sec_passed<(Variables.recording_duration/1000)-1) {
            number=number+1;

            is_recording=true;

            File file = new File(dir+Variables.app_folder +  "myvideo"+(number)+".mp4");
            videopaths.add(dir+Variables.app_folder +  "myvideo"+(number)+".mp4");
            cameraView.captureVideo(file);


                Log.i("jnmmm", String.valueOf(file.canRead()));


            if(audio!=null)
                audio.start();


            done_btn.setBackgroundResource(R.drawable.ic_not_done);
            done_btn.setEnabled(false);
            done_btn.setVisibility(View.GONE);

            video_progress.resume();



            record_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_recoding_yes));
            rec_animation.playAnimation();

            cut_video_btn.setVisibility(View.GONE);

            camera_options.setVisibility(View.GONE);
            add_sound_txt.setClickable(false);
            //rotate_camera.setVisibility(View.GONE);

        }

        else if (is_recording) {

            is_recording=false;

            video_progress.pause();
            video_progress.addDivider();

            if(audio!=null)
                audio.pause();

            cameraView.stopVideo();


            Check_done_btn_enable();

            cut_video_btn.setVisibility(View.VISIBLE);

            record_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_recoding_no));
            rec_animation.cancelAnimation();
            camera_options.setVisibility(View.VISIBLE);

        }

        else if(sec_passed>(Variables.recording_duration/1000)){
            Functions.Show_Alert(this,"Alert","Video only can be a "+(int)Variables.recording_duration/1000+" S");
        }

        if(sec_passed>0 || is_recording){
            camera_options.setVisibility(View.GONE);
        }else {
            camera_options.setVisibility(View.VISIBLE);
        }


    }
int secpassed_upd=0;
    public void initlize_Video_progress(){
        sec_passed=0;
        video_progress=findViewById(R.id.video_progress);
        video_progress.enableAutoProgressView(Variables.recording_duration);
        video_progress.setDividerColor(Color.WHITE);
        video_progress.setDividerEnabled(true);
        video_progress.setDividerWidth(4);
        video_progress.setShader(new int[]{Color.RED, Color.RED, Color.RED});

        video_progress.SetListener(new ProgressBarListener() {
            @Override
            public void TimeinMill(long mills) {
                time_in_milis=mills;
                sec_passed = (int) (mills/1000);


                if(sec_passed!=secpassed_upd){
                    secpassed_upd=sec_passed;
                    sec_passed_text.setText(String.valueOf(sec_passed));
                    Log.i("loiiii",String.valueOf(sec_passed));
                }

                if(sec_passed>(Variables.recording_duration/1000)-1){
                    Start_or_Stop_Recording();
                }

                if(is_recording_timer_enable && sec_passed>=recording_time){
                    is_recording_timer_enable=false;
                    Start_or_Stop_Recording();
                }

            }
        });
    }
    private void init(){
        Variables.Selected_sound_id="null";
        Variables.recording_duration=Variables.max_recording_duration;
        rec_animation=findViewById(R.id.rec_animation);
        root_video_view=findViewById(R.id.root_video_view);
        sec_passed_text=findViewById(R.id.sec_passed_text);
        effects=findViewById(R.id.effects);
        effects.setOnClickListener(this);

        sampleGLView = new GLSurfaceView(getApplicationContext());

        cameraView = findViewById(R.id.camera);
        camera_options=findViewById(R.id.camera_options);

        record_image=findViewById(R.id.record_image);


        findViewById(R.id.upload_layout).setOnClickListener(this);


        cut_video_btn=findViewById(R.id.cut_video_btn);
        cut_video_btn.setVisibility(View.GONE);
        cut_video_btn.setOnClickListener(this);

        done_btn=findViewById(R.id.done);
        done_btn.setEnabled(false);
        done_btn.setOnClickListener(this);


        rotate_camera=findViewById(R.id.rotate_camera);
        rotate_camera.setOnClickListener(this);
        flash_btn=findViewById(R.id.flash_camera);
        flash_btn.setOnClickListener(this);

        findViewById(R.id.Goback).setOnClickListener(this);

        add_sound_txt=findViewById(R.id.add_sound_txt);
        add_sound_txt.setOnClickListener(this);

        findViewById(R.id.time_btn).setOnClickListener(this);

        gallery_view=findViewById(R.id.gallery_view);
        gallery_view.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));

    }



    private void rotateCamera(){
        cameraView.toggleFacing();
    }


    public void Check_done_btn_enable(){
        if(sec_passed>(Variables.min_time_recording/1000)) {
            done_btn.setBackgroundResource(R.drawable.ic_done);
            done_btn.setVisibility(View.VISIBLE);
            done_btn.setEnabled(true);
        }else {
            done_btn.setBackgroundResource(R.drawable.ic_not_done);
            done_btn.setEnabled(false);
            done_btn.setVisibility(View.GONE);
        }
    }


    // this will apped all the videos parts in one  fullvideo
    private boolean append() {
        final ProgressDialog progressDialog=new ProgressDialog(VideoRecorder.this);
        new Thread(new Runnable() {
            @Override
            public void run() {


                runOnUiThread(new Runnable() {
                    public void run() {

                        progressDialog.setMessage("Please wait..");
                        progressDialog.show();
                    }
                });

                ArrayList<String> video_list=new ArrayList<>();
                for (int i=0;i<videopaths.size();i++){

                    File file=new File(videopaths.get(i));
                    if(file.exists()) {
                        try {
                            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                            retriever.setDataSource(VideoRecorder.this, Uri.fromFile(file));
                            String hasVideo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);
                            boolean isVideo = "yes".equals(hasVideo);

                            if (isVideo && file.length() > 3000) {
                                Log.d("resp", videopaths.get(i));
                                video_list.add(videopaths.get(i));
                            }
                        }catch (Exception e){
                            Log.d(Variables.tag,e.toString());
                        }
                    }
                }



                try {

                    Movie[] inMovies = new Movie[video_list.size()];

                    for (int i=0;i<video_list.size();i++){

                        inMovies[i]= MovieCreator.build(video_list.get(i));
                    }


                    List<Track> videoTracks = new LinkedList<Track>();
                    List<Track> audioTracks = new LinkedList<Track>();
                    for (Movie m : inMovies) {
                        for (Track t : m.getTracks()) {
                            if (t.getHandler().equals("soun")) {
                                audioTracks.add(t);
                            }
                            if (t.getHandler().equals("vide")) {
                                videoTracks.add(t);
                            }
                        }
                    }
                    Movie result = new Movie();
                    if (audioTracks.size() > 0) {
                        result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
                    }
                    if (videoTracks.size() > 0) {
                        result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
                    }

                    Container out = new DefaultMp4Builder().build(result);

                    String outputFilePath=null;
                    if(audio!=null){
                        outputFilePath=dir+Variables.outputfile;
                    }else {
                        outputFilePath=dir+Variables.outputfile2;
                    }

                    FileOutputStream fos = new FileOutputStream(new File(outputFilePath));
                    out.writeContainer(fos.getChannel());
                    fos.close();

                    runOnUiThread(new Runnable() {
                        public void run() {

                            progressDialog.dismiss();
                            if(audio!=null)
                                //Merge_withAudio();
                                Go_To_preview_Activity();
                            else {
                                Go_To_preview_Activity();
                            }

                        }
                    });



                } catch (Exception e) {
                    Log.i("rffvv99000","test",e);
                }
            }
        }).start();



        return true;
    }



    // this will add the select audio with the video
    public void Merge_withAudio(){

        String audio_file;
        audio_file =selected_audio;


        Merge_Video_Audio merge_video_audio=new Merge_Video_Audio(VideoRecorder.this);
        merge_video_audio.doInBackground(audio_file,Variables.outputfile,Variables.outputfile2);

    }



    public void RotateCamera(){
        if(is_recording){
            Start_or_Stop_Recording();
            cameraView.toggleFacing();
            Start_or_Stop_Recording();
        }else {
            cameraView.toggleFacing();

        }

    }


    public void Remove_Last_Section(){

        if(videopaths.size()>0){
            File file=new File(videopaths.get(videopaths.size()-1));
            if(file.exists()) {

                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(VideoRecorder.this, Uri.fromFile(file));
                String hasVideo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);
                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                long timeInMillisec = Long.parseLong(time );
                boolean isVideo = "yes".equals(hasVideo);
                if (isVideo) {
                    time_in_milis=time_in_milis-timeInMillisec;
                    video_progress.removeDivider();
                    videopaths.remove(videopaths.size()-1);
                    video_progress.updateProgress(time_in_milis);
                    video_progress.back_countdown(timeInMillisec);
                    if(audio!=null) {
                        int audio_backtime = (int) (audio.getCurrentPosition()- timeInMillisec);
                        audio.seekTo(audio_backtime);
                    }

                    sec_passed = (int) (time_in_milis/1000);

                    Check_done_btn_enable();

                }
            }

            if(videopaths.isEmpty()){
                cut_video_btn.setVisibility(View.GONE);
                add_sound_txt.setClickable(true);
                rotate_camera.setVisibility(View.VISIBLE);
                video_progress.reset();
                PreparedAudio();
            }

            file.delete();
        }

        if(sec_passed>0 || is_recording){
            camera_options.setVisibility(View.GONE);
        }else {
            camera_options.setVisibility(View.VISIBLE);
        }
    }


    @SuppressLint("WrongConstant")
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.rotate_camera:
                RotateCamera();
                break;

            case R.id.upload_layout:
               // Pick_video_from_gallery();

                break;

            case R.id.done:
                append();
                break;
            case R.id.effects:
               // setCameraFilter();
                break;

            case R.id.cut_video_btn:
                Functions.Show_Alert(this, null, "Discard the last clip?", "DELETE", "CANCEL", new Callback() {
                    @Override
                    public void Responce(String resp) {
                        if(resp.equalsIgnoreCase("yes")){
                            Remove_Last_Section();
                        }
                    }
                });

                break;

            case R.id.flash_camera:
                if(is_flash_on){
                    is_flash_on=false;
                    cameraView.setFlash(0);
                    flash_btn.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_on));
                }else {
                    is_flash_on=true;
                    cameraView.setFlash(CameraKit.Constants.FLASH_TORCH);
                    flash_btn.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_off));
                }

                break;

            case R.id.Goback:
                onBackPressed();
                break;

            case R.id.add_sound_txt:

               // Intent intent =new Intent(this, ListSoundActivity.class);
                Intent intent =new Intent(this, ListSoundWebActivity.class);
                startActivityForResult(intent,Sounds_list_Request_code);
                //overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
                break;

            case R.id.time_btn:
                if(sec_passed+1<Variables.recording_duration/1000){
                    RecordingTimeRang_F recordingTimeRang_f = new RecordingTimeRang_F(new Fragment_Callback() {
                        @Override
                        public void Responce(Bundle bundle) {
                            if(bundle!=null){
                                is_recording_timer_enable=true;
                                recording_time=bundle.getInt("end_time");
                                countdown_timer_txt.setText("3");
                                countdown_timer_txt.setVisibility(View.VISIBLE);
                                record_image.setClickable(false);
                                rec_animation.setClickable(false);
                                final Animation scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                new CountDownTimer(4000,1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {

                                        countdown_timer_txt.setText(""+(millisUntilFinished/1000));
                                        countdown_timer_txt.setAnimation(scaleAnimation);

                                    }

                                    @Override
                                    public void onFinish() {
                                        record_image.setClickable(true);
                                        rec_animation.setClickable(true);
                                        countdown_timer_txt.setVisibility(View.GONE);
                                        Start_or_Stop_Recording();
                                    }
                                }.start();

                            }
                        }
                    });
                    Bundle bundle=new Bundle();
                    if(sec_passed<(Variables.recording_duration/1000)-3)
                        bundle.putInt("end_time",(sec_passed+3));
                    else
                        bundle.putInt("end_time",(sec_passed+1));

                    bundle.putInt("total_time",(Variables.recording_duration/1000));
                    recordingTimeRang_f.setArguments(bundle);
                    recordingTimeRang_f.show(getSupportFragmentManager(), "");
                }
                break;

        }


    }


    public void Pick_video_from_gallery(){
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        startActivityForResult(intent, Variables.Pick_video_from_gallery);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == Sounds_list_Request_code) {
                if (data != null) {
                    add_sound_txt.setText(data.getStringExtra("sound_name"));
                    Log.i("gbbjjjjjjjjj",data.getStringExtra("sound_uri"));
                    selected_audio= data.getStringExtra("sound_uri");
                    start_location= Integer.parseInt(data.getStringExtra("start"));
                    PreparedAudio();
                }

            }

            else if (requestCode == Variables.Pick_video_from_gallery) {
                Uri uri = data.getData();
                try {
                    File video_file = FileUtils.getFileFromUri(this, uri);

                    if (getfileduration(uri) < Variables.max_recording_duration) {
                        Chnage_Video_size(video_file.getAbsolutePath(), Variables.gallery_resize_video);

                    } else {
                        try {
                            startTrim(video_file, new File(Variables.gallery_trimed_video), 1000, Variables.max_recording_duration);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
            else if(requestCode==24){
                finish();
            }
        }

    }



    public long getfileduration(Uri uri) {
        try {

            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(this, uri);
            String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            final int file_duration = Integer.parseInt(durationStr);

            return file_duration;
        }
        catch (Exception e){

        }
        return 0;
    }


    public void Chnage_Video_size(String src_path,String destination_path){

        Functions.Show_determinent_loader(this,false,false);
        new GPUMp4Composer(src_path, destination_path)
                .size(720, 1280)
                .videoBitrate((int) (0.25 * 16 * 540 * 960))
                .listener(new GPUMp4Composer.Listener() {
                    @Override
                    public void onProgress(double progress) {

                        Log.d("resp",""+(int) (progress*100));
                        Functions.Show_loading_progress((int)(progress*100));

                    }

                    @Override
                    public void onCompleted() {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Functions.cancel_determinent_loader();

                               /* Intent intent=new Intent(VideoRecorder.this, GallerySelectedVideo_A.class);
                                intent.putExtra("video_path",Variables.gallery_resize_video);
                                startActivity(intent);*/

                            }
                        });


                    }

                    @Override
                    public void onCanceled() {
                        Log.d("resp", "onCanceled");
                    }

                    @Override
                    public void onFailed(Exception exception) {

                        Log.d("resp",exception.toString());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    Functions.cancel_determinent_loader();

                                    Toast.makeText(VideoRecorder.this, "Try Again", Toast.LENGTH_SHORT).show();
                                }catch (Exception e){

                                }
                            }
                        });

                    }
                })
                .start();

    }

    public  void startTrim(final File src, final File dst, final int startMs, final int endMs) throws IOException {

        new AsyncTask<String,Void,String>() {
            @Override
            protected String doInBackground(String... strings) {
                try {

                    FileDataSourceImpl file = new FileDataSourceImpl(src);
                    Movie movie = MovieCreator.build(file);
                    List<Track> tracks = movie.getTracks();
                    movie.setTracks(new LinkedList<Track>());
                    double startTime = startMs / 1000;
                    double endTime = endMs / 1000;
                    boolean timeCorrected = false;

                    for (Track track : tracks) {
                        if (track.getSyncSamples() != null && track.getSyncSamples().length > 0) {
                            if (timeCorrected) {
                                throw new RuntimeException("The startTime has already been corrected by another track with SyncSample. Not Supported.");
                            }
                            startTime = Functions.correctTimeToSyncSample(track, startTime, false);
                            endTime = Functions.correctTimeToSyncSample(track, endTime, true);
                            timeCorrected = true;
                        }
                    }
                    for (Track track : tracks) {
                        long currentSample = 0;
                        double currentTime = 0;
                        long startSample = -1;
                        long endSample = -1;

                        for (int i = 0; i < track.getSampleDurations().length; i++) {
                            if (currentTime <= startTime) {
                                startSample = currentSample;
                            }
                            if (currentTime <= endTime) {
                                endSample = currentSample;
                            } else {
                                break;
                            }
                            currentTime += (double) track.getSampleDurations()[i] / (double) track.getTrackMetaData().getTimescale();
                            currentSample++;
                        }
                        movie.addTrack(new CroppedTrack(track, startSample, endSample));
                    }

                    Container out = new DefaultMp4Builder().build(movie);
                    MovieHeaderBox mvhd = Path.getPath(out, "moov/mvhd");


                     mvhd.setMatrix(Matrix.ROTATE_180);
                    if (!dst.exists()) {
                        dst.createNewFile();
                    }
                    FileOutputStream fos = new FileOutputStream(dst);
                    WritableByteChannel fc = fos.getChannel();
                    try {
                        out.writeContainer(fc);
                    } finally {
                        fc.close();
                        fos.close();
                        file.close();
                    }

                    file.close();
                    return "Ok";
                }catch (IOException e){
                    Log.d(Variables.tag,e.toString());
                    return "error";
                }catch (OutOfMemoryError e){
                    Log.d(Variables.tag,e.toString());
                    // Toast.makeText(getApplicationContext(),"Unable to load video",Toast.LENGTH_LONG).show();
                    // Functions.cancel_indeterminent_loader();
                    return "error";
                }

            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Functions.Show_indeterminent_loader(VideoRecorder.this,true,true);
            }

            @Override
            protected void onPostExecute(String result) {
                if(result.equals("error")){
                    Functions.cancel_indeterminent_loader();
                    Toast.makeText(VideoRecorder.this, "Unable to load video", Toast.LENGTH_SHORT).show();
                }else {
                    Functions.cancel_indeterminent_loader();
                    Chnage_Video_size(Variables.gallery_trimed_video, Variables.gallery_resize_video);
                }
            }


        }.execute();

    }


    // this will play the sound with the video when we select the audio
    MediaPlayer audio;
    public  void PreparedAudio(){

        File file=new File(selected_audio);
        if(file.exists()) {
            audio = new MediaPlayer();
            try {
                audio.setDataSource(selected_audio);
                audio.prepare();
                audio.seekTo(start_location);
            } catch (IOException e) {
                e.printStackTrace();
            }

            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(this, Uri.fromFile(file));
            String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            final int file_duration = Integer.parseInt(durationStr);

            if(file_duration<Variables.max_recording_duration){
                Variables.recording_duration=file_duration;
                initlize_Video_progress();
            }

        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
        Variables.outputfile2=Variables.app_folder+ "output.mp4";
    }



    @Override
    protected void onDestroy() {
        DeleteFile();
        super.onDestroy();
        try {

            if (audio != null) {
                audio.stop();
                audio.reset();
                audio.release();
            }
            cameraView.stop();

        }catch (Exception e){

        }
    }




    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setTitle("Abort video")
                .setMessage("If you Go back you lose your video")
                .setNegativeButton("Keep", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Abort", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                        Start_or_Stop_Recording();
                        finish();
                       // overridePendingTransition(R.anim.in_from_top, R.anim.out_from_bottom);

                    }
                }).show();

    }




    public void Go_To_preview_Activity(){
        //Intent intent =new Intent(this, Preview_Video_A.class);
        //startActivity(intent);
       // overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

        Intent intent=new Intent(VideoRecorder.this, Preview_Video_A.class);
        intent.putExtra("dir",dir);

        /*intent.setAction(Intent.ACTION_SEND);
        intent.setType("video/mp4");
        intent.putExtra(Intent.EXTRA_STREAM,Uri.parse(dir+Variables.outputfile2));*/
        startActivityForResult(intent,24);
    }




    // this will delete all the video parts that is create during priviously created video
    public void DeleteFile(){

        File output = new File(dir+Variables.outputfile);
        File output2 = new File(dir+Variables.outputfile2);

        File gallery_trimed_video = new File(dir+Variables.gallery_trimed_video);
        File gallery_resize_video = new File(dir+Variables.gallery_resize_video);


        if(output.exists()){
            output.delete();
        }

        if(output2.exists()){

            output2.delete();
        }


        if(gallery_trimed_video.exists()){
            gallery_trimed_video.delete();
        }

        if(gallery_resize_video.exists()){
            gallery_resize_video.delete();
        }

        for (int i=0;i<=12;i++) {

            File file = new File(dir+Variables.app_folder + "myvideo" + (i) + ".mp4");
            if (file.exists()) {
                file.delete();
            }

        }


    }

    public boolean check_permissions() {

        String[] PERMISSIONS = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA
        };

        if (!hasPermissions(this, PERMISSIONS)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(PERMISSIONS, 2);
            }
        }else {

            return true;
        }

        return false;
    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void loadAllVideos(){
        MediaLoader.getLoader().loadVideos(VideoRecorder.this, new OnVideoLoaderCallBack() {
            @Override
            public void onResult(VideoResult result) {
                List<GalleryVideoItem> items=new ArrayList<>();


                for(VideoItem video:result.getItems()){
                    Log.i("errffvvvv",video.getPath()+" "+video.getDisplayName()+" "+video.getDuration());
                    if(video.getDuration()<100000 ){
                        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(video.getPath(), MediaStore.Video.Thumbnails.MICRO_KIND);
                        items.add(new GalleryVideoItem(video.getPath(),bitmap));
                    }
                    if(items.size()>3){
                        break;
                    }
                }

                items.add(0,new GalleryVideoItem("open_gallery",null));
                adapter=new GalleryListAdapter(items,VideoRecorder.this);
                gallery_view.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
