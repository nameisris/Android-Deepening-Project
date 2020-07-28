package com.example.naver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class BookActivity extends AppCompatActivity {
    ArrayList<HashMap<String, String>> array; // HashMap에는 키와 값이 들어감
    int display = 10;
    String query = "안드로이드";
    String url = "https://openapi.naver.com/v1/search/book.json";
    MyAdapter ad;
    ListView list;
    ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        getSupportActionBar().setTitle("도서검색");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 백 버튼 생성

        list = findViewById(R.id.list);

        // 스레드 생성
        new NaverThread().execute();

        // 액션 버튼 클릭 리스너
        FloatingActionButton more = findViewById(R.id.more);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                display += 10;
                new NaverThread().execute();
            }
        });
    }

    // 네이버 접속 스레드
    class NaverThread extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {
            progressBar = new ProgressDialog(BookActivity.this);
            progressBar.setMessage("검색중입니다...");
            progressBar.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = Naver.connect(display, query, url); // 갯수, 검색어, 링크
            // System.out.println("result: " + result); // 테스트용 출력. Run에서 확인 가능.
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            parseJSON(s);
            // System.out.println("array 갯수 : " + array.size()); // 테스트용 출력
            ad = new MyAdapter();
            list.setAdapter(ad);
            super.onPostExecute(s);
            progressBar.dismiss();
        }
    }

    // parserJSON
    public void parseJSON(String result) {
        array = new ArrayList<HashMap<String, String>>();
        try {
            JSONArray jArray=new JSONObject(result).getJSONArray("items"); // items를 받아옴
            for(int i=0;i<jArray.length(); i++){
                HashMap<String,String> map=new HashMap<>();

                JSONObject obj=jArray.getJSONObject(i);
                map.put("title", obj.getString("title"));
                map.put("author", obj.getString("author"));
                map.put("price", obj.getString("price"));
                map.put("image", obj.getString("image"));
                map.put("description", obj.getString("description"));
                map.put("publisher", obj.getString("publisher"));
                map.put("pubdate", obj.getString("pubdate"));

                array.add(map);
            }
        } catch (Exception e) { }
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
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.item_book, null);
            TextView title = view.findViewById(R.id.title);
            TextView price = view.findViewById(R.id.price);
            TextView author = view.findViewById(R.id.author);

            final HashMap<String, String> map = array.get(i);
            title.setText(Html.fromHtml(map.get("title")));
            price.setText(Html.fromHtml(map.get("price") + "원"));
            author.setText(Html.fromHtml(map.get("author")));

            // 책 표지
            ImageView image = view.findViewById(R.id.image);
            Picasso.with(BookActivity.this).load(map.get("image")).into(image);

            // 돋보기 버튼 리스너 (list와 연결되어야 하므로 어댑터 클래스 안의 getView 메소드 안에서 선언?)
            ImageView view1 = view.findViewById(R.id.view);
            view1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(BookActivity.this, BookDetailActivity.class);
                    intent.putExtra("title", map.get("title"));
                    intent.putExtra("author", map.get("author"));
                    intent.putExtra("price", map.get("price"));
                    intent.putExtra("description", map.get("description"));
                    intent.putExtra("image", map.get("image"));
                    intent.putExtra("publisher", map.get("publisher"));
                    intent.putExtra("pubdate", map.get("pubdate"));
                    startActivity(intent);
                }
            });
            return view;
        }
    }

    // 옵션 메뉴
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView)search.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() { // 검색 버튼 리스너
            @Override
            public boolean onQueryTextSubmit(String s) { // s는 검색창을 통해 입력받은 값
                display = 10;
                query = s;
                new NaverThread().execute(); // 변경된 display와 query에 대한 스레드 생성 (새로운 책 리스트 10개 생성)
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
        Intent intent = new Intent(BookActivity.this, MainActivity.class);
        switch(item.getItemId()){
            case android.R.id.home: // 백버튼일 경우
                finish();
                break;
            case R.id.news:
                intent.putExtra("url", "https://openapi.naver.com/v1/search/news.json");
                break;
            case R.id.cafe:
                intent.putExtra("url", "https://openapi.naver.com/v1/search/cafearticle.json");
                break;
            case R.id.blog:
                intent.putExtra("url", "https://openapi.naver.com/v1/search/blog.json");
                break;
            case R.id.book:
                break;
        }
        startActivityForResult(intent, 0);
        return super.onOptionsItemSelected(item);
    }
}