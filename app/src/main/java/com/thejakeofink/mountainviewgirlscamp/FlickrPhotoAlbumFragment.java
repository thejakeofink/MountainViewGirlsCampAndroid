package com.thejakeofink.mountainviewgirlscamp;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class FlickrPhotoAlbumFragment extends Fragment implements AdapterView.OnItemClickListener, InitialPageActivity.OnPageChanged {
    private static final String TAG = "FlickrPhotoAlbumFragment";
    public static final int MESSAGE_UPDATE_FLICKR_ALBUMS = 0;

    AlbumAdapter albumAdapter;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case MESSAGE_UPDATE_FLICKR_ALBUMS:
                    ArrayList<Pair<String, String>> thealbums = (ArrayList<Pair<String, String>>) message.obj;

                    if (!thealbums.isEmpty()) {
                        if (albumAdapter != null) {
                            albumAdapter.clear();
                            albumAdapter.refill(thealbums);
                        } else {
                            albumAdapter = new AlbumAdapter(thealbums);
                            albumGridView.setAdapter(albumAdapter);
                            albumGridView.setOnItemClickListener(FlickrPhotoAlbumFragment.this);
                        }

                        albumAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(FlickrPhotoAlbumFragment.this.getActivity(), "An Internet connection is required to view photos.", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    GridView albumGridView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_flickr_photo_album, container, false);

        albumGridView = (GridView) rootView.findViewById(R.id.album_photo_grid);

        albumGridView.setOnItemClickListener(this);

        loadAlbums();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar ab = getActivity().getActionBar();
        if (ab != null) {
            ab.setTitle(R.string.photo_albums);
        }
    }

    public void loadAlbums() {
        FlickrManager.SearchFlickrForSetsTask getFlickrAlbumsTask = new FlickrManager.SearchFlickrForSetsTask(mHandler);
        getFlickrAlbumsTask.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent photoAlbumIntent = new Intent(this.getActivity(), PhotoAlbumActivity.class);
        photoAlbumIntent.putExtra(PhotoAlbumActivity.PHOTOSET_ID, "" + id);
        startActivity(photoAlbumIntent);
    }

	@Override
	public void onEnteringPage(InitialPageActivity activity) {

	}

	@Override
	public void onLeavingPage(InitialPageActivity activity) {

	}

	class AlbumAdapter extends BaseAdapter {

        private String TAG = "AlbumAdapter";

        ArrayList<Pair<String, String>> albumIdsTitles;

        public AlbumAdapter() {
            albumIdsTitles = new ArrayList<>();
        }

        public AlbumAdapter(ArrayList<Pair<String, String>> albums) {
            albumIdsTitles = (ArrayList<Pair<String,String>>)albums.clone();
        }

        public void clear() {
            albumIdsTitles.clear();
        }

        public void refill(ArrayList<Pair<String,String>> albums) {
            for (Pair<String, String> p : albums) {
                albumIdsTitles.add(p);
            }
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
                LayoutInflater inflater = FlickrPhotoAlbumFragment.this.getActivity().getLayoutInflater();
                v = inflater.inflate(R.layout.grid_item_layout, parent,false);
            }

            ((TextView)v.findViewById(R.id.txv_grid_item)).setText(albumIdsTitles.get(position).second);

            return v;
        }
    }
}


