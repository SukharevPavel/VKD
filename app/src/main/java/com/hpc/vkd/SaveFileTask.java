package com.hpc.vkd;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by hpc on 1/27/18.
 */
abstract class SaveFileTask extends AsyncTask<FileParams, FileName, File> {

    private String TAG = SaveFileTask.class.getName();

    @Override
    protected File doInBackground(FileParams... params) {
        try {
            Log.i(TAG, "starting in background for " + params[0].getUrl());
            URL url = new URL(params[0].getUrl());
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            int responseCode = httpConn.getResponseCode();

            Log.i(TAG, "made first connection for " + params[0].getUrl());

            // always check HTTP response code first
            if (responseCode == HttpURLConnection.HTTP_OK) {
                doProgressUpdate(params[0].getFileName());
                Log.i(TAG, "made progress update " + params[0].getUrl());
                // opens input stream from the HTTP connection
                InputStream inputStream = httpConn.getInputStream();
                String saveFilePath = params[0].getPath() + File.separator + params[0].getFileName().generateFileName();
                saveFilePath = FileUtils.getNotExistentFilePath(saveFilePath);
                // opens an output stream to save into file
                FileOutputStream outputStream = new FileOutputStream(saveFilePath);

                int bytesRead = -1;
                byte[] buffer = new byte[1024];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.close();
                inputStream.close();

                Log.i(TAG, "File downloaded to " + saveFilePath);
                return new File(saveFilePath);
            } else {
                Log.i(TAG, "No file to download. Server replied HTTP code: " + responseCode);
            }
            httpConn.disconnect();
        } catch (MalformedURLException ex) {
            Log.i(TAG, "malformed url ex = " + ex.getMessage());
        } catch (IOException ex) {
            Log.i(TAG, "i/o exception " + ex.getMessage());
        }
        return null;
    }


    @Override
    protected void onPostExecute(File file) {
        super.onPostExecute(file);
        doPostExecute(file);
    }

    @Override
    protected void onProgressUpdate(FileName... values) {
        super.onProgressUpdate(values);
        doProgressUpdate(values[0]);
    }

    public abstract void doProgressUpdate(FileName name);

    public abstract void doPostExecute(File result);

}
