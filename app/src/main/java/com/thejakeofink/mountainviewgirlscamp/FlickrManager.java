package com.thejakeofink.mountainviewgirlscamp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
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
        Handler handler;
        String photosURL;
        String albumTitle = "";

        public void destroy() {
            failFast = true;
        }

        public RetrievePhotosTask(String setID, Handler handler) {
            photosURL = flickrURLForPhotoSet(setID);
            this.handler = handler;
            failFast = false;
        }

        @Override
        protected ArrayList<FlickrPhoto> doInBackground(Object[] params) {
            ArrayList<FlickrPhoto> albumPhotos = new ArrayList<FlickrPhoto>();
            if (failFast) return albumPhotos;
            ByteArrayOutputStream baos = URLConnector.readBytes(photosURL);
            if (baos != null) {
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
                            if (i < 5 || i % 5 == 0) {
                                publishProgress(albumPhotos);
                            }
                        }
                    }

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }
            return albumPhotos;
        }

        private void updateAlbumActivity(Handler handler, ArrayList<FlickrPhoto> flickrPhotos) {
            if (handler != null) {
                Pair<String, ArrayList<FlickrPhoto>> titlePhotos = new Pair<String, ArrayList<FlickrPhoto>>(albumTitle, flickrPhotos);
                Message message = handler.obtainMessage(PhotoAlbumActivity.MESSAGE_UPDATE_FLICKR_PHOTOS, titlePhotos);
                handler.sendMessage(message);
            }
        }

        @Override
        protected void onProgressUpdate(ArrayList<FlickrPhoto>... values) {
            if (failFast) return;
            updateAlbumActivity(handler, values[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<FlickrPhoto> flickrPhotos) {
            if (failFast) return;
            updateAlbumActivity(handler, flickrPhotos);
        }
    };

    /*
    Do not call this method from the UI Thread!
     */
    public static Bitmap loadImageForPhoto(FlickrPhoto flickrPhoto, boolean isThumbnail) {
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
        Context context;

        public LoadImagesForPhotos(ArrayList<FlickrPhoto> photos, Context context) {
            this.photosToShare = photos;
            this.context = context;
        }

        @Override
        protected ArrayList<FlickrPhoto> doInBackground(Object... params) {
            PhotoManager pm = new PhotoManager(context, null);
            for (FlickrPhoto p : photosToShare) {
                if (p.largeImage == null) {
                    p.largeImage = pm.getPhotoForFlickrPhoto(p, false);
                }
            }

            return photosToShare;
        }

        @Override
        protected void onPostExecute(ArrayList<FlickrPhoto> flickrPhotos) {
            ArrayList<File> files = new ArrayList<File>();
            ArrayList<Uri> uris = new ArrayList<Uri>();

            for (FlickrPhoto p : flickrPhotos) {
                File file = PhotoManager.getFile(context, PhotoManager.getFileNameFromFlickrPhoto(p));
                if (file != null) {
                    files.add(file);
                }
            }

            for(File file : files ) {
                Uri uri = Uri.fromFile(file);
                uris.add(uri);
            }
            Intent intent = new Intent();
            if (uris.size() > 1) {
                intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
				sendShareIntent(context, intent);
            } else if (uris.size() == 1){
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, uris.get(0));
				sendShareIntent(context, intent);
            }

        }

		private void sendShareIntent(Context context, Intent intent) {
			intent.setType("image/*");
			context.startActivity(Intent.createChooser(intent, "Select App to Share"));
		}
    }

    public static class SearchFlickrForSetsTask extends AsyncTask<Object, ArrayList<Pair<String,String>>, ArrayList<Pair<String, String>>> {
        private static String TAG = "SearchFlickrForSetsTask";
        String listURL;
        Handler handler;

        public SearchFlickrForSetsTask(Handler handler) {
            this.listURL = flickrListURLForAccount();
            this.handler = handler;
        }

        @Override
        protected ArrayList<Pair<String, String>> doInBackground(Object[] params) {
            ByteArrayOutputStream baos = URLConnector.readBytes(listURL);
            ArrayList<Pair<String, String>> albums = new ArrayList<Pair<String, String>>();
            if (baos != null) {
                String json = baos.toString();
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

                            if (i < 5 || i % 5 == 0) {
                                publishProgress(albums);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return albums;
        }

        private void updateUI(Handler handler, ArrayList<Pair<String, String>> albums) {
            if (handler != null) {
                Message message = handler.obtainMessage(FlickrPhotoAlbumFragment.MESSAGE_UPDATE_FLICKR_ALBUMS, albums);
                handler.sendMessage(message);
            }
        }

        @Override
        protected void onProgressUpdate(ArrayList<Pair<String,String>>... values) {
            updateUI(handler, values[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<Pair<String, String>> pairs) {
            updateUI(handler, pairs);
        }
    };

    public static class GetFullPhoto extends AsyncTask<Object, Object, Object> {

        FlickrPhoto flickrPhoto;
        Handler handler;
        Context context;

        public GetFullPhoto(FlickrPhoto photo, Handler handler, Context context){
            this.flickrPhoto = photo;
            this.handler = handler;
            this.context = context;
        }

        @Override
        protected Object doInBackground(Object... params) {

            flickrPhoto.largeImage = loadImageForPhoto(flickrPhoto, false);

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            if (handler != null) {
                Message message = handler.obtainMessage(PhotoActivity.MESSAGE_UPDATE_FLICKR_PHOTO, flickrPhoto);
                handler.sendMessage(message);
            }

            savePhotoToFile(flickrPhoto, context);
        }
    }

    /*
    Don't call from the UI Thread.
     */
    public static void savePhotoToFile(FlickrPhoto flickrPhoto, Context context) {
        String fname = PhotoManager.getFileNameFromFlickrPhoto(flickrPhoto) + ".jpg";

        //Set the new path where you would like the photo to be saved.
        File file = PhotoManager.getOutputMediaFile(context, fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if(flickrPhoto.largeImage != null){
                flickrPhoto.largeImage.compress(Bitmap.CompressFormat.JPEG, 90, out);
            }
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


