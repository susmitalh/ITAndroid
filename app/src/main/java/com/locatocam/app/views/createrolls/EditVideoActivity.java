package com.locatocam.app.views.createrolls;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.locatocam.app.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/*import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;*/

public class EditVideoActivity extends AppCompatActivity {

    private static  String root ;
    private static  String app_folder ;
    String url="";
    TextView tv;
    String bitmapov;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_video_activity);
        String dir=getExternalCacheDir().toString()+"/";
        root=getExternalCacheDir().toString();
        app_folder= root + "/GFG/";

        url=getIntent().getStringExtra("video_url");

        tv=findViewById(R.id.textdata);
        tv.setDrawingCacheEnabled(true);
        tv.setDrawingCacheBackgroundColor(Color.parseColor("#0AFFFFFF"));
        tv.buildDrawingCache();

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms

                tv.buildDrawingCache();
                Bitmap x=tv.getDrawingCache();
                bitmapov=persistImage(x,"test").getPath();
                // String realpath=getRealPathFromURI(Uri.parse(url));
                try {
                    reverse(1000, 5 * 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(EditVideoActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, 100);

      /*  File file=new File(dir+"output.mp4");
        if(!file.exists()){
            file.mkdir();
        }*/


     /*   FFmpeg ffmpeg = FFmpeg.getInstance(this);
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.i("ffmp555","start");
                }

                @Override
                public void onFailure() {
                    Log.i("ffmp555","error");
                }

                @Override
                public void onSuccess() {
                    Log.i("ffmp555","succes");


                }

                @Override
                public void onFinish() {}
            });
        } catch (FFmpegNotSupportedException e) {
            Log.i("ffmp555","error");
            // Handle if FFmpeg is not supported by device
        }


        Needle.onBackgroundThread().execute(new Runnable() {
            @Override
            public void run() {
               // String[] complexCommand = new String[]{"ffmpeg -i "+url+" -i audio.wav -c:v copy -c:a aac "+file.getAbsolutePath()};

                String[] complexCommand = {"ffmpeg", "-i",  url,  "-vf", "drawtext=text='SiteName.local': fontsize=18: fontcolor=white: x=10:y=h-th-10", "-acodec", "copy", "-y",  file.getAbsolutePath()};
                try {
                    ffmpeg.execute(complexCommand, new ExecuteBinaryResponseHandler() {

                        @Override
                        public void onStart() {
                            Log.i("rfgggg","started excicution");
                        }

                        @Override
                        public void onProgress(String message) {}

                        @Override
                        public void onFailure(String message) {
                            Log.i("rfgggg","Failed "+message);
                        }

                        @Override
                        public void onSuccess(String message) {
                            Log.i("rfgggg",message);
                        }

                        @Override
                        public void onFinish() {}
                    });
                } catch (FFmpegCommandAlreadyRunningException e) {
                    e.printStackTrace();
                }
            }
        });*/


    }

    private  File persistImage(Bitmap bitmap, String name) {
        File filesDir = getExternalCacheDir();
        File imageFile = new File(filesDir, name + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e("lo999999", "Error writing bitmap", e);
        }
        return imageFile;
    }
    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }


    // Method for reversing the video
    // The below code is same as above only the command is changed.
    private void reverse(int startMs, int endMs) throws Exception {
        String filePrefix = "reverse";
        String fileExtn = ".mp4";

        final String filePath;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            ContentValues valuesvideos = new ContentValues();
            valuesvideos.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/" + "Folder");
            valuesvideos.put(MediaStore.Video.Media.TITLE, filePrefix + System.currentTimeMillis());
            valuesvideos.put(MediaStore.Video.Media.DISPLAY_NAME, filePrefix + System.currentTimeMillis() + fileExtn);
            valuesvideos.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
            valuesvideos.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
            valuesvideos.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
            Uri uri = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, valuesvideos);
            File file = FileUtils.getFileFromUri(this, uri);
            filePath = file.getAbsolutePath();

        } else {
            filePrefix = "reverse";
            fileExtn = ".mp4";
            File dest = new File(new File(app_folder), filePrefix + fileExtn);
            int fileNo = 0;
            while (dest.exists()) {
                fileNo++;
                dest = new File(new File(app_folder), filePrefix + fileNo + fileExtn);
            }
            filePath = dest.getAbsolutePath();
        }

       // String complexcmd="-y -i " + url + " -filter_complex [0:v]trim=0:" + endMs / 1000 + ",setpts=PTS-STARTPTS[v1];[0:v]trim=" + startMs / 1000 + ":" + endMs / 1000 + ",reverse,setpts=PTS-STARTPTS[v2];[0:v]trim=" + (startMs / 1000) + ",setpts=PTS-STARTPTS[v3];[v1][v2][v3]concat=n=3:v=1 " + "-b:v 2097k -vcodec mpeg4 -crf 0 -preset superfast " + filePath;

        //String complexcmd="-i " + url + "  -vf "+"drawtext=fontfile='\\fonts\\roboto_black.ttf' text='My text starting at 640x360':x=640:y=360:fontsize=24:fontcolor=white "\"" -c:a copy "+filePath;

        //Font urFontName = FontFactory.getFont("assets/fonts/arial.ttf",BaseFont.IDENTITY_H,BaseFont.EMBEDDED);
       // String fontpat="file:///android_asset/fonts/roboto_black.ttf";
        /*String complexcmd="-i \"" + url + "\"" +
                " -vf \"drawtext=text='Placetexthere':x=10:y=H-th-10:\n" +
                "               fontfile=/system/fonts/roboto_black.ttf:fontsize=12:fontcolor=white:\n" +
                "               shadowcolor=black:shadowx=5:shadowy=5\" "
                +filePath;*/

       // String complexcmd="-i  \"" + url + "\"  -i "+bitmapov+" -filter_complex 'overlay=10:main_h-overlay_h-10' "+filePath;
       /* int x=(int)tv.getX();
        int y=(int)tv.getY();
        String complexcmd="-i  \"" + url + "\"  -i "+bitmapov+" -filter_complex 'overlay=x=(main_w-overlay_w):y=(main_h-overlay_h)' "+filePath;

    //   String complexcmd="-i \""+ url +"\" -vf reverse "+filePath;
        Log.i("cmhf666",String.valueOf(x)+"-"+String.valueOf(y));
        long executionId = FFmpeg.executeAsync(complexcmd, new ExecuteCallback() {
            @Override
            public void apply(final long executionId, final int returnCode) {
                if (returnCode == RETURN_CODE_SUCCESS) {
                    Log.i(Config.TAG, Uri.parse(filePath).toString());
                    *//*videoView.setVideoURI(Uri.parse(filePath));
                    video_url = filePath;
                    videoView.start();
                    progressDialog.dismiss();*//*
                } else if (returnCode == RETURN_CODE_CANCEL) {
                    Log.i(Config.TAG, "Async command execution cancelled by user.");
                } else {
                    Log.i(Config.TAG, String.format("Async command execution failed with returnCode=%d.", returnCode));
                }
            }
        });*/
    }
}
