package com.thejakeofink.mountainviewgirlscamp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class FlickrPhotoAlbumFragment extends Fragment implements InitialPageActivity.OnPageChanged {
    private static final String TAG = "FlickrPhotoAlbumFragment";

    AlbumAdapter albumAdapter;
	PhotoAdapter photoAdapter;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case FlickrManager.MESSAGE_UPDATE_FLICKR_ALBUMS:
                    ArrayList<PhotoAlbum> thealbums = (ArrayList<PhotoAlbum>) message.obj;

                    if (!thealbums.isEmpty()) {
                        if (albumAdapter != null) {
                            albumAdapter.clear();
                            albumAdapter.refill(thealbums);
                        } else {
                            albumAdapter = new AlbumAdapter(thealbums);
                            albumRecyclerView.setAdapter(albumAdapter);
                        }

                        albumAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(FlickrPhotoAlbumFragment.this.getActivity(), "An Internet connection is required to view photos.", Toast.LENGTH_SHORT).show();
                    }
                    break;
				case FlickrManager.MESSAGE_UPDATE_FLICKR_PHOTOS:
					ArrayList<FlickrPhoto> thePhotos = (ArrayList<FlickrPhoto>) message.obj;

					if (!thePhotos.isEmpty()) {
						if (photoAdapter != null) {
							photoAdapter.clear();
							photoAdapter.refill(thePhotos);
						} else {
							photoAdapter = new PhotoAdapter(thePhotos);
							albumRecyclerView.setAdapter(photoAdapter);
						}

						if (albumRecyclerView.getAdapter() instanceof AlbumAdapter) {
							albumRecyclerView.setAdapter(photoAdapter);
						}

						photoAdapter.notifyDataSetChanged();
					} else {
						Toast.makeText(FlickrPhotoAlbumFragment.this.getActivity(), "Unable to load photo data try again later.", Toast.LENGTH_SHORT).show();
					}

				break;
            }
        }
    };

    RecyclerView albumRecyclerView;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

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

		public AlbumAdapter(ArrayList<PhotoAlbum> albums) {
			albumIdsTitles = (ArrayList<PhotoAlbum>)albums.clone();
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
				holder.albumOrImage = album;
				holder.handler = FlickrPhotoAlbumFragment.this.mHandler;
				if (holder.vImage != null) {
					holder.vImage.setImageBitmap(album.img);
				}
				if (holder.vText != null) {
					holder.vText.setText(album.title);
				}
			}

			holder.setOnClickListener();
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
    }

	public static class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		protected ImageView vImage;
		protected TextView vText;
		protected Object albumOrImage;
		protected Handler handler;
		protected View mItemView;

		public ImageViewHolder(View itemView) {
			super(itemView);
			mItemView = itemView;
			vImage = (ImageView) itemView.findViewById(R.id.album_image);
			vText = (TextView) itemView.findViewById(R.id.album_title);

		}

		@Override
		public void onClick(View v) {
			if (albumOrImage instanceof PhotoAlbum) {
				loadPhotosForPhotoset((PhotoAlbum)albumOrImage);
			} else if (albumOrImage instanceof FlickrPhoto) {
				Toast.makeText(v.getContext(), "You clicked a photo!", Toast.LENGTH_SHORT).show();
			}
		}

		public void setOnClickListener() {
			if (mItemView != null) {
				mItemView.setOnClickListener(this);
			}
		}

		private void loadPhotosForPhotoset(PhotoAlbum album) {
			new FlickrManager.RetrievePhotosTask(album, handler).execute();
		}
	}

	class PhotoAdapter extends RecyclerView.Adapter<FlickrPhotoAlbumFragment.ImageViewHolder> {

		ArrayList<FlickrPhoto> albumPhotos;

		public PhotoAdapter() {
			albumPhotos = new ArrayList<>();
		}

		public PhotoAdapter(ArrayList<FlickrPhoto> albums) {
			albumPhotos = (ArrayList<FlickrPhoto>)albums.clone();
		}

		@Override
		public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_album_card, parent, false);
			return new ImageViewHolder(v);
		}

		@Override
		public void onBindViewHolder(ImageViewHolder holder, int position) {
			FlickrPhoto photo = albumPhotos.get(position);
			if (photo != null) {
				holder.albumOrImage = photo;
				holder.handler = FlickrPhotoAlbumFragment.this.mHandler;
				if (holder.vImage != null) {
					holder.vImage.setImageBitmap(photo.thumbnail);
				}
				if (holder.vText != null) {
					holder.vText.setText("");
				}
			}

			holder.setOnClickListener();
		}

		public void clear() {
			albumPhotos.clear();
		}

		public void refill(ArrayList<FlickrPhoto> photos) {
			for (FlickrPhoto p : photos) {
				albumPhotos.add(p);
			}
		}

		@Override
		public int getItemCount() {
			return albumPhotos.size();
		}
	}

}


