package com.example.ex06;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.security.ProtectionDomain;

public class WebActivity extends AppCompatActivity {
    ProgressDialog progress;
    WebSettings webSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        Intent intent = getIntent();
        String link = intent.getStringExtra("link");
        String title = intent.getStringExtra("title");

        getSupportActionBar().setTitle(Html.fromHtml(link));

        TextView text = (TextView)findViewById(R.id.title);
        text.setText(Html.fromHtml(title));

        WebView web = (WebView)findViewById(R.id.web);
        web.setWebViewClient(new MyWebView());

        // 프로그래스 바
        progress = new ProgressDialog(this);
        progress.setMessage("페이지를 불러오는 중입니다.");
        progress.show();

        webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);
        web.loadUrl(link);
    }

    public class MyWebView extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String link) {
            return super.shouldOverrideUrlLoading(view, link);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            progress.dismiss();
            super.onPageFinished(view, url);
        }
    }
}