package com.thejakeofink.mountainviewgirlscamp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class PhotoAlbumActivity extends Activity {
    private static final String TAG = "PhotoAlbumActivity";
    public static final String PHOTOSET_ID = "photosetID";
    public static final int MESSAGE_UPDATE_FLICKR_PHOTOS = 0;

    PhotoAdapter photoAdapter;
    GridView photoGridView;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case MESSAGE_UPDATE_FLICKR_PHOTOS:
                    Pair<String, ArrayList<FlickrPhoto>> titlePhotos = (Pair<String, ArrayList<FlickrPhoto>>) message.obj;
                    ArrayList<FlickrPhoto> thePhotos = titlePhotos.second;
                    String title = titlePhotos.first;
                    if (PhotoAlbumActivity.this.getActionBar().getTitle().equals("")) {
                        PhotoAlbumActivity.this.getActionBar().setTitle(title);
                    }
                    if (photoAdapter != null) {
                        photoAdapter.clear();
                        photoAdapter.refill(thePhotos);
                    } else {
                        photoAdapter = new PhotoAdapter(thePhotos, PhotoAlbumActivity.this);
                        photoGridView.setAdapter(photoAdapter);
                    }
                    photoAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_album);

        photoGridView = (GridView) findViewById(R.id.photo_grid_view);

        Intent intent = getIntent();

        getActionBar().setTitle("");

        if (intent.getExtras() != null) {
            loadPhotosForPhotoset(intent.getExtras().getString(PHOTOSET_ID));
        }
    }

    private void loadPhotosForPhotoset(String albumID) {
        FlickrManager.RetrievePhotosTask retrievePhotosTask = new FlickrManager.RetrievePhotosTask(albumID, this);
        retrievePhotosTask.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.photo_album, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_share) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

class PhotoAdapter extends BaseAdapter {

    private static String TAG = "AlbumAdapter";

    ArrayList<FlickrPhoto> flickrPhotos;
    Activity activity;

    public PhotoAdapter(Activity a) {
        flickrPhotos = new ArrayList<FlickrPhoto>();
        activity = a;
    }

    public PhotoAdapter(ArrayList<FlickrPhoto> photos, Activity a) {
        flickrPhotos = (ArrayList<FlickrPhoto>) photos.clone();
        activity = a;
    }

    public void clear() {
        flickrPhotos.clear();
    }

    public void refill(ArrayList<FlickrPhoto> photos) {
        for (FlickrPhoto p : photos) {
            flickrPhotos.add(p);
        }
    }

    @Override
    public int getCount() {
        return flickrPhotos.size();
    }

    @Override
    public Object getItem(int position) {
        return flickrPhotos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return flickrPhotos.get(position).photoID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            // Need to create a view
            LayoutInflater inflater = activity.getLayoutInflater();
            v = inflater.inflate(R.layout.grid_photo_item_layout, parent,false);
        }


        ((ImageView)v.findViewById(R.id.imgv_grid_item)).setImageBitmap(flickrPhotos.get(position).thumbnail);

        return v;
    }
}
