package com.locatocam.app.views.ceratepost;


import static com.locatocam.app.security.ResponseVerify.generateHashWithHmac256;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.anandwana001.ogtagparser.LinkSourceContent;
import com.anandwana001.ogtagparser.LinkViewCallback;
import com.anandwana001.ogtagparser.OgTagParser;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.ByteArrayBuffer;
import com.locatocam.app.R;
import com.locatocam.app.adapter.OtherUserTitleAdapter;
import com.locatocam.app.custom.VolleyMultipartRequest;
import com.locatocam.app.custom.VolleySingleton;
import com.locatocam.app.security.SharedPrefEnc;
import com.locatocam.app.utils.AppHelper;
import com.locatocam.app.utils.ItemData;
import com.locatocam.app.views.custom.CustomSearchSpinner;
import com.locatocam.app.views.custom.imageVideoPicker.ListGalleryImVdoActivity;
import com.locatocam.app.views.home.HomeFragment;
import com.locatocam.app.views.home.OtherProfileWithFeedFragment;
import com.locatocam.app.views.home.test.SimpleAdapter;
import com.locatocam.app.views.home.test.SimpleAdapterOtherprofile;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import io.umehara.ogmapper.OgMapper;
import io.umehara.ogmapper.domain.OgTags;
import io.umehara.ogmapper.jsoup.JsoupOgMapperFactory;
import needle.Needle;

public class UploadPostmanual extends AppCompatActivity {

    TextView selected_type;

    EditText headline;
    EditText sub_header;
    EditText description;
    EditText select_brand;
    EditText select_items;
    EditText choose_button;

    Button submit;
    RelativeLayout progress;

    String input_type = "", userId, postId, video, thumbnail;
    Uri upload_uri;
    ImageView upload_thuimb;
    VideoView videoview;
    boolean _istext = false;
    ConstraintLayout videowrapper;
    String link_open = "";
    ImageButton back;
    EditText paste_link;
    Button choose_file;
    String otherUser="";
    int IMAGE_PICKER_SELECT = 114, position;

    ProgressBar og_loader;
    String mandatory = "0";
    TextView brand_optional_tag;
    ImageButton pause_play;

    LinearLayout admin_content;

    boolean brandSelectionMandatory = false;
    String storagePermission[];
    private static final int STORAGE_REQUEST = 200;

    private void init() {
        admin_content = findViewById(R.id.admin_content);
        selected_type = findViewById(R.id.selected_type);
        headline = findViewById(R.id.headline);
        sub_header = findViewById(R.id.sub_header);
        description = findViewById(R.id.description);
        select_brand = findViewById(R.id.select_brand);
        select_items = findViewById(R.id.select_items);
        choose_button = findViewById(R.id.choose_button);
        brand_optional_tag = findViewById(R.id.brand_optional_tag);
        videowrapper = findViewById(R.id.videowrapper);
        submit = findViewById(R.id.submit);
        progress = findViewById(R.id.progress);
        upload_thuimb = findViewById(R.id.upload_thuimb);
        videoview = findViewById(R.id.videoview);
        back = findViewById(R.id.back);
        paste_link = findViewById(R.id.paste_link);
        choose_file = findViewById(R.id.choose_file);
        og_loader = findViewById(R.id.og_loader);
        pause_play = findViewById(R.id.pause_play);
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST);
    }

    private Boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case STORAGE_REQUEST: {
                if (grantResults.length > 0) {
                    boolean writeStorageaccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageaccepted) {
                        Intent intent1 = new Intent(UploadPostmanual.this, ListGalleryImVdoActivity.class);
                        startActivityForResult(intent1, IMAGE_PICKER_SELECT);
                    } else {
                        Toast.makeText(this, "Please Enable Storage Permissions", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        video = getIntent().getStringExtra("video");
        thumbnail = getIntent().getStringExtra("thumbnail");
        String headlineTxt = getIntent().getStringExtra("headline");
        String subHeadlineTxt = getIntent().getStringExtra("subHeadline");
        String descriptionTxt = getIntent().getStringExtra("description");
        userId = getIntent().getStringExtra("userId");
        postId = getIntent().getStringExtra("postId");
        otherUser = getIntent().getStringExtra("otherUser");
        position = getIntent().getIntExtra("position", 0);
        String getFile_extension_type = getIntent().getStringExtra("getFile_extension_type");
        String theme = "light";
        if (!theme.equals("dark")) {
            setContentView(R.layout.activity_upload_post_manuel_light);
        } else {
            setContentView(R.layout.activity_upload_post_manuel);
        }

        init();
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();


        checkBrandMandatory();
        // Log.i("hbnntgnn1111",intent.getStringExtra(Intent.EXTRA_TEXT));
        // Log.i("hbnntgnn1111",intent.getParcelableExtra(Intent.EXTRA_STREAM));
        if (userId != null) {
//            if (video.contains(".mp4")) {
            if (!getFile_extension_type.equals("image")) {

                videoview.setVideoURI(Uri.parse(video));
                videoview.start();
                og_loader.setVisibility(View.VISIBLE);
                videowrapper.setVisibility(View.VISIBLE);

                videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {

                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                og_loader.setVisibility(View.GONE);
                                videoview.pause();
                                pause_play.setImageResource(R.drawable.ic_play_arrow_dark_24dp);

                            }
                        }, 100);

                    }
                });

            } else {

            Glide.with(this).load(video).into(upload_thuimb);
            upload_thuimb.setVisibility(View.VISIBLE);
            }

            headline.setText(headlineTxt);
            sub_header.setText(subHeadlineTxt);
            description.setText(descriptionTxt);


        }
        if (videoview.isPlaying()) {
            pause_play.setImageResource(R.drawable.ic_pause_dark_24dp);
        } else {
            pause_play.setImageResource(R.drawable.ic_play_arrow_dark_24dp);
        }

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            Log.i("hbnnnn1111", type);
            if (type.startsWith("video/")) {
                selected_type.setText("Video selected");
                getmedia(intent); // Handle text being sent
                input_type = "video";
                Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (imageUri == null) {
                    imageUri = (Uri) getIntent().getExtras().get(Intent.EXTRA_STREAM);


                }
                Log.i("ju78888", imageUri.toString());
                getThumb(imageUri);
                upload_thuimb.setVisibility(View.GONE);
                videoview.setVisibility(View.VISIBLE);
//                videoview.setZOrderOnTop(true);
                videoview.setVideoURI(imageUri);
                videoview.start();

                videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                videoview.pause();
                                pause_play.setImageResource(R.drawable.ic_play_arrow_dark_24dp);
                            }
                        }, 100);

                    }
                });

                // upload_thuimb.setImageResource(R.drawable.play_thumb);
            } else if (type.startsWith("image/")) {
                Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
                upload_thuimb.setImageURI(imageUri);

                selected_type.setText("Image selected");
                getmedia(intent); // Handle single image being sent
                input_type = "image";
                upload_thuimb.setVisibility(View.VISIBLE);
                videoview.setVisibility(View.GONE);
            } else if (type.startsWith("text/")) {
                _istext = true;
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                description.setText(sharedText);
                upload_thuimb.setVisibility(View.VISIBLE);
                videoview.setVisibility(View.GONE);
                link_open = sharedText;


                new OgTagParser().execute(sharedText, new LinkViewCallback() {
                    @Override
                    public void onBeforeLoading() {

                    }

                    @Override
                    public void onAfterLoading(@NotNull LinkSourceContent linkSourceContent) {
                        Log.i("tgfff5555", linkSourceContent.getImages());
                        Log.e("TAG", "onAfterLoddading: "+linkSourceContent.getOgUrl() );
                       /* Glide.with(UploadPostmanual.this)
                                .load(linkSourceContent.getImages())
                                .error(R.drawable.ic_facebook)
                                .into(upload_thuimb);*/
                        videowrapper.setVisibility(View.VISIBLE);
                        videoview.setVideoURI(Uri.parse(linkSourceContent.getOgUrl()));

                        Log.i("tgfff5555", "desc" + linkSourceContent.getOgDescription());
                        Log.i("tgfff5555", "site" + linkSourceContent.getOgSiteName());
                        Log.i("tgfff5555", "title" + linkSourceContent.getOgTitle());
                        sub_header.setText(linkSourceContent.getOgTitle());
                    }
                });


            }
        }

        paste_link.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                videowrapper.setVisibility(View.VISIBLE);
                _istext = true;
                String sharedText = paste_link.getText().toString();
                description.setText(sharedText);
                upload_thuimb.setVisibility(View.GONE);
                videoview.setVisibility(View.GONE);
                link_open = sharedText;
                og_loader.setVisibility(View.VISIBLE);

               /* new OgTagParser().execute(paste_link.getText().toString(), new LinkViewCallback() {
                    @Override
                    public void onBeforeLoading() {

                    }

                    @Override
                    public void onAfterLoading(@NotNull LinkSourceContent linkSourceContent) {
                        Log.i("tgfff5555",linkSourceContent.getImages());
                        Glide.with(UploadPostmanual.this)
                                .load(linkSourceContent.getImages())
                                .error(R.drawable.ic_facebook)
                                .into(upload_thuimb);

                        Log.i("tgfff5555","desc"+linkSourceContent.getOgUrl());
                        Log.i("tgfff5555","site"+linkSourceContent.getOgSiteName());
                        Log.i("tgfff5555","title"+linkSourceContent.getOgTitle());
                        sub_header.setText(linkSourceContent.getOgTitle());
                        description.setText(paste_link.getText().toString());
                        og_loader.setVisibility(View.GONE);
                        upload_thuimb.setVisibility(View.VISIBLE);
                    }
                });*/

                Needle.onBackgroundThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        OgMapper ogMapper = new JsoupOgMapperFactory().build();
                        String urlForTheRockOnIMDB = sharedText;
                        try {
                            OgTags ogTags = ogMapper.process(new URL(urlForTheRockOnIMDB));
                            Log.i("ottttt5555", ogTags.getTitle().toString());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Glide.with(UploadPostmanual.this)
                                            .load(ogTags.getImage())
                                            .error(R.drawable.ic_facebook)
                                            .into(upload_thuimb);

                                    sub_header.setText(ogTags.getTitle());
                                    description.setText(paste_link.getText().toString());
                                    og_loader.setVisibility(View.GONE);
                                    upload_thuimb.setVisibility(View.VISIBLE);
                                }
                            });
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

                    }
                });


            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAll();

            }
        });

        select_brand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                populateBrands();
                // getBase64Video();
            }
        });


        select_items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (select_brand.getTag().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Select brand first", Toast.LENGTH_LONG).show();
                } else {
                    populateDish(select_brand.getTag().toString());
                    Log.i("rf44444", select_brand.getTag().toString());
                }

            }
        });

        choose_button.setText("Order Now");

        choose_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                populateButton();
            }
        });

        choose_file.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
               /* DazzleOptions dazzleOptions=DazzleOptions.init();
                dazzleOptions.setMaxCount(5);//maximum number of images/videos to be picked
                dazzleOptions.setMaxVideoDuration(20);               //maximum duration for video capture in seconds
                dazzleOptions.setAllowFrontCamera(true);           //allow front camera use
                dazzleOptions.setExcludeVideos(false);

                Dazzle.startPicker(UploadPostmanual.this, dazzleOptions) ;   //this -> context of Activity or Fragment*/
                //startActivityForResult(intent, IMAGE_PICKER_SELECT);

                if (!checkCameraPermission()) {
                    requestStoragePermission();
                } else {
                    Intent intent1 = new Intent(UploadPostmanual.this, ListGalleryImVdoActivity.class);
                    startActivityForResult(intent1, IMAGE_PICKER_SELECT);
                }
            }
        });

        if (theme.equals("dark")) {
            pause_play.setImageResource(R.drawable.ic_pause_black_24dp);
        } else {
            pause_play.setImageResource(R.drawable.ic_pause_dark_24dp);
        }

        pause_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String theme = "light";

                if (videoview.isPlaying()) {
                    videoview.pause();
                    if (theme.equals("dark")) {
                        pause_play.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                    } else {
                        pause_play.setImageResource(R.drawable.ic_play_arrow_dark_24dp);
                    }

                } else {
                    videoview.start();

                    if (theme.equals("dark")) {
                        pause_play.setImageResource(R.drawable.ic_pause_black_24dp);
                    } else {
                        pause_play.setImageResource(R.drawable.ic_pause_dark_24dp);
                    }
                }
            }
        });

        condition();
    }

    private void condition() {
        String isAdmin = SharedPrefEnc.getPref(this, "is_admin");
        String loginType = SharedPrefEnc.getPref(this, "user_type");
        Log.e("TAG", "condition: " + isAdmin + "," + loginType);

        if (isAdmin.equals("1")) {
            if (loginType.equals("company")) {
                brand_optional_tag.setVisibility(View.GONE);
                brandSelectionMandatory = true;
            } else {
                brand_optional_tag.setVisibility(View.VISIBLE);
                brandSelectionMandatory = false;
            }
            admin_content.setVisibility(View.VISIBLE);
        } else {
            admin_content.setVisibility(View.GONE);
        }
    }

    /*private String getRealPathFromURI(Uri contentUri) {
        String result;
        String[] projection = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(this, contentUri, projection, null, null, null);
        Cursor cursor = loader.loadInBackground();
        if(cursor == null){
            result = contentUri.getPath();
        }else {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
            cursor.close();
        }
        return result;
    }*/
    private void getmedia(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

        if (imageUri != null) {
            Log.i("hbnntgnncv1111", imageUri.toString());
            // Update UI to reflect image being shared
            upload_uri = imageUri;
        } else if (getIntent().getExtras().get(Intent.EXTRA_STREAM) != null) {
            upload_uri = (Uri) getIntent().getExtras().get(Intent.EXTRA_STREAM);
        }


    }


    private void validateAll() {
        // postFile();
        if (headline.getText().toString().equals("")) {
            headline.setError("Input value ");
        } else if (sub_header.getText().toString().equals("")) {
            sub_header.setError("Input value ");
        } else if (description.getText().toString().equals("")) {
            description.setError("Input value ");
        } else if (brandSelectionMandatory == true) {
            if (select_brand.getText().toString().equals("")) {
                select_brand.setError("Input value ");
            }
        }
       /* else if(select_items.getText().toString().equals("")){
            select_items.setError("Input value ");
        }*/
        /*else if (choose_button.getText().toString().equals("")) {
            choose_button.setError("Input value ");
        }*/
        else {

            if (mandatory.equals("1")) {
                if (select_brand.getTag().toString().equals("")) {
                    select_brand.setError("Input value ");
                } else {
                    confirmUpload();
                }
            } else {
                confirmUpload();
            }


        }

    }


    private void postFile() {

        Log.i("errrrrrr", "start");
        progress.setVisibility(View.VISIBLE);
        String url;
        if (userId != null) {
            url = "https://loca-toca.com/App/edit_post";
        } else {
            url = "https://loca-toca.com/App/insert_post";
        }
        Log.e("TAG", "postFileUrl: " + url);
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                progress.setVisibility(View.GONE);
                if (userId != null) {
                    HomeFragment.instance.feedApi();

                    /*Log.e("TAG", "onResponseSS: Edit "+getBase64() );
                    ((SimpleAdapter)(HomeFragment.Companion.getBinding().playerContainer.getAdapter())).mediaList.get(position).setScreenshot(getBase64());
                    ((SimpleAdapter)(HomeFragment.Companion.getBinding().playerContainer.getAdapter())).mediaList.get(position).setHeader(headline.getText().toString());
                    ((SimpleAdapter)(HomeFragment.Companion.getBinding().playerContainer.getAdapter())).notifyDataChanged();*/

                    if (otherUser.equals("true")){
                        ((SimpleAdapterOtherprofile)(OtherProfileWithFeedFragment.Companion.getBinding().playerContainer.getAdapter())).mediaList.remove(position);
                        ((SimpleAdapterOtherprofile)(OtherProfileWithFeedFragment.Companion.getBinding().playerContainer.getAdapter())).notifyItemRemoved(position);
                    }else {
                        ((SimpleAdapter) (HomeFragment.Companion.getBinding().playerContainer.getAdapter())).mediaList.remove(position);
                        ((SimpleAdapter) (HomeFragment.Companion.getBinding().playerContainer.getAdapter())).notifyItemRemoved(position);
                    }
                    Toast.makeText(getApplicationContext(), "Posted succesfully, will be live after approval", Toast.LENGTH_LONG).show();
                    Intent data = new Intent();
                    setResult(RESULT_OK,data);
                    finish();
                }


                Log.i("errrrrrr", resultResponse);
                Toast.makeText(getApplicationContext(), "Posted succesfully, will be live after approval", Toast.LENGTH_LONG).show();
                   /* Intent intent = new Intent(UploadPostmanual.this, UploadPostmanual.class);
                    startActivity(intent);*/
                Intent data = new Intent();
                setResult(RESULT_OK,data);
                finish();
                try {
                    JSONObject result = new JSONObject(resultResponse);


                } catch (JSONException e) {


                    e.getMessage();
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e("TAG", "onErrorResponsedd: " + error.getMessage());
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
                        Log.i("errrrrrr", "js start");
                    }
                }
                Log.i("errrrrrr", errorMessage);
//                Toast.makeText(getApplicationContext(), "Posted succesfully, will be live after approval", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), ""+error.getMessage(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(UploadPostmanual.this, UploadPostmanual.class);
                startActivity(intent);
                finish();
                progress.setVisibility(View.GONE);
                error.printStackTrace();
                error.getMessage();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String mobile = SharedPrefEnc.getPref(UploadPostmanual.this, "mobile");
                if (userId != null) {

                    params.put("user_id", userId);//4
                    params.put("post_id", postId);//1
                    params.put("phone", mobile);//12
                    params.put("screenshot", getBase64());//10
                    params.put("brand_id", select_brand.getTag().toString());//6
                    params.put("brand_name", select_brand.getText().toString());//3
                    params.put("item", select_items.getTag().toString());//7
                    params.put("header", headline.getText().toString());
                    params.put("sub_header", sub_header.getText().toString());//2
                    params.put("description", description.getText().toString());//8
                    params.put("button", choose_button.getText().toString());//5
//                params.put("button", choose_button.getTag().toString());
                    params.put("social", _istext ? "1" : "0");//11
                    params.put("link", link_open);//9
                    Log.e("paramsxxxc", params.toString());
                    Log.e("TAG", "getParams: " + headline.toString());
                } else {

                    params.put("phone", mobile);
                    params.put("screenshot", getBase64());
                    params.put("brand_id", select_brand.getTag().toString());
                    params.put("brand_name", select_brand.getText().toString());
                    params.put("item", select_items.getTag().toString());
                    params.put("header", headline.getText().toString());
                    params.put("sub_header", sub_header.getText().toString());
                    params.put("description", description.getText().toString());
                    params.put("button", choose_button.getText().toString());
//                params.put("button", choose_button.getTag().toString());
                    params.put("social", _istext ? "1" : "0");
                    params.put("link", link_open);
                    Log.i("paramsxxxc", params.toString());
                    Log.e("TAG", "getParams: " + headline.toString());
                }
                //params.put("id", q_id);

                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();

                try {

                    if (upload_uri != null) {
                        //InputStream iStream =   getContentResolver().openInputStream(upload_uri);
                        File myFile = new File(upload_uri.getPath());
                        FileInputStream inStream = new FileInputStream(myFile);

                        byte[] inputData = getBytes(inStream);
                        Random random = new Random();
                        int nm = random.nextInt(100000);
                        if (input_type.equals("video")) {
                            params.put("file", new DataPart("video" + String.valueOf(nm) + ".mp4", inputData, "video/mp4"));
                        } else {
                            if (_istext) {

                                params.put("file", new DataPart("image" + String.valueOf(nm) + ".jpeg", AppHelper.getFileDataFromDrawable(getBaseContext(), upload_thuimb.getDrawable()), "image/jpeg"));

                            } else {
                                params.put("file", new DataPart("image" + String.valueOf(nm) + ".jpeg", inputData, "image/jpeg"));
                            }
                        }
                    } else {
                        try {
                            if (video.contains(".mp4")) {
                                input_type = "video";
                            } else {
                                input_type = "image";
                            }
                            URL imageUrl = new URL(video);
                            URLConnection ucon = imageUrl.openConnection();

                            InputStream is = ucon.getInputStream();
                            BufferedInputStream bis = new BufferedInputStream(is);

                            ByteArrayBuffer baf = new ByteArrayBuffer(500);
                            int current = 0;
                            while ((current = bis.read()) != -1) {
                                baf.append((byte) current);
                            }
                            byte[] inputData = baf.toByteArray();

                            Random random = new Random();
                            int nm = random.nextInt(100000);
                            if (input_type.equals("video")) {
                                params.put("file", new DataPart("video" + String.valueOf(nm) + ".mp4", inputData, "video/mp4"));
                            } else {
                                if (_istext) {
                                    params.put("file", new DataPart("image" + String.valueOf(nm) + ".jpeg", AppHelper.getFileDataFromDrawable(getBaseContext(), upload_thuimb.getDrawable()), "image/jpeg"));

                                } else {
                                    params.put("file", new DataPart("image" + String.valueOf(nm) + ".jpeg", inputData, "image/jpeg"));
                                }
                            }
                        } catch (Exception e) {
                            Log.d("ImageManager", "Error: " + e.toString());
                        }
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
    }

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

    private void populateBrands() {

        String url = "https://loca-toca.com/Api/get_brands";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("final_response3", response.toString());

                final ArrayList<ItemData> list = new ArrayList<>();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("brands");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        list.add(new ItemData(obj.getString("id"), obj.getString("name")));
                    }

                    if (!mandatory.equals("1")) {
                        list.add(0, new ItemData("none", "None"));
                    }

                    Intent intent = new Intent(UploadPostmanual.this, CustomSearchSpinner.class);
                    intent.putExtra("data_list", list);
                    intent.putExtra("req_code", 14);
                    intent.putExtra("desc_text", "Brand");
                    startActivityForResult(intent, 14);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(UploadPostmanual.this, "Unable to connect to server..", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String mobile = SharedPrefEnc.getPref(UploadPostmanual.this, "mobile");
                params.put("phone", mobile);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //return super.getHeaders();
                Map<String, String> params = new HashMap<>();
                params.put("x-fm-api-key", "toca@loca");
                params.put("signature", "loca-toca@123");
                return params;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                // System.out.println(response.headers.get("Set-Cookie"));
                Log.i("res6666645", response.headers.toString());
                Log.i("res6666645", new String(response.data));

                boolean res = generateHashWithHmac256(new String(response.data), "loca-toca@123", response.headers.get("signature"));
                if (!res) {
                    return Response.error(new VolleyError("UnAuthorised response"));
                } else {
                    return super.parseNetworkResponse(response);
                }
            }
        };

        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(request);

    }

    private void populateDish(String brand) {

        String url = "https://loca-toca.com/Api/get_dish";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("final_response3", response.toString());

                final ArrayList<ItemData> list = new ArrayList<>();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        list.add(new ItemData(obj.getString("dish_id"), obj.getString("dish_name")));
                    }
                    list.add(new ItemData("0", "No particalar item"));
                    Intent intent = new Intent(UploadPostmanual.this, CustomSearchSpinner.class);
                    intent.putExtra("data_list", list);
                    intent.putExtra("req_code", 15);
                    intent.putExtra("desc_text", "item");
                    startActivityForResult(intent, 15);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(UploadPostmanual.this, "Unable to connect to server..", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //String mobile=getSharedPreferences("userinfo", Context.MODE_PRIVATE).getString("mobile", "");
                params.put("brand", brand);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //return super.getHeaders();
                Map<String, String> params = new HashMap<>();
                params.put("x-fm-api-key", "toca@loca");
                params.put("signature", "loca-toca@123");
                return params;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                // System.out.println(response.headers.get("Set-Cookie"));
                Log.i("res6666645", response.headers.toString());
                Log.i("res6666645", new String(response.data));

                boolean res = generateHashWithHmac256(new String(response.data), "loca-toca@123", response.headers.get("signature"));
                if (!res) {
                    return Response.error(new VolleyError("UnAuthorised response"));
                } else {
                    return super.parseNetworkResponse(response);
                }
            }
        };

        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(request);

    }

    private void populateButton() {

        String url = "https://loca-toca.com/Api/get_order_type";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("final_response3", response.toString());

                final ArrayList<ItemData> list = new ArrayList<>();
                try {
                    //JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        list.add(new ItemData(obj.getString("id"), obj.getString("name")));
                    }

                    Intent intent = new Intent(UploadPostmanual.this, CustomSearchSpinner.class);
                    intent.putExtra("data_list", list);
                    intent.putExtra("req_code", 16);
                    intent.putExtra("desc_text", "Button");
                    startActivityForResult(intent, 16);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(UploadPostmanual.this, "Unable to connect to server..", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //String mobile=getSharedPreferences("userinfo", Context.MODE_PRIVATE).getString("mobile", "");
                // params.put("brand", brand);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //return super.getHeaders();
                Map<String, String> params = new HashMap<>();
                params.put("x-fm-api-key", "toca@loca");
                params.put("signature", "loca-toca@123");
                return params;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                // System.out.println(response.headers.get("Set-Cookie"));
                Log.i("res6666645", response.headers.toString());
                Log.i("res6666645", new String(response.data));

                boolean res = generateHashWithHmac256(new String(response.data), "loca-toca@123", response.headers.get("signature"));
                if (!res) {
                    return Response.error(new VolleyError("UnAuthorised response"));
                } else {
                    return super.parseNetworkResponse(response);
                }
            }
        };

        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(request);

    }


    private void checkBrandMandatory() {

        String url = "https://loca-toca.com/Api/get_brand_filter_mandatory";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("final_redcsponse3", response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    mandatory = jsonObject.getString("status");

                    if (jsonObject.getString("is_admin").equals("0")) {
//                        admin_content.setVisibility(View.GONE);
                    } else {
//                        admin_content.setVisibility(View.VISIBLE);
                    }

                    if (mandatory.equals("1")) {
//                        brand_optional_tag.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(UploadPostmanual.this, "Unable to connect to server..", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String mobile = SharedPrefEnc.getPref(UploadPostmanual.this, "mobile");
                params.put("phone", mobile);
                //9945888855
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //return super.getHeaders();
                Map<String, String> params = new HashMap<>();
                params.put("x-fm-api-key", "toca@loca");
                params.put("signature", "loca-toca@123");
                return params;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                // System.out.println(response.headers.get("Set-Cookie"));
                Log.i("res6666645", response.headers.toString());
                Log.i("res6666645", new String(response.data));

                boolean res = generateHashWithHmac256(new String(response.data), "loca-toca@123", response.headers.get("signature"));
                if (!res) {
                    return Response.error(new VolleyError("UnAuthorised response"));
                } else {
                    return super.parseNetworkResponse(response);
                }
            }
        };

        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(request);

    }

    private String getBase64() {
        String encodedImage="";
        BitmapDrawable drawable = (BitmapDrawable) upload_thuimb.getDrawable();
        if (drawable != null) {
            Bitmap bitmap = drawable.getBitmap();
            //Bitmap bm = BitmapFactory.decodeFile("/path/to/image.jpg");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the bitmap object
            byte[] b = baos.toByteArray();
            encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

            Log.i("jhikkkk99", encodedImage);
            return encodedImage;
        } else {
            try {
                URL url = new URL(thumbnail);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);


                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the bitmap object
                byte[] b = baos.toByteArray();
                encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return encodedImage;
    }

    private Bitmap getBase64Video() {
        Bitmap b = Bitmap.createBitmap(videowrapper.getWidth(), videowrapper.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        videowrapper.layout(videowrapper.getLeft(), videowrapper.getTop(), videowrapper.getRight(), videowrapper.getBottom());
        videowrapper.draw(c);
        return b;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 14) {
            if (resultCode == 14) {
                select_brand.setTag(data.getStringExtra("result_id").toString());
                select_brand.setText(data.getStringExtra("result_value").toString());
                select_brand.setError(null);
                brandSelectionMandatory = false;

            }
        } else if (requestCode == 15) {
            if (resultCode == 15) {
                select_items.setTag(data.getStringExtra("result_id").toString());
                select_items.setText(data.getStringExtra("result_value").toString());
            }
        } else if (requestCode == 16) {
            if (resultCode == 16) {
                choose_button.setTag(data.getStringExtra("result_id").toString());
                choose_button.setText(data.getStringExtra("result_value").toString());
//                choose_button.setError(null);

            }
        }

        if (resultCode == RESULT_OK && requestCode == IMAGE_PICKER_SELECT) {
            Uri selectedMediaUri = Uri.parse(data.getStringExtra("uri"));
            String tp = data.getStringExtra("type");

            if (tp.contains("video")) {
                selected_type.setText("Video selected");
                if (selectedMediaUri != null) {
                    Log.i("hbnntgnncv1111", selectedMediaUri.toString());
                    // Update UI to reflect image being shared
                    upload_uri = selectedMediaUri;
                }
                input_type = "video";
                Uri imageUri = selectedMediaUri;
                Log.i("ju78888", imageUri.toString());
                getThumb(imageUri);
                videowrapper.setVisibility(View.VISIBLE);
                upload_thuimb.setVisibility(View.GONE);
                videoview.setVisibility(View.VISIBLE);
//                videoview.setZOrderOnTop(true);
                videoview.setVideoURI(imageUri);
                videoview.start();

                videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                videoview.pause();
                                pause_play.setImageResource(R.drawable.ic_play_arrow_dark_24dp);
                            }
                        }, 100);

                    }
                });
            } else if (tp.contains("image")) {
                //handle video
                Uri imageUri = selectedMediaUri;
                upload_thuimb.setImageURI(imageUri);

                selected_type.setText("Image selected");
                if (selectedMediaUri != null) {
                    Log.i("hbnntgnncv1111", selectedMediaUri.toString());
                    // Update UI to reflect image being shared
                    upload_uri = selectedMediaUri;
                }
                input_type = "image";
                upload_thuimb.setVisibility(View.VISIBLE);
                videoview.setVisibility(View.GONE);
            }
        }
    }


    private void getThumb(Uri uric) {

        try {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();

            mediaMetadataRetriever.setDataSource(this, uric);
            Bitmap bmFrame = mediaMetadataRetriever.getFrameAtTime(1000); //unit in microsecond
            upload_thuimb.setImageBitmap(bmFrame);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }


    }

    public Uri myUri(Uri originalUri) {
        Uri returnedUri = null;
        if (originalUri.getScheme() == null) {
            returnedUri = Uri.fromFile(new File(originalUri.getPath()));
            // or you can just do -->
            // returnedUri = Uri.parse("file://"+camUri.getPath());
        } else {
            returnedUri = originalUri;
        }
        return returnedUri;
    }


    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            ;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    private void confirmUpload() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popupuploadconfirm);
        dialog.setCanceledOnTouchOutside(true);

        Button yes = (Button) dialog.findViewById(R.id.yes);
        Button no = (Button) dialog.findViewById(R.id.no);

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });


        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postFile();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static Uri getContentUri(Context context, String absPath, String type) {

        if (type.equals("image")) {
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    , new String[]{MediaStore.Images.Media._ID}
                    , MediaStore.Images.Media.DATA + "=? "
                    , new String[]{absPath}, null);

            if (cursor != null && cursor.moveToFirst()) {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Integer.toString(id));

            } else if (!absPath.isEmpty()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, absPath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        } else {
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    , new String[]{MediaStore.Video.Media._ID}
                    , MediaStore.Images.Media.DATA + "=? "
                    , new String[]{absPath}, null);

            if (cursor != null && cursor.moveToFirst()) {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                return Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, Integer.toString(id));

            } else if (!absPath.isEmpty()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Video.Media.DATA, absPath);
                return context.getContentResolver().insert(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }

    }
}
