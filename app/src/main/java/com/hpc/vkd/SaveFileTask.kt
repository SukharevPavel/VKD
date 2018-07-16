package com.hpc.vkd

import android.os.AsyncTask
import android.util.Log

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

/**
 * Created by hpc on 1/27/18.
 */
internal abstract class SaveFileTask : AsyncTask<FileParams, FileName, File>() {

    private val TAG = SaveFileTask::class.java.name

    override fun doInBackground(vararg params: FileParams): File? {
        try {
            Log.i(TAG, "starting in background for " + params[0].url)
            val url = URL(params[0].url)
            val httpConn = url.openConnection() as HttpURLConnection
            val responseCode = httpConn.responseCode

            Log.i(TAG, "made first connection for " + params[0].url)

            // always check HTTP response code first
            if (responseCode == HttpURLConnection.HTTP_OK) {
                doProgressUpdate(params[0].fileName)
                Log.i(TAG, "made progress update " + params[0].url)
                // opens input stream from the HTTP connection
                val inputStream = httpConn.inputStream
                var saveFilePath = params[0].path + File.separator + params[0].fileName.generateFileName()
                saveFilePath = FileUtils.getNotExistentFilePath(saveFilePath)
                // opens an output stream to save into file
                val outputStream = FileOutputStream(saveFilePath)

                var bytesRead : Int
                val buffer = ByteArray(1024)
                while (true){
                    bytesRead = inputStream.read(buffer);
                    if (bytesRead == -1){
                        break
                    }
                    outputStream.write(buffer, 0, bytesRead)

                }
                outputStream.close()
                inputStream.close()

                Log.i(TAG, "File downloaded to $saveFilePath")
                return File(saveFilePath)
            } else {
                Log.i(TAG, "No file to download. Server replied HTTP code: $responseCode")
            }
            httpConn.disconnect()
        } catch (ex: MalformedURLException) {
            Log.i(TAG, "malformed url ex = " + ex.message)
        } catch (ex: IOException) {
            Log.i(TAG, "i/o exception " + ex.message)
        }

        return null
    }


    override fun onPostExecute(file: File) {
        super.onPostExecute(file)
        doPostExecute(file)
    }

    override fun onProgressUpdate(vararg values: FileName) {
        super.onProgressUpdate(*values)
        doProgressUpdate(values[0])
    }

    abstract fun doProgressUpdate(name: FileName)

    abstract fun doPostExecute(result: File)

}
