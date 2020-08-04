package com.example.crawling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    ArrayList<HashMap<String, String>> arrayCGV = new ArrayList<>();
    ArrayList<HashMap<String, String>> arrayDaum = new ArrayList<>();
    CGVAdapter cgvAdapter;
    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // CGV 스레드 생성
        new CGVThread().execute();
        // Daum 스레드 생성
        new DaumThread().execute();
    }

    // CGV 스레드
    class CGVThread extends AsyncTask<String, String, String> {

         @Override
         protected String doInBackground(String... strings) { // 전부 jsoup으로 Alt+Enter
             try {
                 // 해당 링크의 페이지에 출력되는 값들을
                 // HashMap<String, String>형인 map에 더해주는 작업을 한 뒤
                 // ArrayList<HashMap<String, String>>형인 arrayCGV에 map을 더해줌
                 Document doc = Jsoup.connect("http://www.cgv.co.kr/movies").get();
                 Elements elements = doc.select(".sect-movie-chart ol"); // ol 안의
                 for (Element e : elements.select("li")) { // li 안의
                     HashMap<String, String> map = new HashMap<String, String>();
                     map.put("rank", e.select(".rank").text());
                     map.put("title", e.select(".title").text());
                     map.put("image", e.select("img").attr("src"));
                     if (!e.select(".rank").text().equals("")) {
                         arrayCGV.add(map);
                     }
                 }
             } catch (Exception e) {
                 System.out.println(e.toString());
             }
             return null;
         }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // System.out.println("데이터갯수 : " + arrayCGV.size());

            // 리사이어클 뷰 생성
            RecyclerView listCGV = findViewById(R.id.listCGV);
            listCGV.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            // 어댑터 생성
            cgvAdapter = new CGVAdapter(MainActivity.this, arrayCGV);
            // 어댑터 연결
            listCGV.setAdapter(cgvAdapter);
        }

    }

    // 다음 날씨 스레드
    class DaumThread extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                // 해당 링크의 페이지에 출력되는 값들을
                // HashMap<String, String>형인 map에 더해주는 작업을 한 뒤
                // ArrayList<HashMap<String, String>>형인 arrayDaum에 map을 더해줌
                HashMap<String, Object> object=new HashMap<String, Object>();
                Document doc=Jsoup.connect("http://www.daum.net").get();
                Elements elements=doc.select(".list_weather"); // list_weahter 안의
                for(Element e:elements.select("li")) { // li 안의 (li가 여러 개이므로 for문 사용)
                    HashMap<String, String> map=new HashMap<String, String>();
                    map.put("part", e.select(".txt_part").text()); // part라는 이름으로 txt_part 값을 select
                    map.put("temper", e.select(".txt_temper").text());
                    map.put("wa", e.select(".ir_wa").text());
                    map.put("ico", e.select(".ico_ws").text());
                    arrayDaum.add(map);
                }
            }catch(Exception e) {
                System.out.println(e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // System.out.println("날씨데이터갯수 : " + arrayDaum.size());
            getSupportActionBar().setTitle("다음날씨");

            BackThread backThread = new BackThread();
            backThread.setDaemon(true);
            backThread.start();
        }
    }

    // 백 스레드
    class BackThread extends Thread{
        @Override
        public void run() {
            super.run();
            index=0;
            while(true){
                handler.sendEmptyMessage(0);
                index++;
                if(index==arrayDaum.size()){ // 만약 arrayDaum의 사이즈와 같아진다면
                    index=0; // 인덱스를 0으로 초기화 (arrayDaum.size()번째 인덱스까지 반복하고 다시 0번 인덱스부터 반복 시작)
                }
                try{Thread.sleep(3000);}catch(Exception e){}
            }
        }
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            String part=arrayDaum.get(index).get("part");
            String temper=arrayDaum.get(index).get("temper");
            String ico=arrayDaum.get(index).get("ico");
            getSupportActionBar().setTitle(part + temper + "도/ "+ ico);
        }
    };
}