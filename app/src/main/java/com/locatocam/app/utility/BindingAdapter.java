package com.locatocam.app.utility;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class BindingAdapter {

    @androidx.databinding.BindingAdapter(value = {"imageUrl", "placeholder"}, requireAll = false)
    public static void loadImage(ImageView view, String imageUrl, Drawable placeholder) {
        if (imageUrl == null || imageUrl.equalsIgnoreCase("")) {
            view.setImageDrawable(placeholder);
        } else {
            Glide.with(view.getContext())
                    .load(imageUrl)
                    .error(placeholder)
                    .placeholder(placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(view);
        }
    }





    @androidx.databinding.BindingAdapter(value = {"setPostDateTime"}, requireAll = false)
    public static void setPostDateTime(TextView textView, String postDateTime) {

        Log.e("TAG", "setPostDateTime: "+postDateTime );

        if (postDateTime != null){

            try {
                SimpleDateFormat df = new SimpleDateFormat("MMMM dd, yyyy - h:mm a", Locale.ENGLISH);
                df.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = null;
                date = df.parse(postDateTime);
                df.setTimeZone(TimeZone.getDefault());
                String formattedDate = df.format(date);

                textView.setText(formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }


    }


}
