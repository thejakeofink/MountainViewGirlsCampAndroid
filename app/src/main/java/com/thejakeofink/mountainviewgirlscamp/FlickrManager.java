package com.thejakeofink.mountainviewgirlscamp;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Pair;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by Jacob Stokes on 8/26/14.
 */
public class FlickrManager {

    public static String flickrAPIKey = "f3b34fa4324967a8e889ae3c815c84a9";
    public static String userID = "125836065@N02";
    public static String flickrPreString = "https://api.flickr.com/services/rest/?";

    private String flickrURLForPhotoSet(String photosetID) {
        return flickrPreString + "method=flickr.photosets.getPhotos&api_key=" + flickrAPIKey + "&photoset_id=" + photosetID + "&format=json&nojsoncallback=1";
    }

    private String flickrPhotoURLForFlickrPhoto(FlickrPhoto flickrPhoto, String size) {
        if (size == null) {
            size = "m";
        }
        return "http://farm" + flickrPhoto.farm + ".staticflickr.com/" + flickrPhoto.server + "/" + flickrPhoto.photoID + "_" + flickrPhoto.secret + "_" + size + ".jpg";
    }

    private String flickrListURLForAccount() {
        return flickrPreString + "method=flickr.photosets.getList&api_key=" + flickrAPIKey + "&user_id=" + userID + "&format=json&nojsoncallback=1";
    }

    public class SearchFlickrForSets extends AsyncTask<Object, Object, ArrayList<Pair<String, String>>> {

        String listURL;

        @Override
        protected void onPreExecute() {
            listURL = flickrListURLForAccount();
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Pair<String, String>> doInBackground(Object[] params) {
            ByteArrayOutputStream baos = URLConnector.readBytes(listURL);
            String json = baos.toString();


            return null;
        }
    };

    public class RetrievePhotosTask extends AsyncTask<Object, Object, ArrayList<FlickrPhoto>> {

        String photosURL;

        public RetrievePhotosTask(String setID) {
            photosURL = flickrURLForPhotoSet(setID);
        }

        @Override
        protected ArrayList<FlickrPhoto> doInBackground(Object[] params) {
            ByteArrayOutputStream baos = URLConnector.readBytes(photosURL);
            String json = baos.toString();

            return null;
        }
    };

    public class LoadImageForPhoto extends AsyncTask<Object, Object, Bitmap> {

        String photoURL;

        public LoadImageForPhoto(FlickrPhoto photo, String size) {
            photoURL = flickrPhotoURLForFlickrPhoto(photo, size);
        }

        @Override
        protected Bitmap doInBackground(Object... params) {
            ByteArrayOutputStream baos = URLConnector.readBytes(photoURL);
            String json = baos.toString();

            return null;
        }
    }





//            Pair<String, FlickrPhoto>, Object, Bitmap> {
//
//        String photoURL;
//
//        @Override
//        protected FlickrPhoto doInBackground(Pair<String, FlickrPhoto>[] params) {
//
//            photoURL = flickrPhotoURLForFlickrPhoto(params[0].second, params[0].first);
//
//            return new FlickrPhoto();
//        }
//    }
}
