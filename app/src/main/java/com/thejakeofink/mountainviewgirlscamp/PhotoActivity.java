package com.thejakeofink.mountainviewgirlscamp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ShareActionProvider;

import java.io.File;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;


public class PhotoActivity extends Activity {

    public static final String FLICKR_PHOTO_THUMBNAIL = "FlickrPhotoThumbnail";
    public static final String FLICKR_PHOTO_FARM = "FlickrPhotoFarm";
    public static final String FLICKR_PHOTO_SECRET = "FlickrPhotoSecret";
    public static final String FLICKR_PHOTO_LARGE_IMAGE = "FlickrPhotoLargeImage";
    public static final String FLICKR_PHOTO_ID = "FlickrPhotoID";
    public static final String FLICKR_PHOTO_SERVER = "FLickrPhotoServer";
    public static final int MESSAGE_UPDATE_FLICKR_PHOTO = 0;
    private static final String TAG = "PhotoActivity";

    FlickrPhoto flickrPhoto;
    ImageViewTouch photoView;
    ShareActionProvider mShareActionProvider;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case MESSAGE_UPDATE_FLICKR_PHOTO:
                    if (photoView != null) {
                        updatePhotoView();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        photoView = (ImageViewTouch) findViewById(R.id.imgv_flickr_photo);

        photoView.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);

        Bundle bundle;

        if (savedInstanceState == null) {
            bundle = getIntent().getExtras();
        } else {
            bundle = savedInstanceState;
        }

        if (bundle != null) {
            flickrPhoto = loadPhotoFromBundle(bundle);

        } else {

        }

        updatePhotoView();
    }

    private void updatePhotoView() {
        if (flickrPhoto != null) {
            PhotoManager pm = new PhotoManager(this, mHandler);

            flickrPhoto.largeImage = pm.getPhotoForFlickrPhoto(flickrPhoto, true);

            if (flickrPhoto.largeImage == null) {
                photoView.setImageBitmap(flickrPhoto.thumbnail);
            } else {
                photoView.setImageBitmap(flickrPhoto.largeImage);

                buildAndSetShareIntent();
            }
        }
    }

    private void buildAndSetShareIntent() {
        File file = PhotoManager.getFile(this, PhotoManager.getFileNameFromFlickrPhoto(flickrPhoto));
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("image/*");
        setShareIntent(intent);
    }

    private FlickrPhoto loadPhotoFromBundle(Bundle bundle) {
        Bitmap thumb = (Bitmap) bundle.get(FLICKR_PHOTO_THUMBNAIL);
        Bitmap large = (Bitmap) bundle.get(FLICKR_PHOTO_LARGE_IMAGE);
        int farm = bundle.getInt(FLICKR_PHOTO_FARM);
        int server = bundle.getInt(FLICKR_PHOTO_SERVER);
        String secret = bundle.getString(FLICKR_PHOTO_SECRET);
        long id = bundle.getLong(FLICKR_PHOTO_ID);
        return new FlickrPhoto(thumb, large, id, farm, server, secret);
    }

    private void loadPhotoIntoBundle(Bundle bundle) {
        bundle.putParcelable(FLICKR_PHOTO_THUMBNAIL, flickrPhoto.thumbnail);
        bundle.putParcelable(FLICKR_PHOTO_LARGE_IMAGE, flickrPhoto.largeImage);
        bundle.putLong(FLICKR_PHOTO_ID, flickrPhoto.photoID);
        bundle.putInt(FLICKR_PHOTO_FARM, flickrPhoto.farm);
        bundle.putInt(FLICKR_PHOTO_SERVER, flickrPhoto.server);
        bundle.putString(FLICKR_PHOTO_SECRET, flickrPhoto.secret);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        loadPhotoIntoBundle(outState);
        super.onSaveInstanceState(outState);
    }

    /*
    Possible Additional share feature in each photo. For now commented out.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.photo, menu);

        MenuItem item = menu.findItem(R.id.menu_item_share);

        mShareActionProvider = (ShareActionProvider) item.getActionProvider();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.menu_item_share) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }
}


