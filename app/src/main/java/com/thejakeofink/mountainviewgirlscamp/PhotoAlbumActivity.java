package com.thejakeofink.mountainviewgirlscamp;

import android.app.ActionBar;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.Serializable;
import java.util.ArrayList;


public class PhotoAlbumActivity extends Activity implements AdapterView.OnItemClickListener {
    private static final String TAG = "PhotoAlbumActivity";
    public static final String PHOTOSET_ID = "photosetID";
    public static final int MESSAGE_UPDATE_FLICKR_PHOTOS = 0;

    PhotoAdapter photoAdapter;
    GridView photoGridView;
    FlickrManager.RetrievePhotosTask retrievePhotosTask;
    ActionBar actionBar;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case MESSAGE_UPDATE_FLICKR_PHOTOS:
                    Pair<String, ArrayList<FlickrPhoto>> titlePhotos = (Pair<String, ArrayList<FlickrPhoto>>) message.obj;
                    ArrayList<FlickrPhoto> thePhotos = titlePhotos.second;
                    String title = titlePhotos.first;
                    if (actionBar != null && actionBar.getTitle().equals("")) {
                        actionBar.setTitle(title);
                    }
                    if (photoAdapter != null) {
                        photoAdapter.clear();
                        photoAdapter.refill(thePhotos);
                    } else {
                        photoAdapter = new PhotoAdapter(thePhotos, PhotoAlbumActivity.this);
                        photoGridView.setAdapter(photoAdapter);
                        photoGridView.setOnItemClickListener(PhotoAlbumActivity.this);
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

        Bundle bundle = getIntent().getExtras();

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
        }

        if (bundle != null && bundle.containsKey(PHOTOSET_ID)) {
            loadPhotosForPhotoset(bundle.getString(PHOTOSET_ID));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (retrievePhotosTask != null) {
            retrievePhotosTask.destroy();
        }
    }

    private void loadPhotosForPhotoset(String albumID) {
        retrievePhotosTask = new FlickrManager.RetrievePhotosTask(albumID, this);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        openPhotoActivityForPosition(position);
    }

    private void openPhotoActivityForPosition(int position) {
        FlickrPhoto photoForBundle = (FlickrPhoto)photoAdapter.getItem(position);
        Intent photoIntent = new Intent(this, PhotoActivity.class);
        photoIntent.putExtra(PhotoActivity.FLICKR_PHOTO_THUMBNAIL, photoForBundle.thumbnail);
        photoIntent.putExtra(PhotoActivity.FLICKR_PHOTO_LARGE_IMAGE, photoForBundle.largeImage);
        photoIntent.putExtra(PhotoActivity.FLICKR_PHOTO_FARM, photoForBundle.farm);
        photoIntent.putExtra(PhotoActivity.FLICKR_PHOTO_ID, photoForBundle.photoID);
        photoIntent.putExtra(PhotoActivity.FLICKR_PHOTO_SECRET, photoForBundle.secret);
        photoIntent.putExtra(PhotoActivity.FLICKR_PHOTO_SERVER, photoForBundle.server);
        startActivity(photoIntent);
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
