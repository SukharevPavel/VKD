package com.hpc.vkd

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_file_name.*
import kotlinx.android.synthetic.main.dialog_file_name.view.*

import java.io.File

class MainActivity : AppCompatActivity(), DownloaderListener {

    private val notificationHelper = NotificationHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpWebView()
        Downloader.getInstance().subscribe(this)
    }

    private fun setUpWebView() {
        val webSettings = web_view.settings
        webSettings.javaScriptEnabled = true
        web_view.settings.useWideViewPort = true
        web_view.settings.loadWithOverviewMode = false
        web_view.webViewClient = object : WebViewClient() {

            override fun onLoadResource(view: WebView, url: String) {
                if (!url.contains(Constants.AUDIO_EXT)) {
                    super.onLoadResource(view, url)
                }
            }


            override fun shouldInterceptRequest(view: WebView, url: String): WebResourceResponse? {
                if (url.contains(Constants.AUDIO_EXT)) {
                    sendToastToMainThread()
                    Downloader.getInstance().getFileNameForUrl(url)
                    return WebResourceResponse(null, null, null)
                }
                return super.shouldInterceptRequest(view, url)
            }
        }
        web_view!!.loadUrl("https://vk.com")
    }

    override fun onDestroy() {
        super.onDestroy()
        Downloader.getInstance().unsubscribe()
    }

    private fun sendToastToMainThread() {
        Handler(Looper.getMainLooper()).post { Toast.makeText(this@MainActivity, getString(R.string.load_started_toast), Toast.LENGTH_SHORT).show() }
    }

    override fun onFileNameReceived(url: String, fileName: FileName) {
        if (fileName.isCompleted) {
            Downloader.getInstance().downloadFromUrl(url, fileName)
        } else {

            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.dialog_file_name, null)
            view.dialog_file_name_artist.setText(fileName.artist?:Constants.EMPTY_STRING)
            view.dialog_file_name_title.setText(fileName.title?:Constants.EMPTY_STRING)

            builder.setView(view)
            val dialog = builder.create()
            dialog.show()
            view.dialog_file_name_button.setOnClickListener {
                val resultFileName = FileName(view.dialog_file_name_artist.text.toString(),
                        view.dialog_file_name_title.text.toString())
                Downloader.getInstance().downloadFromUrl(url, resultFileName)
                dialog.dismiss()
            }

        }
    }

    override fun onLoadStarted(notificationId: Int, fileName: FileName) {
        notificationHelper.showLoadingNotification(notificationId, fileName.generateFileName())
    }

    override fun onLoadFinished(notificationId: Int, file: File) {
        notificationHelper.showLoadedNotification(notificationId, file)
    }

    companion object {

        private val TAG = MainActivity::class.java.simpleName
    }
}
