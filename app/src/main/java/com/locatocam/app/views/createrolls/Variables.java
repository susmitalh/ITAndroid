package com.locatocam.app.views.createrolls;

import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Variables {

    public static final String device="android";

    public static int screen_width;
    public static int screen_height;

    public static final String SelectedAudio_AAC ="SelectedAudio.aac";

    public static  String root="";
    //public static final String root2= get.toString();
    public static final String app_folder=root+"/Locatoca/";
    public static final String draft_app_folder=app_folder+"Draft/";



    public static int max_recording_duration=60000;
    public static int min_time_recording=500;
    public static int recording_duration=60000;


    public static String outputfile=app_folder + "output.mp4";
    public static String outputfile2=app_folder + "output2.mp4";
    public static String output_filter_file=app_folder + "output-filtered.mp4";

    public static String gallery_trimed_video=app_folder + "gallery_trimed_video.mp4";
    public static String gallery_resize_video=app_folder + "gallery_resize_video.mp4";



    public static SharedPreferences sharedPreferences;
    public static final String pref_name="pref_name";
    public static final String u_id="u_id";
    public static final String u_name="u_name";
    public static final String u_pic="u_pic";
    public static final String f_name="f_name";
    public static final String l_name="l_name";
    public static final String gender="u_gender";
    public static final String islogin="is_login";
    public static final String device_token="device_token";
    public static final String api_token="api_token";
    public static final String device_id="device_id";
    public static final String uploading_video_thumb="uploading_video_thumb";

    public static String user_id;
    public static String user_name;
    public static String user_pic;



    public static String tag="tictic_";

    public static String Selected_sound_id="null";

    public static  boolean Reload_my_videos=false;
    public static  boolean Reload_my_videos_inner=false;
    public static  boolean Reload_my_likes_inner=false;
    public static  boolean Reload_my_notification=false;


    public static final String gif_firstpart="https://media.giphy.com/media/";
    public static final String gif_secondpart="/100w.gif";

    public static final String gif_firstpart_chat="https://media.giphy.com/media/";
    public static final String gif_secondpart_chat="/200w.gif";


    public static final SimpleDateFormat df =
            new SimpleDateFormat("dd-MM-yyyy HH:mm:ssZZ", Locale.ENGLISH);

    public static final SimpleDateFormat df2 =
            new SimpleDateFormat("dd-MM-yyyy HH:mmZZ", Locale.ENGLISH);





    public final static int permission_camera_code=786;
    public final static int permission_write_data=788;
    public final static int permission_Read_data=789;
    public final static int permission_Recording_audio=790;
    public final static int Pick_video_from_gallery=791;




}
