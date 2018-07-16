package com.hpc.vkd;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity implements DownloaderListener {

    private final static String TAG = MainActivity.class.getSimpleName();

    private WebView webView;
    private NotificationHelper notificationHelper = new NotificationHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpWebView();
        Downloader.getInstance().subscribe(this);
    }

    private void setUpWebView() {
        webView = (WebView) findViewById(R.id.web_view);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(false);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i(TAG, "override url = " + url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.i(TAG,"on page started " + url);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                Log.i(TAG,"on load resource " + url);
                if (!url.contains(Constants.AUDIO_EXT)) {
                    super.onLoadResource(view, url);
                }
            }



            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                Log.i(TAG, "intercept url = " + url);
                if (url.contains(Constants.AUDIO_EXT)) {
                    Log.i(TAG, "catch audio url = " + url);
                    sendToastToMainThread();
                   Downloader.getInstance().getFileNameForUrl(url);
                    return new WebResourceResponse(null,null,null);
                }
                return super.shouldInterceptRequest(view, url);
            }
        });
        webView.loadUrl("https://vk.com");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Downloader.getInstance().unsubscribe();
    }

    private void sendToastToMainThread() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, getString(R.string.load_started_toast), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onFileNameReceived(final String url, FileName fileName) {
        if (fileName != null && fileName.isCompleted()) {
            Downloader.getInstance().downloadFromUrl(url, fileName);
        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = getLayoutInflater().inflate(R.layout.dialog_file_name, null);
            Button button = view.findViewById(R.id.dialog_file_name_button);
            final EditText artistEditText = view.findViewById(R.id.dialog_file_name_artist);
            final EditText titleEditText = view.findViewById(R.id.dialog_file_name_title);
            if (fileName != null) {
                artistEditText.setText(fileName.getArtist());
                titleEditText.setText(fileName.getTitle());
            }

            builder.setView(view);
            final AlertDialog dialog = builder.create();
            dialog.show();
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FileName fileName= new FileName(artistEditText.getText().toString(),
                            titleEditText.getText().toString());
                    Downloader.getInstance().downloadFromUrl(url, fileName);
                    dialog.dismiss();
                }
            });

        }
    }

    @Override
    public void onLoadStarted(int notificationId, FileName fileName) {
        notificationHelper.showLoadingNotification(notificationId, fileName.generateFileName());
    }

    @Override
    public void onLoadFinished(int notificationId, File file) {
        notificationHelper.showLoadedNotification(notificationId, file);
    }
}
