package com.hpc.vkd;

import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by hpc on 1/27/18.
 */
abstract class GetFileNameTask extends AsyncTask<String, Void, FileName> {

    private String TAG = GetFileNameTask.class.getName();

    @Override
    protected FileName doInBackground(String... fileUrl) {
        try {
            Log.i(TAG, "starting in background for " + fileUrl[0]);
            URL url = new URL(fileUrl[0]);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            int responseCode = httpConn.getResponseCode();

            // always check HTTP response code first
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return getFileNameForUrl(fileUrl[0]);
            }
        } catch (IOException ex) {
            return null;
        }
        return null;
    }

    private FileName getFileNameForUrl(String url) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(url, new HashMap<String, String>());
        String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        return new FileName(artist, title);
    }
}
