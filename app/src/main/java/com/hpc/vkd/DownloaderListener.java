package com.hpc.vkd;

import java.io.File;

/**
 * Class for listening downloader events through view
 */

interface DownloaderListener {

    void onFileNameReceived(String url, FileName fileName);

    void onLoadStarted(int notificationId, FileName fileName);

    void onLoadFinished(int notificationId, File file);
}
