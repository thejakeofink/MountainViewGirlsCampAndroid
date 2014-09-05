package com.thejakeofink.mountainviewgirlscamp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by Jacob Stokes on 8/26/14.
 */
public class FlickrManager {
    public static final String TAG = "FlickrManager";
    public static String flickrAPIKey = "f3b34fa4324967a8e889ae3c815c84a9";
    public static String userID = "125836065@N02";
    public static String flickrPreString = "https://api.flickr.com/services/rest/?";

    private static String flickrURLForPhotoSet(String photosetID) {
        return flickrPreString + "method=flickr.photosets.getPhotos&api_key=" + flickrAPIKey + "&photoset_id=" + photosetID + "&format=json&nojsoncallback=1";
    }

    private static String flickrPhotoURLForFlickrPhoto(FlickrPhoto flickrPhoto, String size) {
        if (size == null) {
            size = "m";
        }
        return "http://farm" + flickrPhoto.farm + ".staticflickr.com/" + flickrPhoto.server + "/" + flickrPhoto.photoID + "_" + flickrPhoto.secret + "_" + size + ".jpg";
    }

    private static String flickrListURLForAccount() {
        return flickrPreString + "method=flickr.photosets.getList&api_key=" + flickrAPIKey + "&user_id=" + userID + "&format=json&nojsoncallback=1";
    }



    public static class RetrievePhotosTask extends AsyncTask<Object, ArrayList<FlickrPhoto>, ArrayList<FlickrPhoto>> {
        private static final String TAG = "RetrievePhotosTask";
        private static boolean failFast = false;
        String photosURL;
        String albumTitle = "";
        WeakReference<PhotoAlbumActivity> weakActivity;

        public void destroy() {
            failFast = true;
        }

        public RetrievePhotosTask(String setID, PhotoAlbumActivity photoAlbumActivity) {
            photosURL = flickrURLForPhotoSet(setID);
            weakActivity = new WeakReference<PhotoAlbumActivity>(photoAlbumActivity);
            failFast = false;
        }

        @Override
        protected ArrayList<FlickrPhoto> doInBackground(Object[] params) {
            ArrayList<FlickrPhoto> albumPhotos = new ArrayList<FlickrPhoto>();
            if (failFast) return albumPhotos;
            ByteArrayOutputStream baos = URLConnector.readBytes(photosURL);
            String json = baos.toString();

            try {
                JSONObject root = new JSONObject(json);

                String status = root.getString("stat");
                if (status.equals("ok")) {

                    JSONObject photoset = root.getJSONObject("photoset");

                    albumTitle = photoset.getString("title");

                    JSONArray photos = photoset.getJSONArray("photo");

                    for (int i = 0; i < photos.length() && !failFast; i++) {
                        JSONObject jsonPhoto = photos.getJSONObject(i);
                        FlickrPhoto flickrPhoto = new FlickrPhoto();
                        flickrPhoto.photoID = Long.parseLong(jsonPhoto.getString("id"));
                        flickrPhoto.farm = Integer.parseInt(jsonPhoto.getString("farm"));
                        flickrPhoto.secret = jsonPhoto.getString("secret");
                        flickrPhoto.server = Integer.parseInt(jsonPhoto.getString("server"));
                        flickrPhoto.thumbnail = loadImageForPhoto(flickrPhoto, true);
                        albumPhotos.add(flickrPhoto);
                        if (i % 5 == 0) {
                            publishProgress(albumPhotos);
                        }
                    }
                }

            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }

            return albumPhotos;
        }

        @Override
        protected void onProgressUpdate(ArrayList<FlickrPhoto>... values) {
            if (failFast) return;
            Pair<String, ArrayList<FlickrPhoto>> titlePhotos = new Pair<String, ArrayList<FlickrPhoto>>(albumTitle, values[0]);
            PhotoAlbumActivity currentActivity = weakActivity.get();
            if (currentActivity != null) {
                Message message = currentActivity.mHandler.obtainMessage(PhotoAlbumActivity.MESSAGE_UPDATE_FLICKR_PHOTOS, titlePhotos);
                currentActivity.mHandler.sendMessage(message);
            }
        }

        @Override
        protected void onPostExecute(ArrayList<FlickrPhoto> flickrPhotos) {
            if (failFast) return;
            Pair<String, ArrayList<FlickrPhoto>> titlePhotos = new Pair<String, ArrayList<FlickrPhoto>>(albumTitle, flickrPhotos);
            PhotoAlbumActivity currentActivity = weakActivity.get();
            if (currentActivity != null) {
                Message message = currentActivity.mHandler.obtainMessage(PhotoAlbumActivity.MESSAGE_UPDATE_FLICKR_PHOTOS, titlePhotos);
                currentActivity.mHandler.sendMessage(message);
            }
        }
    };

    private static Bitmap loadImageForPhoto(FlickrPhoto flickrPhoto, boolean isThumbnail) {
        String size = isThumbnail ? "m" : "b";
        String photoURL = flickrPhotoURLForFlickrPhoto(flickrPhoto, size);
        Bitmap bm = null;
        try {
            URL aURL = new URL(photoURL);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(is);
            bis.close();
            is.close();
        } catch (Exception e) {
            Log.e(TAG, "loadImageForPhoto our error is: " + e.getMessage());
        }
        return bm;
    }

    public static class LoadImagesForPhotos extends AsyncTask<Object, Object, ArrayList<FlickrPhoto>> {

        ArrayList<FlickrPhoto> photosToShare;

        public LoadImagesForPhotos(ArrayList<FlickrPhoto> photos) {
            photosToShare = photos;
        }

        @Override
        protected ArrayList<FlickrPhoto> doInBackground(Object... params) {
            for (FlickrPhoto p : photosToShare) {

                String photoURL = flickrPhotoURLForFlickrPhoto(p, "b");
                ByteArrayOutputStream baos = URLConnector.readBytes(photoURL);
                String json = baos.toString();
            }

            return null;
        }
    }

    public static class SearchFlickrForSetsTask extends AsyncTask<Object, Object, ArrayList<Pair<String, String>>> {
        private static String TAG = "SearchFlickrForSetsTask";
        String listURL;
        WeakReference<FlickrPhotoAlbumActivity> weakActivity;

        public SearchFlickrForSetsTask(FlickrPhotoAlbumActivity albumActivity) {
            listURL = flickrListURLForAccount();
            weakActivity = new WeakReference<FlickrPhotoAlbumActivity>(albumActivity);
        }

        @Override
        protected ArrayList<Pair<String, String>> doInBackground(Object[] params) {
            ByteArrayOutputStream baos = URLConnector.readBytes(listURL);
            String json = baos.toString();
            ArrayList<Pair<String,String>> albums = new ArrayList<Pair<String, String>>();
            try {
                JSONObject root = new JSONObject(json);

                String status = root.getString("stat");
                if (status.equals("ok")) {
                    JSONObject rootContent = root.getJSONObject("photosets");

                    JSONArray photosets = rootContent.getJSONArray("photoset");

                    for (int i = 0; i < photosets.length(); i++) {
                        JSONObject tempObj = photosets.getJSONObject(i);
                        String albumTitle;
                        String albumID;
                        albumID = tempObj.getString("id");
                        albumTitle = tempObj.getJSONObject("title").getString("_content");
                        Pair<String, String> tempPair = new Pair<String, String>(albumID, albumTitle);
                        albums.add(tempPair);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return albums;
        }

        @Override
        protected void onPostExecute(ArrayList<Pair<String, String>> pairs) {
            FlickrPhotoAlbumActivity currentActivity = weakActivity.get();
            if (currentActivity != null) {
                Message message = currentActivity.mHandler.obtainMessage(FlickrPhotoAlbumActivity.MESSAGE_UPDATE_FLICKR_ALBUMS, pairs);
                currentActivity.mHandler.sendMessage(message);
            }
        }
    };

}


