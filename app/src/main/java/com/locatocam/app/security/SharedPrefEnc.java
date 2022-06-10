package com.locatocam.app.security;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefEnc {
    public static void setPref(String key, String value,Context context) {
        String encodedval= Encode.encode(value);
        SharedPreferences sharedPreferences = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, encodedval);
        editor.apply();
    }

    public static String getPref(Context context,String key){
        String value=context.getSharedPreferences("userinfo", Context.MODE_PRIVATE).getString(key, "");
        if(value.equals("")){
            return  "";
        }else {
            return Decode.decode(value);
        }
    }

    public SharedPrefEnc getInstance(){
        return this;
    }
}
