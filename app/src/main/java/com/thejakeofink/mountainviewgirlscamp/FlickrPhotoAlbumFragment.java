package com.thejakeofink.mountainviewgirlscamp;

import android.app.Activity;
import android.content.Context;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class FlickrPhotoAlbumFragment extends Fragment implements InitialPageActivity.OnPageChanged {
    private static final String TAG = "FlickrPhotoAlbumFragment";

    AlbumAdapter albumAdapter;
    PhotoAdapter photoAdapter;

    static FlickrManager.RetrievePhotosTask getPhotosTask;
    static FlickrManager.SearchFlickrForSetsTask getFlickrAlbumsTask;

    static boolean cancelledPhotoTask = false;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case FlickrManager.MESSAGE_UPDATE_FLICKR_ALBUMS:
                    PhotoAlbum thealbum = (PhotoAlbum) message.obj;
                    if (albumAdapter != null) {
                        albumAdapter.addAlbum(thealbum);
                    } else {
                        albumAdapter = new AlbumAdapter(thealbum, FlickrPhotoAlbumFragment.this.getActivity());
                        albumRecyclerView.setAdapter(albumAdapter);
                    }

                    albumAdapter.notifyDataSetChanged();
                    break;
                case FlickrManager.MESSAGE_UPDATE_FLICKR_PHOTOS:
                    if (!cancelledPhotoTask) {
                        FlickrPhoto thePhoto = (FlickrPhoto) message.obj;

                        if (photoAdapter != null) {
                            photoAdapter.addPhoto(thePhoto);
                        } else {
                            photoAdapter = new PhotoAdapter(thePhoto, FlickrPhotoAlbumFragment.this.getActivity());
                            albumRecyclerView.setAdapter(photoAdapter);
                        }

                        if (albumRecyclerView.getAdapter() instanceof AlbumAdapter) {
                            albumRecyclerView.setAdapter(photoAdapter);
                        }

                        photoAdapter.notifyDataSetChanged();
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

        if (getPhotosTask == null) {
            getPhotosTask = new FlickrManager.RetrievePhotosTask(mHandler);
        }

        //albumRecyclerView.setOnItemClickListener(this); //TODO: redo click for items

        loadAlbums();

        return rootView;
    }

    public void loadAlbums() {
        getFlickrAlbumsTask = new FlickrManager.SearchFlickrForSetsTask(mHandler);
        getFlickrAlbumsTask.execute();
    }

    @Override
    public void onEnteringPage(InitialPageActivity activity) {

    }

    @Override
    public void onLeavingPage(InitialPageActivity activity) {

    }

    public void cancelPhotosTask() {
        getPhotosTask.destroy();
        cancelledPhotoTask = true;
        getPhotosTask = new FlickrManager.RetrievePhotosTask(mHandler);
        mHandler.removeMessages(FlickrManager.MESSAGE_UPDATE_FLICKR_PHOTOS);
    }


    class AlbumAdapter extends RecyclerView.Adapter<FlickrPhotoAlbumFragment.ImageViewHolder> {

        private String TAG = "AlbumAdapter";

        ArrayList<PhotoAlbum> albumIdsTitles;
        Context context;
        int lastPosition = -1;

        public AlbumAdapter() {
            albumIdsTitles = new ArrayList<>();
        }

        public AlbumAdapter(PhotoAlbum album, Context context) {
            albumIdsTitles = new ArrayList<>();
            albumIdsTitles.add(album);
            this.context = context;
        }

        public void addAlbum(PhotoAlbum album) {
            albumIdsTitles.add(album);
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

            setAnimation(holder.mItemView, position);
            holder.setOnClickListener();
        }

        @Override
        public void onViewDetachedFromWindow(ImageViewHolder holder) {
            holder.mItemView.clearAnimation();
            super.onViewDetachedFromWindow(holder);
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

        /**
         * Here is the key method to apply the animation
         */
        private void setAnimation(View viewToAnimate, int position) {
            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition) {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.scale_to_full);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
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
                loadPhotosForPhotoset((PhotoAlbum) albumOrImage);
            } else if (albumOrImage instanceof FlickrPhoto) {
                openPhotoActivity((FlickrPhoto) albumOrImage);
            }
        }

        private void openPhotoActivity(FlickrPhoto photoForBundle) {
            Intent photoIntent = new Intent(mItemView.getContext(), PhotoActivity.class);
            photoIntent.putExtra(PhotoActivity.FLICKR_PHOTO_THUMBNAIL, photoForBundle.thumbnail);
            photoIntent.putExtra(PhotoActivity.FLICKR_PHOTO_LARGE_IMAGE, photoForBundle.largeImage);
            photoIntent.putExtra(PhotoActivity.FLICKR_PHOTO_FARM, photoForBundle.farm);
            photoIntent.putExtra(PhotoActivity.FLICKR_PHOTO_ID, photoForBundle.photoID);
            photoIntent.putExtra(PhotoActivity.FLICKR_PHOTO_SECRET, photoForBundle.secret);
            photoIntent.putExtra(PhotoActivity.FLICKR_PHOTO_SERVER, photoForBundle.server);
            mItemView.getContext().startActivity(photoIntent);
        }

        public void setOnClickListener() {
            if (mItemView != null) {
                mItemView.setOnClickListener(this);
            }
        }

        private void loadPhotosForPhotoset(PhotoAlbum album) {
            getPhotosTask.addAlbum(album);
            cancelledPhotoTask = false;
            getPhotosTask.execute();
        }
    }

    class PhotoAdapter extends RecyclerView.Adapter<FlickrPhotoAlbumFragment.ImageViewHolder> {

        ArrayList<FlickrPhoto> albumPhotos;
        Context context;
        int lastPosition = -1;

        public PhotoAdapter() {
            albumPhotos = new ArrayList<>();
        }

        public PhotoAdapter(FlickrPhoto photo, Context context) {
            albumPhotos = new ArrayList<>();
            albumPhotos.add(photo);
            this.context = context;
        }

        public void addPhoto(FlickrPhoto photo) {
            albumPhotos.add(photo);
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
            setAnimation(holder.mItemView, position);
            holder.setOnClickListener();
        }

        @Override
        public void onViewDetachedFromWindow(ImageViewHolder holder) {
            holder.mItemView.clearAnimation();
            super.onViewDetachedFromWindow(holder);
        }

        public void clear() {
            albumPhotos.clear();
            lastPosition = -1;
        }

        public void refill(ArrayList<FlickrPhoto> photos) {
            for (FlickrPhoto p : photos) {
                albumPhotos.add(p);
            }
        }

        public FlickrPhoto getItem(int position) {
            return albumPhotos.get(position);
        }

        @Override
        public int getItemCount() {
            return albumPhotos.size();
        }

        /**
         * Here is the key method to apply the animation
         */
        private void setAnimation(View viewToAnimate, int position) {
            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition) {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.scale_to_full);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }
    }

}


