package com.hpc.vkd;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class what makes all the work for downloading the song
 */

public final class Downloader {

    private final static String TAG = Downloader.class.getName();
    static final String FILE_PROVIDER_AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";
    private static AtomicInteger notification_id = new AtomicInteger();
    private static int THREAD_COUNT = 6;
    private static ThreadPoolExecutor NETWORK_POOL_EXECUTOR = new ThreadPoolExecutor(THREAD_COUNT,
            THREAD_COUNT, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(128));

    private int getNotificationId() {
        return notification_id.incrementAndGet();
    }

    private static Downloader instance;
    private DownloaderListener listener;

    static Downloader getInstance(){
        if (instance == null) {
            instance = new Downloader();
        }
        return instance;
    }

    private Downloader(){
    }

    void subscribe(DownloaderListener listener) {
        this.listener = listener;
    }

    void unsubscribe(){
        this.listener = null;
    }


    @SuppressLint("StaticFieldLeak")
    void getFileNameForUrl(final String fileUrl){
         GetFileNameTask task = new GetFileNameTask() {
            @Override
            protected void onPostExecute(FileName fileName) {
                super.onPostExecute(fileName);
                if (listener != null) {
                    listener.onFileNameReceived(fileUrl, fileName);
                }
            }
        };
        task.execute(fileUrl);
    }

    @SuppressLint("StaticFieldLeak")
    public void downloadFromUrl(final String fileUrl, final FileName fileName) {
        FileParams params = new FileParams(fileUrl,getSaveDir(),fileName);
        Log.i(TAG,this + "getting id");
        final int notificationId = getNotificationId();
        Log.i(TAG,this + "making task for " + fileUrl);
        SaveFileTask task = new SaveFileTask() {
            @Override
            public void doProgressUpdate(FileName name) {
                listener.onLoadStarted(notificationId, name);
            }

            @Override
            public void doPostExecute(File result) {
                listener.onLoadFinished(notificationId, result);
            }
        };
        Log.i(TAG,this + " executing task for " + fileUrl);
        task.executeOnExecutor(NETWORK_POOL_EXECUTOR, params);
    }

    private String getSaveDir() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath();
    }


}
