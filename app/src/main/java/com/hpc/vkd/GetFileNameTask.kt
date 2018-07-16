package com.hpc.vkd

import android.media.MediaMetadataRetriever
import android.os.AsyncTask
import android.util.Log

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.HashMap

/**
 * Created by hpc on 1/27/18.
 */
internal abstract class GetFileNameTask : AsyncTask<String, Void, FileName>() {

    private val TAG = GetFileNameTask::class.java.name

    override fun doInBackground(vararg fileUrl: String): FileName? {
        try {
            Log.i(TAG, "starting in background for " + fileUrl[0])
            val url = URL(fileUrl[0])
            val httpConn = url.openConnection() as HttpURLConnection
            val responseCode = httpConn.responseCode

            // always check HTTP response code first
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return getFileNameForUrl(fileUrl[0])
            }
        } catch (ex: IOException) {
            return null
        }

        return null
    }

    private fun getFileNameForUrl(url: String): FileName {
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(url, HashMap())
        val artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
        val title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        return FileName(artist, title)
    }
}
