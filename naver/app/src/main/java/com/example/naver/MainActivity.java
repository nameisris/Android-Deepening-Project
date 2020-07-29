package com.example.naver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    ArrayList<HashMap<String, String>> array; // 스트링 키, 스트링 값
    MyAdapter ad;
    ListView list;
    ProgressDialog progressBar; // 프로그래스 바
    int display = 5;
    String query = "송중기";
    String url = "https://openapi.naver.com/v1/search/news.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("뉴스검색"); // 기본값인 뉴스검색으로 title 설정

        list = findViewById(R.id.list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HashMap<String, String> map = array.get(i);
                String title = map.get("title");
                String link = map.get("link");

                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("title", title); // intent와 함께 보낼 값으로 "title"이라는 이름으로 title값을 보냄
                intent.putExtra("link", link);
                startActivity(intent);
            }
        });

        Intent intent = getIntent(); // BookActivity에서 intent를 통해 메인액티비티로 이동해왔을 경우
        if(intent.getStringExtra("url")!=null){
            url = intent.getStringExtra("url");
            getSupportActionBar().setTitle(intent.getStringExtra("title"));
        }

        new NaverThread().execute();

        FloatingActionButton more = findViewById(R.id.more);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                display = display + 5;
                new NaverThread().execute(); // display가 5개씩 추가되며 스레드를 다시실행
            }
        });
    }

    // 네이버접속 스레드
    class NaverThread extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {
            // 아래의 메소드들을 실행하기 이전에 프로그래스 바가 현재 액티비티에 나오게 함 (로딩)
            progressBar = new ProgressDialog(MainActivity.this); // 현재 액티비티인 MainActivity인 this에서 프로그래스 바 생성
            progressBar.setMessage("검색중입니다...");
            progressBar.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = Naver.connect(display, query, url);
            // System.out.println("result : "+result); // 테스트
            return result;
        }

        @Override
        protected void onPostExecute(String s) { // doInBackground 메소드의 리턴결과가 s로 들어감
            parserJSON(s);
            // System.out.println("array 사이즈:" + array.size());
            ad = new MyAdapter(); // 어댑터 생성
            list.setAdapter(ad); // list에 어댑터 ad를 set
            super.onPostExecute(s);
            progressBar.dismiss();
        }
    }

    // 결과 파싱
    // 결과를 파싱하여 분석하여
    public void parserJSON(String result){ // result를 받아 파싱해주어 ArrayList에 넣어주기 위한 메소드
        array = new ArrayList<HashMap<String, String>>();
        try{
            JSONArray jArray = new JSONObject(result).getJSONArray("items"); // items의 값을 가져와 jarray에 넣어줌
            for(int i = 0;i < jArray.length();i++){ // 배열의 크기만큼 반복 (display는 기본값이 10이므로 10만큼 반복)
                // jArray의 i번째의 값들을 String형으로 가져옴
                JSONObject obj = jArray.getJSONObject(i);
                String title = obj.getString("title");
                String link = obj.getString("link");
                String description = obj.getString("description");

                // HashMap인 map에 값들을 넣어줌
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("title", title);
                map.put("link", link);
                map.put("description", description);
                // HashMap<String, String>형인 map을  ArrayList<HashMap<String, String>>형인 array에 넣어줌
                array.add(map);
            }
        }catch (Exception e){}
    }

    // 어댑터
    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return array.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) { // 내용 넣어주기
            view = getLayoutInflater().inflate(R.layout.item_news, null);
            TextView title = view.findViewById(R.id.title);
            TextView description = view.findViewById(R.id.description);

            HashMap<String, String> map = array.get(i); // array의 i번째 값을 넣어줌
            title.setText(Html.fromHtml(map.get("title"))); // map의 title에 대한 값을 가져와서 set
            description.setText(Html.fromHtml(map.get("description"))); // map의 description에 대한 값을 가져와서 set
            // Html.fromHtml.() 안에 넣어주어야 각종 <b>, &quot; 같은 태그들이 안붙음
            return view;
        }
    }

    // 옵션 메뉴 등록
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        // 검색 작업
        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView)search.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                query = s;
                display = 5;
                new NaverThread().execute();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.news:
                getSupportActionBar().setTitle("뉴스검색");
                url = "https://openapi.naver.com/v1/search/news.json";
                break;
            case R.id.cafe:
                getSupportActionBar().setTitle("카페검색");
                url = "https://openapi.naver.com/v1/search/cafearticle.json";
                break;
            case R.id.blog:
                getSupportActionBar().setTitle("블로그검색");
                url = "https://openapi.naver.com/v1/search/blog.json";
                break;
            case R.id.book:
                Intent intent = new Intent(MainActivity.this, BookActivity.class);
                startActivity(intent);
                break;
        }
        display =  5; // 검색결과 5개로 설정
        new NaverThread().execute(); // 스레드를 다시 실행 (새로 검색하기 위해)
        return super.onOptionsItemSelected(item);
    }
}