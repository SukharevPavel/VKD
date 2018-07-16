package com.hpc.vkd

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Environment
import android.util.Log

import java.io.File
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * Class what makes all the work for downloading the song
 */

class Downloader private constructor() {

    private val notificationId: Int
        get() = notification_id.incrementAndGet()
    private var listener: DownloaderListener? = null

    private val saveDir: String
        get() = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).absolutePath

    internal fun subscribe(listener: DownloaderListener) {
        this.listener = listener
    }

    internal fun unsubscribe() {
        this.listener = null
    }


    @SuppressLint("StaticFieldLeak")
    internal fun getFileNameForUrl(fileUrl: String) {
        val task = object : GetFileNameTask() {
            override fun onPostExecute(fileName: FileName) {
                super.onPostExecute(fileName)
                listener?.onFileNameReceived(fileUrl, fileName)
            }
        }
        task.execute(fileUrl)
    }

    @SuppressLint("StaticFieldLeak")
    fun downloadFromUrl(fileUrl: String, fileName: FileName) {
        val params = FileParams(fileUrl, saveDir, fileName)
        Log.i(TAG, this.toString() + "getting id")
        val notificationId = notificationId
        Log.i(TAG, this.toString() + "making task for " + fileUrl)
        val task = object : SaveFileTask() {
            override fun doProgressUpdate(name: FileName) {
                listener?.onLoadStarted(notificationId, name)
            }

            override fun doPostExecute(result: File) {
                listener?.onLoadFinished(notificationId, result)
            }
        }
        Log.i(TAG, this.toString() + " executing task for " + fileUrl)
        task.executeOnExecutor(NETWORK_POOL_EXECUTOR, params)
    }

    companion object {

        private val TAG = Downloader::class.java.name
        internal val FILE_PROVIDER_AUTHORITY = BuildConfig.APPLICATION_ID + ".provider"
        private val notification_id = AtomicInteger()
        private val THREAD_COUNT = 6
        private val NETWORK_POOL_EXECUTOR = ThreadPoolExecutor(THREAD_COUNT,
                THREAD_COUNT, 0, TimeUnit.SECONDS, LinkedBlockingQueue(128))

        private var instance = Downloader()

        internal fun getInstance(): Downloader {
            return instance
        }
    }


}
