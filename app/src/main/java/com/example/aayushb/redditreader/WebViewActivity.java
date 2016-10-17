package com.example.aayushb.redditreader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN); //hides status and title bars
        setContentView(R.layout.activity_web_view);

        String targetLink = "https://www.reddit.com/";

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            targetLink = extras.getString("targetLink");
        }

        WebView linksWebView = (WebView) findViewById(R.id.linksWebView);

        linksWebView.getSettings().setJavaScriptEnabled(true);
        linksWebView.getSettings().setSupportZoom(true);
        linksWebView.getSettings().setBuiltInZoomControls(true);
        linksWebView.setWebViewClient(new WebViewClient());
        linksWebView.loadUrl(targetLink);

    }

}
