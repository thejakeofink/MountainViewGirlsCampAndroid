package com.thejakeofink.mountainviewgirlscamp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;


public class PhotoAlbumActivity extends Activity implements AdapterView.OnItemClickListener, ActionMode.Callback {
    private static final String TAG = "PhotoAlbumActivity";
    public static final String PHOTOSET_ID = "photosetID";
    public static final int MESSAGE_UPDATE_FLICKR_PHOTOS = 0;

    PhotoAdapter photoAdapter;
    GridView photoGridView;
    FlickrManager.RetrievePhotosTask retrievePhotosTask;
    ActionBar actionBar;
    ActionMode actionMode;

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
                        photoAdapter = new PhotoAdapter(thePhotos);
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
            if (actionMode == null) {
                actionMode = this.startActionMode(this);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (actionMode == null) {
            openPhotoActivityForPosition(position);
        } else {
            selectPhotoAtPosition(position);
        }
    }

    private void selectPhotoAtPosition(int pos) {
        boolean photoSelected = !((FlickrPhoto)photoAdapter.getItem(pos)).selected;
        ((FlickrPhoto)photoAdapter.getItem(pos)).selected = photoSelected;
        photoAdapter.notifyDataSetChanged();
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

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.share_contextual, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

        ArrayList<FlickrPhoto> photosToShare = new ArrayList<FlickrPhoto>();

        for (FlickrPhoto fp : photoAdapter.flickrPhotos) {
            if (fp.selected) {
                photosToShare.add(fp);
            }
        }

        FlickrManager.LoadImagesForPhotos loadImagesForPhotos = new FlickrManager.LoadImagesForPhotos(photosToShare, this);
        loadImagesForPhotos.execute();

        actionMode.finish();
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        for (FlickrPhoto p : photoAdapter.flickrPhotos) {
            p.selected = false;
        }
        photoAdapter.notifyDataSetChanged();
        actionMode = null;
    }


    public class PhotoAdapter extends BaseAdapter {

        private String TAG = "AlbumAdapter";

        ArrayList<FlickrPhoto> flickrPhotos;

        public PhotoAdapter() {
            flickrPhotos = new ArrayList<FlickrPhoto>();
        }

        public PhotoAdapter(ArrayList<FlickrPhoto> photos) {
            flickrPhotos = (ArrayList<FlickrPhoto>) photos.clone();
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
                LayoutInflater inflater = PhotoAlbumActivity.this.getLayoutInflater();
                v = inflater.inflate(R.layout.grid_photo_item_layout, parent,false);
            }

            ((ImageView)v.findViewById(R.id.imgv_grid_item)).setImageBitmap(flickrPhotos.get(position).thumbnail);
            if (flickrPhotos.get(position).selected) {
                v.setBackgroundDrawable(v.getResources().getDrawable(R.drawable.item_border_selected_white));
            } else {
                v.setBackgroundDrawable(v.getResources().getDrawable(R.drawable.list_selector_white));
            }

//            v.setOnLongClickListener(PhotoAlbumActivity.this.longClickListener);
//
//            v.setTag(position);

            return v;
        }
    }
}

