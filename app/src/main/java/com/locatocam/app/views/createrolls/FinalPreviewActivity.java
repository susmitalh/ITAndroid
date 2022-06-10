package com.locatocam.app.views.createrolls;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.locatocam.app.R;
import com.locatocam.app.custom.VolleyMultipartRequest;
import com.locatocam.app.custom.VolleySingleton;
import com.locatocam.app.security.SharedPrefEnc;
import com.locatocam.app.views.MainActivity;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class FinalPreviewActivity extends AppCompatActivity implements View.OnClickListener {


    TextView upload;
    String phone="",screenshot="",file_path="",overlay_text="",colour_code="";
    ImageView thumb;
    EditText description;
    ImageButton back;
    VideoView videoview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_preview);
        init();
        Intent intent = getIntent();

        phone=intent.getStringExtra("phone");
        //screenshot=intent.getStringExtra("screenshot");
        file_path=intent.getStringExtra("file_path");
        overlay_text= intent.getStringExtra("overlay_text");
        colour_code=intent.getStringExtra("colour_code");

        screenshot=getBase64();
        byte[] decodedString = Base64.decode(screenshot, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        thumb.setImageBitmap(decodedByte);

        videoview.setVideoURI(Uri.parse(file_path));
        videoview.start();
    }
    private String getBase64(){
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(file_path, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
    private void init(){
        upload=findViewById(R.id.upload);
        thumb=findViewById(R.id.thumnile);
        description=findViewById(R.id.description);
        videoview=findViewById(R.id.videoview);
        upload.setOnClickListener(this);
        back=findViewById(R.id.back);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.upload:
               //postFile();
                confirmUpload();
                break;
            case R.id.back:
               // setResult(RESULT_OK);
                finish();
                break;
        }
    }

    private void postFile() {

        final ProgressDialog progressDialog=new ProgressDialog(FinalPreviewActivity.this);
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
                FinalPreviewActivity.this.setResult(RESULT_OK);
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
                Toast.makeText(getApplicationContext(),"Posted succesfully, will be live after approval",Toast.LENGTH_LONG).show();
                Intent intent=new Intent(FinalPreviewActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                //progress_bar.setVisibility(View.GONE);
                progressDialog.dismiss();
                error.printStackTrace();
                error.getMessage();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String mobile= SharedPrefEnc.getPref(FinalPreviewActivity.this,"mobile");
                params.put("phone", mobile);
                params.put("screenshot", screenshot);
                params.put("overlay_text", overlay_text);
                params.put("overlay_color", colour_code);
                params.put("description", description.getText().toString());

                return params;
            }

            @Override
            protected Map<String, VolleyMultipartRequest.DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();

                try {
                    InputStream targetStream=new FileInputStream(new File(file_path));

                        byte[] inputData = getBytes(targetStream);
                        Random random=new Random();
                        int nm=random.nextInt(100000);

                        params.put("file", new DataPart("video"+String.valueOf(nm)+".mp4", inputData, "video/mp4"));


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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    private void confirmUpload() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popupuploadconfirm);
        dialog.setCanceledOnTouchOutside(false);

        final EditText mobileNo = (EditText) dialog.findViewById(R.id.mobileNo);
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
                dialog.dismiss();
                postFile();
            }
        });
        dialog.show();
    }
}
