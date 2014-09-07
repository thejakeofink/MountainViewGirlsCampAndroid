package com.thejakeofink.mountainviewgirlscamp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;

/**
 * Created by Jacob Stokes on 9/6/14.
 */
public class PhotoManager {
    Context context;
    Handler handler;

    public PhotoManager(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    public Bitmap getPhotoForFlickrPhoto(final FlickrPhoto flickrPhoto, boolean onUiThread) {
        if (context.getExternalFilesDir(null) == null) return null;

        String root = getRoot(context);
        File folder = new File(root);
        String myFilePath = "";

        Boolean fileExists = false;

        if(!folder.exists()){
            folder.mkdir();
            fileExists = false;
        } else {
            File myDir = new File(root);
            File[] files = myDir.listFiles(new FileFilter() {

                @Override
                public boolean accept(File file) {
                    return file.getName().startsWith(getFileNameFromFlickrPhoto(flickrPhoto));
                }
            });

            if (files.length > 0) {
                myFilePath = files[0].getPath();
                fileExists = true;
            } else {
                fileExists = false;
            }
        }

        if (fileExists) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(myFilePath, options);
            return bitmap;
        } else {
            if (onUiThread) {
                FlickrManager.GetFullPhoto getFullPhoto = new FlickrManager.GetFullPhoto(flickrPhoto, handler, context);
                getFullPhoto.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                flickrPhoto.largeImage = FlickrManager.loadImageForPhoto(flickrPhoto, false);
                FlickrManager.savePhotoToFile(flickrPhoto, context);
            }
        }

        return null;
    }

    public static String getFileNameFromFlickrPhoto(FlickrPhoto fp) {
        return fp.photoID + "_b";
    }

    public static File getFile(Context context, final String fileName) {

        String root = getRoot(context);

        File myDir = new File(root);
        File[] files = myDir.listFiles(new FileFilter() {

            @Override
            public boolean accept(File file) {
                return file.getName().startsWith(fileName);
            }
        });

        if (files.length > 0) {
            return files[0];
        } else {
            return null;
        }
    }

    public static String getRoot(Context context) {
        return Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getApplicationContext().getPackageName()
                + "/Files";
    }

    public static File getOutputMediaFile(Context context, String fileName){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(getRoot(context));

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return mediaFile;
    }


}
