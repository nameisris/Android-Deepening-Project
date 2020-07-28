package com.example.naver;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new NaverThread().execute();
    }

    // 네이버접속 스레드
    class NaverThread extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {
            Naver.connect();
            return null;
        }
    }
}