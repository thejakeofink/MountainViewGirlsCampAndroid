package com.thejakeofink.mountainviewgirlscamp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Jacob Stokes on 8/27/14.
 */
public class URLConnector {

    private static int CONNECT_TIMEOUT_MS = 5000;
    private static int READ_TIMEOUT_MS = 15000;

    public static boolean isOnline(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public static ByteArrayOutputStream readBytes(String urlS) {
        ByteArrayOutputStream baos = null;
        InputStream is = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(urlS);
            Log.i("URL", url.toString());
            httpURLConnection = (HttpURLConnection) url.openConnection();
            int response = httpURLConnection.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                httpURLConnection.setConnectTimeout(CONNECT_TIMEOUT_MS);
                httpURLConnection.setReadTimeout(READ_TIMEOUT_MS);
                is = new BufferedInputStream(httpURLConnection.getInputStream());

                int size = 1024;
                byte[] buffer = new byte[size];

                baos = new ByteArrayOutputStream();
                int read = 0;
                while ((read = is.read(buffer)) != -1) {
                    if (read > 0) {
                        baos.write(buffer, 0, read);
                        buffer = new byte[size];
                    }
                }
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (httpURLConnection != null) {
                try {
                    httpURLConnection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return baos;
    }
}
