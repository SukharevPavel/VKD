package com.hpc.vkd

import java.io.File

/**
 * Class for listening downloader events through view
 */

interface DownloaderListener {

    fun onFileNameReceived(url: String, fileName: FileName)

    fun onLoadStarted(notificationId: Int, fileName: FileName)

    fun onLoadFinished(notificationId: Int, file: File)
}
