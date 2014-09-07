package com.thejakeofink.mountainviewgirlscamp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

    public Bitmap getPhotoForFlickrPhoto(final FlickrPhoto flickrPhoto) {
        if (context.getExternalFilesDir(null) == null) return null;

        String root =  Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getApplicationContext().getPackageName()
                + "/Files";
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
                    return file.getName().startsWith(String.valueOf(flickrPhoto.photoID + "_b"));
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
            FlickrManager.GetFullPhoto getFullPhoto = new FlickrManager.GetFullPhoto(flickrPhoto, handler, context);
            getFullPhoto.execute();
        }

        return null;
    }

    public static File getOutputMediaFile(Context context, String fileName){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getApplicationContext().getPackageName()
                + "/Files");

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
