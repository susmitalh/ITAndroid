package com.locatocam.app.views.createrolls;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Spanned;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.MovieHeaderBox;
import com.daasuu.gpuv.composer.GPUMp4Composer;
import com.daasuu.gpuv.egl.filter.GlFilterGroup;
import com.daasuu.gpuv.player.GPUPlayerView;
import com.daasuu.gpuv.player.PlayerScaleType;
import com.divyanshu.colorseekbar.ColorSeekBar;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoListener;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;
import com.googlecode.mp4parser.util.Path;
import com.locatocam.app.R;
import com.locatocam.app.security.SharedPrefEnc;
import com.locatocam.app.views.createrolls.video.FilterType;
import com.locatocam.app.views.createrolls.video.Filter_Adapter;
import com.locatocam.app.views.createrolls.video.MovieWrapperView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import needle.Needle;


public class Preview_Video_A extends AppCompatActivity  implements Player.EventListener {


    String video_url;
    GPUPlayerView gpuPlayerView;
    public static int  select_postion=0;
    final List<FilterType> filterTypes = FilterType.createFilterList();
    Filter_Adapter adapter;
    RecyclerView recylerview;

    String draft_file,dir="";
    ProgressBar progress_bar;
    String patx="";
    TextView add_sound_txt;
    public static int Sounds_list_Request_code=1;
    String selected_audio="";
    RelativeLayout audio_save_controls;
    LinearLayout add_music_layout,video_cut,add_caption,camera_options;
    ImageButton Goback;
    TextView cancel_audio,done_text,cancel_text;
    TextView done_audio;
    TextView next_btn;
    EditText add_text;
    ColorSeekBar colorPickerSeekBar;
    String colour_code="",overlay_text="";
    int trim_start=1000;
    int trim_end=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_video);
        progress_bar=findViewById(R.id.progress_bar);
        add_sound_txt=findViewById(R.id.add_sound_txt);
        video_cut=findViewById(R.id.video_cut);
        add_text=findViewById(R.id.add_text);
        done_text=findViewById(R.id.done_text);
        add_caption=findViewById(R.id.add_caption);
        colorPickerSeekBar=findViewById(R.id.color_seek_bar);
        camera_options=findViewById(R.id.camera_options);
        cancel_text=findViewById(R.id.cancel_text);





        trim_end=Variables.max_recording_duration;

        colorPickerSeekBar.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int i) {
                add_text.setTextColor(i);
            }
        });



        add_caption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


               /* Intent intent=new Intent(Preview_Video_A.this,EditVideoActivity.class);
                intent.putExtra("video_url",video_url);
                startActivity(intent);*/
                Goback.setVisibility(View.GONE);
                camera_options.setVisibility(View.GONE);
                next_btn.setVisibility(View.GONE);
                recylerview.setVisibility(View.GONE);

                add_text.setFocusable(true);
                cancel_text.setVisibility(View.VISIBLE);
                add_text.setVisibility(View.VISIBLE);
                add_text.requestFocus();

                done_text.setVisibility(View.VISIBLE);
                colorPickerSeekBar.setVisibility(View.VISIBLE);
                InputMethodManager inputMethodManager =
                        (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(
                        add_text.getApplicationWindowToken(),
                        InputMethodManager.SHOW_FORCED, 0);
            }
        });
        done_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();
                Log.i("t5rrrrrr", Html.toHtml((Spanned) add_text.getText()));
                String hexColor = String.format("#%06X", (0xFFFFFF & add_text.getCurrentTextColor()));
                Log.i("t5rrrrrr", hexColor);
                overlay_text= Html.toHtml((Spanned) add_text.getText());
                colour_code=hexColor;

                Goback.setVisibility(View.VISIBLE);
                camera_options.setVisibility(View.VISIBLE);
                next_btn.setVisibility(View.VISIBLE);
                recylerview.setVisibility(View.VISIBLE);

                cancel_text.setVisibility(View.GONE);
                done_text.setVisibility(View.GONE);
                if(add_text.getText().toString().equals("")){
                    add_text.setVisibility(View.GONE);
                }

                add_text.setFocusable(false);
                colorPickerSeekBar.setVisibility(View.GONE);


            }
        });

        cancel_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();
                Goback.setVisibility(View.VISIBLE);
                camera_options.setVisibility(View.VISIBLE);
                next_btn.setVisibility(View.VISIBLE);
                recylerview.setVisibility(View.VISIBLE);

                cancel_text.setVisibility(View.GONE);
                add_text.setVisibility(View.GONE);
                done_text.setVisibility(View.GONE);
                colorPickerSeekBar.setVisibility(View.GONE);
            }
        });
        cancel_audio=findViewById(R.id.cancel_audio);
        done_audio=findViewById(R.id.done_audio);

        next_btn=findViewById(R.id.next_btn);


        video_cut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* audio_save_controls.setVisibility(View.GONE);
                add_music_layout.setVisibility(View.VISIBLE);
                Goback.setVisibility(View.VISIBLE);
                recylerview.setVisibility(View.GONE);
                next_btn.setVisibility(View.GONE);*/
               Intent intent=new Intent(Preview_Video_A.this, TrimVideoCustom.class);
               intent.putExtra("video_url",video_url);
               startActivityForResult(intent,114);

            }
        });

        cancel_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                audio_save_controls.setVisibility(View.GONE);
                add_music_layout.setVisibility(View.VISIBLE);
                Goback.setVisibility(View.VISIBLE);
                recylerview.setVisibility(View.VISIBLE);
                next_btn.setVisibility(View.VISIBLE);

            }
        });
        done_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                audio_save_controls.setVisibility(View.GONE);
                add_music_layout.setVisibility(View.VISIBLE);
                Goback.setVisibility(View.VISIBLE);
                recylerview.setVisibility(View.VISIBLE);
                next_btn.setVisibility(View.VISIBLE);

                //merge audio video here
               // Merge_Video_Audio merge_video_audio=new Merge_Video_Audio(Preview_Video_A.this);
               // merge_video_audio.doInBackground(selected_audio,dir+Variables.outputfile,dir+Variables.output_filter_file);
                muxing(selected_audio,dir+Variables.outputfile);
            }
        });

        audio_save_controls=findViewById(R.id.audio_save_controls);
        add_music_layout=findViewById(R.id.add_music_layout);
        Goback=findViewById(R.id.Goback);


        Intent intent=getIntent();
        if(intent!=null){
            dir=intent.getStringExtra("dir");
           // draft_file=intent.getStringExtra("draft_file");
        }

        Intent intentx = getIntent();
        String action = intentx.getAction();
        String type = intentx.getType();


        if (Intent.ACTION_SEND.equals(action) && type != null) {
            Log.i("hbnnnn1111", type);
            if (type.startsWith("video/")) {
                Uri imageUri = (Uri) intentx.getParcelableExtra(Intent.EXTRA_STREAM);
                if (imageUri == null) {
                    imageUri = (Uri) getIntent().getExtras().get(Intent.EXTRA_STREAM);
                }
                Log.i("hbnnnn1111", imageUri.toString());

                String tty ;
                if(imageUri.toString().contains("content")){
                    tty=imageUri.toString();
                }else {
                    tty=getRealPathFromURI(this,imageUri);
                }

                Log.i("ju78888", tty);
                dir="";
                Variables.outputfile2=tty;
            }
        }

         patx = getExternalCacheDir().toString();




        select_postion=0;
        video_url= dir+Variables.outputfile2;



        findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
               // overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);

            }
        });


        findViewById(R.id.next_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(select_postion==0){
                    try {
                        if(dir.equals("")){
                            Functions.copyFile(new File(Variables.outputfile2),
                                    new File(patx+Variables.output_filter_file));
                        }else {
                            Functions.copyFile(new File(dir+Variables.outputfile2),
                                    new File(dir+Variables.output_filter_file));
                        }

                        GotopostScreen();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(dir+Variables.tag,e.toString());
                        Save_Video(dir+Variables.outputfile2,dir+Variables.output_filter_file);
                    }

                }else{
                    Save_Video(dir+Variables.outputfile2,patx+Variables.output_filter_file);
                }

            }
        });


        Set_Player(video_url);


        recylerview=findViewById(R.id.recylerview);

        adapter = new Filter_Adapter(this, filterTypes, new Filter_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int postion, FilterType item) {
                select_postion=postion;
                gpuPlayerView.setGlFilter(FilterType.createGlFilter(filterTypes.get(postion), getApplicationContext()));
                adapter.notifyDataSetChanged();
            }
        });
        recylerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recylerview.setAdapter(adapter);


        add_sound_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(Preview_Video_A.this, ListSoundWebActivity.class);
                startActivityForResult(intent,Sounds_list_Request_code);
            }
        });

        add_text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
    }

    private void closeKeyboard()
    {
        // this will give us the view
        // which is currently focus
        // in this layout
        View view = this.getCurrentFocus();

        // if nothing is currently
        // focus then this will protect
        // the app from crash
        if (view != null) {

            // now assign the system
            // service to InputMethodManager
            InputMethodManager manager
                    = (InputMethodManager)
                    getSystemService(
                            Context.INPUT_METHOD_SERVICE);
            manager
                    .hideSoftInputFromWindow(
                            view.getWindowToken(), 0);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == Sounds_list_Request_code) {
                if (data != null) {

                    selected_audio = data.getStringExtra("sound_uri");
                   // PreparedAudio();
                    Log.i("ty55555rr",video_url+"---"+patx+Variables.output_filter_file);

                    /*Merge_Video_Audio merge_video_audio=new Merge_Video_Audio(Preview_Video_A.this);
                    merge_video_audio.doInBackground(selected_audio,video_url,patx+Variables.output_filter_file);*/


                    Needle.onBackgroundThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            muxing(selected_audio,video_url);
                        }
                    });

                   /* Needle.onBackgroundThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                Movie video = MovieCreator.build(video_url);
                                Track videoTrack = video.getTracks().get(0);


                                Movie audio = MovieCreator.build(new File(selected_audio).getAbsolutePath()); // here
                                Track audioTrack = audio.getTracks().get(0);

                                Movie movie = new Movie();
                                movie.addTrack(videoTrack);
                                movie.addTrack(audioTrack);
                                Container mp4file = new DefaultMp4Builder().build(movie);
                                FileChannel fc = new FileOutputStream(new File(patx+Variables.output_filter_file)).getChannel();
                                mp4file.writeContainer(fc);
                                fc.close();
                            } catch (IOException e) {
                                Log.i("r4rrfvvv",e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    });*/


                }

            }else if(requestCode==15)
            {
                Preview_Video_A.this.setResult(RESULT_OK);
                finish();
            }else if(requestCode==114){
                String edited_uri=data.getStringExtra("trim_start");
                String trim_endx=data.getStringExtra("trim_end");
                String videouri=data.getStringExtra("videouri");
                trim_start= Integer.parseInt(edited_uri);
                trim_end= Integer.parseInt(trim_endx);

                Set_Player(videouri);

                if(videouri.contains("com.kitchenset.foodstamart")){
                    String filename=videouri.substring(videouri.lastIndexOf("/")+1);
                    Variables.outputfile2="/Locatoca/"+filename;
                }else {
                    Variables.outputfile2=videouri;
                }
            }
        }
    }

    // this function will set the player to the current video
    SimpleExoPlayer player;
    public void Set_Player(String path){


        DefaultTrackSelector trackSelector = new DefaultTrackSelector();
         player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "LocaToka"));

        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(path));

        player.prepare(videoSource);

        player.setRepeatMode(Player.REPEAT_MODE_ALL);
        player.addListener(this);
        player.seekTo(trim_start);

        player.setPlayWhenReady(true);



        gpuPlayerView = new GPUPlayerView(this);



        player.addVideoListener(new VideoListener() {
            @Override
            public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
                Log.d(dir+Variables.tag,width+" "+height);
                if(width>height){
                    gpuPlayerView.setPlayerScaleType(PlayerScaleType.RESIZE_FIT_WIDTH);
                }
                else
                    gpuPlayerView.setPlayerScaleType(PlayerScaleType.RESIZE_NONE);
            }
        });

        gpuPlayerView.setSimpleExoPlayer(player);
        gpuPlayerView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        ((MovieWrapperView) findViewById(R.id.layout_movie_wrapper)).addView(gpuPlayerView);

        gpuPlayerView.onResume();


    }



    // this is lifecyle of the Activity which is importent for play,pause video or relaese the player
    @Override
    protected void onStop() {
        super.onStop();
        if(player!=null){
           player.setPlayWhenReady(false);
         }

    }

    @Override
    protected void onPause() {
        super.onPause();
        player.stop();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(player!=null){
            player.setPlayWhenReady(true);
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if(player!=null){
            player.setPlayWhenReady(true);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(player!=null){
            player.setPlayWhenReady(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(player!=null){
            player.removeListener(Preview_Video_A.this);
            player.release();
            player=null;
        }

        try {

            if (audio != null) {
                audio.stop();
                audio.reset();
                audio.release();
            }

        }catch (Exception e){

        }
    }




    // this function will add the filter to video and save that same video for post the video in post video screen
    public void Save_Video(String srcMp4Path, final String destMp4Path){

        Log.i("33eddddd",srcMp4Path);
        Log.i("33eddddd",destMp4Path);
        Functions.Show_determinent_loader(this,false,false);
        new GPUMp4Composer(srcMp4Path, destMp4Path)
                .filter(new GlFilterGroup(FilterType.createGlFilter(filterTypes.get(select_postion), getApplicationContext())))
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

                                GotopostScreen();


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

                                    Toast.makeText(Preview_Video_A.this, "Try Again", Toast.LENGTH_SHORT).show();
                                }catch (Exception e){

                                }
                            }
                        });

                    }
                })
                .start();


    }








    public void GotopostScreen(){
      /*String v=  getBase64();
      Log.i("tfgg",v);*/
       //postFile();
        Intent intent=new Intent(Preview_Video_A.this,FinalPreviewActivity.class);
        String mobile= SharedPrefEnc.getPref(Preview_Video_A.this,"mobile");
        intent.putExtra("phone", mobile);
        //intent.putExtra("screenshot", getBase64());

        String file_path="";
        if(dir.equals("")){
            intent.putExtra("file_path",patx+Variables.output_filter_file);
            file_path=patx+Variables.output_filter_file;
        }else {
            intent.putExtra("file_path",dir+Variables.output_filter_file);
            file_path=dir+Variables.output_filter_file;
        }
        if(trim_start>1000){
            try {
                if(dir.equals("")){
                    Variables.gallery_trimed_video=patx+"/Locatoca/gallery_trimed_video.mp4";
                }
                Log.i("rdd3eeedd",file_path+"----"+dir+Variables.gallery_trimed_video);
                startTrim(new File(file_path), new File(dir+Variables.gallery_trimed_video), trim_start, trim_end);
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("r4rrrrrfff",e.getMessage());
            }
        }else {
            intent.putExtra("overlay_text",overlay_text);
            intent.putExtra("colour_code",colour_code);
            startActivityForResult(intent,15);
        }

    }




    /*private void postFile() {

        final ProgressDialog progressDialog=new ProgressDialog(Preview_Video_A.this);
        progressDialog.setMessage("Uploading plese wait...");
        progressDialog.show();
       // progress_bar.setVisibility(View.VISIBLE);
        String url = "https://loca-toca.com/Api/insert_rolls";
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                //progress_bar.setVisibility(View.GONE);
                progressDialog.dismiss();
                Log.i("errrrrrr", resultResponse);
                Toast.makeText(getApplicationContext(),"Posted succesfully..",Toast.LENGTH_LONG).show();
                *//*Intent intent=new Intent(Preview_Video_A.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);*//*
                Preview_Video_A.this.setResult(RESULT_OK);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("status");
                        String message = response.getString("message");

                        Log.e("Error Status", status);
                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message + " Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message + " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message + " Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("errrrrrr", errorMessage);
               *//* Toast.makeText(getApplicationContext(),"Posted succesfully, will be live after approval",Toast.LENGTH_LONG).show();
                Intent intent=new Intent(Preview_Video_A.this,MainActivity.class);
                startActivity(intent);
                finish();*//*
                //progress_bar.setVisibility(View.GONE);
                progressDialog.dismiss();
                error.printStackTrace();
                error.getMessage();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String mobile=SharedPrefEnc.getPref(Preview_Video_A.this,"mobile");
                params.put("phone", mobile);
                params.put("screenshot", getBase64());

                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();

                try {
                    if(dir+Variables.output_filter_file!=null){
                        //InputStream iStream =   getContentResolver().openInputStream(new File(dir+Variables.output_filter_file).ge());
                        InputStream targetStream;
                        if(dir.equals("")){
                             targetStream = new FileInputStream(new File(patx+Variables.output_filter_file));
                        }else {
                            targetStream = new FileInputStream(new File(dir+Variables.output_filter_file));
                        }
                        byte[] inputData = getBytes(targetStream);
                        Random random=new Random();
                        int nm=random.nextInt(100000);

                        params.put("file", new DataPart("video"+String.valueOf(nm)+".mp4", inputData, "video/mp4"));

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("errrrrrr", e.getMessage().toString());
                }

                return params;
            }
        };
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(5000000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
    }*/
    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private String getBase64(){
        String path;
        if(dir.equals("")){
            path =patx+Variables.output_filter_file;
        }else {
            path=dir+Variables.output_filter_file;
        }
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
    // Bottom all the function and the Call back listener of the Expo player
    @Override
    public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {
        Log.i("ju877777uuu","test");

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }


    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }



    @Override
    public void onBackPressed() {

        finish();
       // overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);

    }

    MediaPlayer audio;
    public  void PreparedAudio(){

        File file=new File(selected_audio);
        if(file.exists()) {
            audio = new MediaPlayer();
            try {
                audio.setDataSource(selected_audio);
                audio.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(audio!=null){
                audio.start();
                audio.setLooping(true);
            }
            /*MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(this, Uri.fromFile(file));
            String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            final int file_duration = Integer.parseInt(durationStr);

            if(file_duration<Variables.max_recording_duration){
                Variables.recording_duration=file_duration;
                initlize_Video_progress();
            }*/

        }

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


                   // mvhd.setMatrix(Matrix.ROTATE_180);
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
                Functions.Show_indeterminent_loader(Preview_Video_A.this,true,true);
            }

            @Override
            protected void onPostExecute(String result) {
                if(result.equals("error")){
                    Functions.cancel_indeterminent_loader();
                    Toast.makeText(Preview_Video_A.this, "Unable to load video", Toast.LENGTH_SHORT).show();
                }else {
                    Functions.cancel_indeterminent_loader();
                    //Chnage_Video_size(Variables.gallery_trimed_video, Variables.gallery_resize_video);
                    Intent intent=new Intent(Preview_Video_A.this,FinalPreviewActivity.class);
                    String mobile=SharedPrefEnc.getPref(Preview_Video_A.this,"mobile");
                    intent.putExtra("phone", mobile);
                    intent.putExtra("file_path",dir+Variables.gallery_trimed_video);
                    intent.putExtra("overlay_text",overlay_text);
                    intent.putExtra("colour_code",colour_code);
                    startActivityForResult(intent,15);
                }
            }


        }.execute();

    }

String TAG="trr44444fff";
    @SuppressLint("WrongConstant")
    private void muxing(String audioFilePath, String videopath) {

        String outputFile = "";

        try {

            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "finalxx2.mp4");
            file.createNewFile();
            outputFile = file.getAbsolutePath();

            MediaExtractor videoExtractor = new MediaExtractor();
            //AssetFileDescriptor afdd = getAssets().openFd("Produce.MP4");
            videoExtractor.setDataSource(videopath);

            MediaExtractor audioExtractor = new MediaExtractor();
            audioExtractor.setDataSource(audioFilePath);

            Log.d(TAG, "Video Extractor Track Count " + videoExtractor.getTrackCount());
            Log.d(TAG, "Audio Extractor Track Count " + audioExtractor.getTrackCount());

            MediaMuxer muxer = new MediaMuxer(outputFile, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

            videoExtractor.selectTrack(0);
            MediaFormat videoFormat = videoExtractor.getTrackFormat(0);
            int videoTrack = muxer.addTrack(videoFormat);

            audioExtractor.selectTrack(0);
            MediaFormat audioFormat = audioExtractor.getTrackFormat(0);
            int audioTrack = muxer.addTrack(audioFormat);

            Log.d(TAG, "Video Format " + videoFormat.toString());
            Log.d(TAG, "Audio Format " + audioFormat.toString());

            boolean sawEOS = false;
            int frameCount = 0;
            int offset = 100;
            int sampleSize = 256 * 1024;
            ByteBuffer videoBuf = ByteBuffer.allocate(sampleSize);
            ByteBuffer audioBuf = ByteBuffer.allocate(sampleSize);
            MediaCodec.BufferInfo videoBufferInfo = new MediaCodec.BufferInfo();
            MediaCodec.BufferInfo audioBufferInfo = new MediaCodec.BufferInfo();


            videoExtractor.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
            audioExtractor.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC);

            muxer.start();

            while (!sawEOS) {
                videoBufferInfo.offset = offset;
                videoBufferInfo.size = videoExtractor.readSampleData(videoBuf, offset);


                if (videoBufferInfo.size < 0 || audioBufferInfo.size < 0) {
                    Log.d(TAG, "saw input EOS.");
                    sawEOS = true;
                    videoBufferInfo.size = 0;

                } else {
                    videoBufferInfo.presentationTimeUs = videoExtractor.getSampleTime();
                    videoBufferInfo.flags = videoExtractor.getSampleFlags();
                    muxer.writeSampleData(videoTrack, videoBuf, videoBufferInfo);
                    videoExtractor.advance();


                    frameCount++;
                    Log.d(TAG, "Frame (" + frameCount + ") Video PresentationTimeUs:" + videoBufferInfo.presentationTimeUs + " Flags:" + videoBufferInfo.flags + " Size(KB) " + videoBufferInfo.size / 1024);
                    Log.d(TAG, "Frame (" + frameCount + ") Audio PresentationTimeUs:" + audioBufferInfo.presentationTimeUs + " Flags:" + audioBufferInfo.flags + " Size(KB) " + audioBufferInfo.size / 1024);

                }
            }

            Toast.makeText(getApplicationContext(), "frame:" + frameCount, Toast.LENGTH_SHORT).show();


            boolean sawEOS2 = false;
            int frameCount2 = 0;
            while (!sawEOS2) {
                frameCount2++;

                audioBufferInfo.offset = offset;
                audioBufferInfo.size = audioExtractor.readSampleData(audioBuf, offset);

                if (videoBufferInfo.size < 0 || audioBufferInfo.size < 0) {
                    Log.d(TAG, "saw input EOS.");
                    sawEOS2 = true;
                    audioBufferInfo.size = 0;
                } else {
                    audioBufferInfo.presentationTimeUs = audioExtractor.getSampleTime();
                    audioBufferInfo.flags = audioExtractor.getSampleFlags();
                    muxer.writeSampleData(audioTrack, audioBuf, audioBufferInfo);
                    audioExtractor.advance();


                    Log.d(TAG, "Frame (" + frameCount + ") Video PresentationTimeUs:" + videoBufferInfo.presentationTimeUs + " Flags:" + videoBufferInfo.flags + " Size(KB) " + videoBufferInfo.size / 1024);
                    Log.d(TAG, "Frame (" + frameCount + ") Audio PresentationTimeUs:" + audioBufferInfo.presentationTimeUs + " Flags:" + audioBufferInfo.flags + " Size(KB) " + audioBufferInfo.size / 1024);

                }
            }

            Toast.makeText(getApplicationContext(), "frame:" + frameCount2, Toast.LENGTH_SHORT).show();

            muxer.stop();
            muxer.release();


        } catch (IOException e) {
            Log.d(TAG, "Mixer Error 1 " + e.getMessage());
        } catch (Exception e) {
            Log.d(TAG, "Mixer Error 2 " + e.getMessage());
        }
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };

            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
