package com.locatocam.app.views.createrolls.models;

import android.graphics.Bitmap;

public class GalleryVideoItem {
    String path,name;
    Bitmap image;
    long length;

    public GalleryVideoItem(String path, Bitmap image) {
        this.path = path;
        this.image = image;
    }

    public GalleryVideoItem(String path, String name, Bitmap image) {
        this.path = path;
        this.name = name;
        this.image = image;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }
}
