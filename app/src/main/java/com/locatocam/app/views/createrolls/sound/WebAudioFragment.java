package com.locatocam.app.views.createrolls.sound;


import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.locatocam.app.R;
import com.locatocam.app.custom.VolleySingleton;
import com.locatocam.app.views.createrolls.ListSoundWebActivity;
import com.locatocam.app.views.createrolls.adapters.AudioListAdapterWeb;
import com.locatocam.app.views.createrolls.audio_trimmer.SoundRangeBar;
import com.locatocam.app.views.createrolls.models.GalleryVideoItem;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class WebAudioFragment extends Fragment {

    RecyclerView recyc;
    AudioListAdapterWeb adapter;
    ProgressBar progress_bar;

    public MediaPlayer mediaPlayer;
    public LottieAnimationView animationView;
    public SoundRangeBar soundRangeBar;
    public long audio_start = 0;
    public LinearLayout audio_select;

    ProgressBar progressBar;
    Button select_audio;
    public TextView audio_name;
    String final_uri="";
    public WebAudioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_web_audio, container, false);
        init(v);

        soundRangeBar = v.findViewById(R.id.soundrange);
        soundRangeBar.animate();

        soundRangeBar.setOnRangeListener(new SoundRangeBar.OnRangeListener() {
            @Override
            public void onRangeStart(long start, long max) {
                Log.i("values3333", String.valueOf(start) + "--" + String.valueOf(max));
                audio_start = start;
                if (mediaPlayer != null) {
                    mediaPlayer.seekTo((int) start * 1000);
                    audio_start = (int) start * 1000;
                   /* mediaPlayer.reset();
                    mediaPlayer*/
                }
            }

        });

        select_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListSoundWebActivity activity=(ListSoundWebActivity)getActivity();
                activity.trimAndSave(audio_name.getText().toString(),final_uri, String.valueOf(audio_start));
            }
        });
        soundRangeBar.setSoundEffectsEnabled(true);
        return v;
    }




    public void setrange(long vi) {
        soundRangeBar.setVisibility(View.GONE);
        soundRangeBar.setMax(vi / 1000);
        soundRangeBar.setVisibility(View.VISIBLE);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getAudios();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mediaPlayer.stop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mediaPlayer.stop();
    }

    private void init(View v) {
        mediaPlayer = new MediaPlayer();
        recyc = v.findViewById(R.id.recyclerview);
        audio_select = v.findViewById(R.id.audio_select);
        //recyc.setLayoutManager(new GridLayoutManager(this, 3));
        progress_bar = v.findViewById(R.id.progress_bar);

        recyc.setLayoutManager(new LinearLayoutManager(getActivity()));

        select_audio=v.findViewById(R.id.select_audio);
        audio_name=v.findViewById(R.id.audio_name);
    }

    private void setAudios(String resp) {
        List<GalleryVideoItem> items = new ArrayList<>();
        try {
            JSONArray jsonArray=new JSONArray(resp);
            for(int i=0;i<jsonArray.length();i++){
                JSONObject object=jsonArray.getJSONObject(i);
                int index = object.getString("mp3").lastIndexOf("/");
                String fileName = object.getString("mp3").substring(index + 1);
                GalleryVideoItem item = new GalleryVideoItem(object.getString("mp3"), fileName, null);
                items.add(item);
            }
        } catch (JSONException e) {
            Log.i("4rffff",e.getMessage());
            e.printStackTrace();
        }




        adapter = new AudioListAdapterWeb(items, WebAudioFragment.this);
        recyc.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        // progress_bar.setVisibility(View.GONE);

    }

    private void getAudios() {

        StringRequest request = new StringRequest(Request.Method.POST, "https://loca-toca.com/Api/list_of_mp3", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("final_rre33esponse", response.toString());
                setAudios(response);
                // open_cart_main_layout.setVisibility(View.VISIBLE);
                // current_order_status.setText("");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // open_cart_main_layout.setVisibility(View.GONE);
                //Toast.makeText(getApplicationContext(), "Unable to connect to server..", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                Log.i("ythuyh", params.toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //return super.getHeaders();
                Map<String,String> params=new HashMap<>();
                params.put("x-fm-api-key","toca@loca");
                params.put("signature","loca-toca@123");
                return  params;
            }
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                // System.out.println(response.headers.get("Set-Cookie"));
                Log.i("res6666645",response.headers.toString());
                Log.i("res6666645", new String(response.data));

              /*  boolean res= generateHashWithHmac256(new String(response.data),"loca-toca@123",response.headers.get("signature"));
                if(!res){
                    return Response.error(new VolleyError("UnAuthorised response"));
                }else {

                }*/
                return super.parseNetworkResponse(response);
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(500000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //queue.add(request);
        VolleySingleton.getInstance(getActivity().getBaseContext()).addToRequestQueue(request);

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

    public void downloadAndPlay(String url,ProgressBar progressBr) {
        progress_bar=progressBr;
        progressBr.setVisibility(View.VISIBLE);
        String dir=getActivity().getExternalCacheDir().toString()+"/temp/file.mp3";
        File dirf = new File(getActivity().getExternalCacheDir().toString()+"/temp/");
        if (!dirf.exists()) {
            dirf.mkdir();
        }
        new DownloadFile().execute(url,dir);
    }


    private class DownloadFile extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... url) {
            int count;
            try {
                URL urld = new URL(url[0]);
                String destination=url[1];
                URLConnection conexion = urld.openConnection();
                conexion.connect();
                // this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = conexion.getContentLength();

                // downlod the file
                InputStream input = new BufferedInputStream(urld.openStream());
                OutputStream output = new FileOutputStream(destination);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    publishProgress((int) (total * 100 / lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                Log.i("ui88888",e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i("dine33","done");
            if(progress_bar!=null){
                progress_bar.setVisibility(View.GONE);
            }
            playSound();
        }
    }

    private void playSound(){
        String uri=getActivity().getExternalCacheDir().toString()+"/temp/file.mp3";
        final_uri=uri;
        Uri myUri = Uri.parse(uri);
        try {
            mediaPlayer.stop();
            mediaPlayer.reset();


            mediaPlayer.setDataSource(getActivity(),myUri);
            setrange(getVideoLength(uri));
        } catch (IOException e) {
            e.printStackTrace();
        }catch (IllegalStateException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (IllegalStateException e) {
            e.printStackTrace();
        }
        mediaPlayer.seekTo(2000);
        mediaPlayer.start();
        audio_select.setVisibility(View.VISIBLE);

    }

    private long  getVideoLength(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInmillisec = Long.parseLong( time );
        long duration = timeInmillisec / 1000;
        long hours = duration / 3600;
        long minutes = (duration - hours * 3600) / 60;
        long seconds = duration - (hours * 3600 + minutes * 60);
        return timeInmillisec;
    }
}
