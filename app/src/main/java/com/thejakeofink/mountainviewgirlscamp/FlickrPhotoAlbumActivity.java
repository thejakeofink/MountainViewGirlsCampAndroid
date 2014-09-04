package com.thejakeofink.mountainviewgirlscamp;

import android.app.Activity;
import android.content.Context;
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
import android.widget.TextView;

import java.util.ArrayList;


public class FlickrPhotoAlbumActivity extends Activity implements AdapterView.OnItemClickListener {
    private static final String TAG = "FlickrPhotoAlbumActivity";
    public static final int MESSAGE_UPDATE_FLICKR_ALBUMS = 0;

    AlbumAdapter albumAdapter;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case MESSAGE_UPDATE_FLICKR_ALBUMS:
                    ArrayList<Pair<String, String>> thealbums = (ArrayList<Pair<String, String>>) message.obj;
                    Log.v(TAG, "We got our albums " + thealbums);
                    albumAdapter = new AlbumAdapter(thealbums, FlickrPhotoAlbumActivity.this);
                    albumGridView.setAdapter(albumAdapter);
                    albumAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    GridView albumGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flickr_photo_album);

        albumGridView = (GridView) findViewById(R.id.album_photo_grid);

        albumGridView.setOnItemClickListener(this);

        loadAlbums();

        getActionBar().setTitle(R.string.photo_albums);
    }

    public void loadAlbums() {
        FlickrManager.SearchFlickrForSetsTask getFlickrAlbumsTask = new FlickrManager.SearchFlickrForSetsTask(this);
        getFlickrAlbumsTask.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent photoAlbumIntent = new Intent(this, PhotoAlbumActivity.class);
        photoAlbumIntent.putExtra(PhotoAlbumActivity.PHOTOSET_ID, "" + id);
        startActivity(photoAlbumIntent);
    }
}

class AlbumAdapter extends BaseAdapter {

    private static String TAG = "AlbumAdapter";

    ArrayList<Pair<String, String>> albumIdsTitles;
    Activity activity;

    public AlbumAdapter(Activity a) {
        albumIdsTitles = new ArrayList<Pair<String, String>>();
        activity = a;
    }

    public AlbumAdapter(ArrayList<Pair<String, String>> albums, Activity a) {
        albumIdsTitles = albums;
        activity = a;
    }

    @Override
    public int getCount() {
        return albumIdsTitles.size();
    }

    @Override
    public Object getItem(int position) {
        return albumIdsTitles.get(position).second;
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(albumIdsTitles.get(position).first);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            // Need to create a view
            LayoutInflater inflater = activity.getLayoutInflater();
            v = inflater.inflate(R.layout.grid_item_layout, parent,false);
        }

        ((TextView)v.findViewById(R.id.txv_grid_item)).setText(albumIdsTitles.get(position).second);

        return v;
    }
}
