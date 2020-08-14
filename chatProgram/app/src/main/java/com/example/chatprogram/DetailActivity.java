package com.example.chatprogram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.security.ProtectionDomain;

public class DetailActivity extends AppCompatActivity {
    ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        String strTitle = intent.getStringExtra("title"); // 받아온 intent의 "title"이라는 이름의 데이터에서 String으로 값을 가져옴
        String strLink = intent.getStringExtra("link");

        TextView title = findViewById(R.id.title);
        title.setText(Html.fromHtml(strTitle)); //

        progressBar = new ProgressDialog(this); // 현재 액티비티인 this에 프로그래스 바 생성
        progressBar.setMessage("페이지를 불러오는 중입니다...");
        progressBar.show();

        WebView web = (WebView)findViewById(R.id.web);
        web.setWebViewClient(new MyWebView());
        web.loadUrl(strLink);
    }

    public class MyWebView extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        public void onPageFinished(WebView view, String url) {
            progressBar.dismiss();
            super.onPageFinished(view, url);
        }
    }
}