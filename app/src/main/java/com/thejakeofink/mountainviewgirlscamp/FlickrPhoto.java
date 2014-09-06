package com.thejakeofink.mountainviewgirlscamp;

import android.graphics.Bitmap;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * Created by Jacob Stokes on 8/26/14.
 */
public class FlickrPhoto {

    public FlickrPhoto(){}

    public FlickrPhoto(Bitmap thumb, Bitmap large, long id, int farm, int server, String secret) {
        this.thumbnail = thumb;
        this.largeImage = large;
        this.photoID = id;
        this.farm = farm;
        this.server = server;
        this.secret = secret;
    }

    public Bitmap thumbnail;
    public Bitmap largeImage;

    public long photoID;
    public int farm;
    public int server;
    public String secret;
}
