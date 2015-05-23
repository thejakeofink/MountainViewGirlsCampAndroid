package com.thejakeofink.mountainviewgirlscamp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
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
                    ArrayList<PhotoAlbum> thealbums = (ArrayList<PhotoAlbum>) message.obj;

                    if (!thealbums.isEmpty()) {
                        if (albumAdapter != null) {
                            albumAdapter.clear();
                            albumAdapter.refill(thealbums);
                        } else {
                            albumAdapter = new AlbumAdapter(thealbums);
                            albumRecyclerView.setAdapter(albumAdapter);
                            //albumRecyclerView.setOnItemClickListener(FlickrPhotoAlbumFragment.this);//TODO: redo click for items
                        }

                        albumAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(FlickrPhotoAlbumFragment.this.getActivity(), "An Internet connection is required to view photos.", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    RecyclerView albumRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_flickr_photo_album, container, false);

        albumRecyclerView = (RecyclerView) rootView.findViewById(R.id.album_photo_grid);
		albumRecyclerView.setHasFixedSize(false);
		GridLayoutManager glm = new GridLayoutManager(getActivity(), 2);
		glm.setOrientation(GridLayoutManager.VERTICAL);
		albumRecyclerView.setLayoutManager(glm);

        //albumRecyclerView.setOnItemClickListener(this); //TODO: redo click for items

        loadAlbums();

        return rootView;
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

	class AlbumAdapter extends RecyclerView.Adapter<FlickrPhotoAlbumFragment.ImageViewHolder> {

        private String TAG = "AlbumAdapter";

        ArrayList<PhotoAlbum> albumIdsTitles;

        public AlbumAdapter() {
            albumIdsTitles = new ArrayList<>();
        }

		@Override
		public FlickrPhotoAlbumFragment.ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_album_card, parent, false);
			return new ImageViewHolder(v);
		}

		@Override
		public void onBindViewHolder(FlickrPhotoAlbumFragment.ImageViewHolder holder, int position) {
			PhotoAlbum album = albumIdsTitles.get(position);
			if (album != null) {
				if (holder.vImage != null) {
					holder.vImage.setImageBitmap(album.img);
				}
				if (holder.vText != null) {
					holder.vText.setText(album.title);
				}
			}
		}

		public AlbumAdapter(ArrayList<PhotoAlbum> albums) {
            albumIdsTitles = (ArrayList<PhotoAlbum>)albums.clone();
        }

        public void clear() {
            albumIdsTitles.clear();
        }

        public void refill(ArrayList<PhotoAlbum> albums) {
            for (PhotoAlbum p : albums) {
                albumIdsTitles.add(p);
            }
        }

        @Override
        public long getItemId(int position) {
            return Long.parseLong(albumIdsTitles.get(position).id);
        }

		@Override
		public int getItemCount() {
			return albumIdsTitles.size();
		}

//		@Override //TODO: switch this to the on bind stuff.
//        public View getView(int position, View convertView, ViewGroup parent) {
//            View v = convertView;
//            if (v == null) {
//                // Need to create a view
//                LayoutInflater inflater = FlickrPhotoAlbumFragment.this.getActivity().getLayoutInflater();
//                v = inflater.inflate(R.layout.grid_item_layout, parent,false);
//            }
//
//            ((TextView)v.findViewById(R.id.txv_grid_item)).setText(albumIdsTitles.get(position).second);
//
//            return v;
//        }
    }

	public static class ImageViewHolder extends RecyclerView.ViewHolder {
		protected ImageView vImage;
		protected TextView vText;

		public ImageViewHolder(View itemView) {
			super(itemView);

			vImage = (ImageView) itemView.findViewById(R.id.album_image);
			vText = (TextView) itemView.findViewById(R.id.album_title);
		}
	}
}


