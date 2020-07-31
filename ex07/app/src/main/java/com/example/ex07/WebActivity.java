package com.example.ex07;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class WebActivity extends AppCompatActivity {
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        progressBar = findViewById(R.id.progress);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String url = intent.getStringExtra("url");

        getSupportActionBar().setTitle(Html.fromHtml(title)); // intent에서 받아온 title을 액션바의 타이틀로 설정
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 백 버튼

        WebView web = findViewById(R.id.web);
        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true); // 이거를 해줘야 정상적으로 웹 뷰가 나옴

        web.setWebViewClient(new MyWebView()); // WebView형 web에 만들어둔 MyWebView를 등록
        web.loadUrl(url); // url의 링크를 load
    }

    class MyWebView extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageFinished(WebView view, String url) { // 페이지가 finish 되었을 때
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home: // 백 버튼을 클릭했을 때
                finish(); // 현재 액티비티를 finish (이전 액티비티로 돌아감)
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}