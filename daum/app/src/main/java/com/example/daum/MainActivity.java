package com.example.daum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    String url = "https://dapi.kakao.com/v2/search/image"; // 검색어 검색을 실행하는 url
    String query = "설현"; // 검색어
    int size = 10; // size의 디폴트 값이 80이라 10으로 설정

    ArrayList<HashMap<String, String>> array;
    MyAdapter ad;
    ListView list;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progress = findViewById(R.id.progress);

        getSupportActionBar().setTitle("이미지검색");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 백버튼

        list = findViewById(R.id.list);
        new DaumThread().execute();

        FloatingActionButton more = findViewById(R.id.more);
        more.setOnClickListener(new View.OnClickListener() { // 플로팅 액션 버튼을 눌렀을 때
            @Override
            public void onClick(View view) {
                size += 10; // 사이즈를 10씩 증가 (검색 결과를 10개 더해줌)
                new DaumThread().execute(); // 스레드 재실행
            }
        });
    }

    // 카카오 스레드
    class DaumThread extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() { // 스레드의 메인 작업 수행 이전 메소드
            super.onPreExecute();
            progress.setVisibility(View.VISIBLE); // 검색을 하기 이전에 activity_main의 프로그래스 바를 보이게 함 (VISIBLE)
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = Daum.connect(url + "?query=" + query + "&size=" + size); // url, query, size를 넘겨줌 (변경된 값으로 실행해주기 위함)
            return result; // result를 화면에 출력하기 위해 아래의 parser메소드를 이용
        }

        @Override
        protected void onPostExecute(String s) { // doInBackGround() 메소드의 반환값인 result를 매개변수인 s로 받아와서 parser()메소드에 넣어줌
            parser(s);
            // System.out.println("데이터개수 : " + array.size());
            ad = new MyAdapter();
            list.setAdapter(ad);
            progress.setVisibility(View.INVISIBLE); // 검색이 끝난 이후에 프로그래스 바 안보이게 하기
            super.onPostExecute(s);
        }
    }

    // parserJSON
    // 가져온 데이터를 ArrayList인 array에 넣어주는 작업
    public void parser(String result){
        array = new ArrayList<>();
        // parsing을 할 때 에러가 날 수 있으므로 try catch문 사용
        try {
            JSONArray jArray = new JSONObject(result).getJSONArray("documents"); // result의 documents에 대한 값을 가져옴
            for(int i = 0;i < jArray.length();i++){
                JSONObject obj = jArray.getJSONObject(i);

                HashMap<String, String> map = new HashMap<>();
                map.put("name", obj.getString("display_sitename")); // 사이트 이름
                map.put("thumb", obj.getString("thumbnail_url")); // 썸네일
                map.put("link", obj.getString("doc_url")); // 링크 주소
                map.put("image", obj.getString("image_url")); // 원본 이미지

                array.add(map); // HashMap<> 형인 map을 넣어줌
            }
        } catch(Exception e) {}
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
            view = getLayoutInflater().inflate(R.layout.item_image, null); // item_image.xml을 펼처서 view로 줌

            // 리스트뷰 아이디 가져오기
            TextView name = view.findViewById(R.id.name);
            ImageView thumb = view.findViewById(R.id.thumb);

            // name set 해주기
            final HashMap<String, String> map = array.get(i); // array로부터 데이터 가져옴
            name.setText(map.get("name")); // array에서 가져온 데이터가 들어간 map에서 또 "name"으로 name의 text를 set

            Picasso.with(MainActivity.this).load(map.get("thumb")).into(thumb);

            // 썸네일 클릭 리스너
            thumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LinearLayout layout = (LinearLayout)getLayoutInflater().inflate(R.layout.item_bigimage, null);
                    // item_bigimage.xml을 LinearLayout으로 형변환하여 받아옴

                    ImageView bigImage = layout.findViewById(R.id.bigimage);
                    Picasso.with(MainActivity.this).load(map.get("image")).into(bigImage);

                    AlertDialog.Builder box = new AlertDialog.Builder(MainActivity.this);
                    box.setTitle("이미지");
                    box.setPositiveButton("확인", null);
                    box.setView(layout); // box의 view를 layout으로 set
                    box.show();
                }
            });
            return view;
        }
    }

    // Search 메뉴
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem search = menu.findItem(R.id.search); // item 가져옴
        SearchView searchView = (SearchView)search.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) { // 검색
                query = s; // 입력한 텍스트 값이 들어있는 s
                size = 10;
                new DaumThread().execute();
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
        Intent intent;
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.local:
                intent = new Intent(MainActivity.this, LocalActivity.class);
                startActivity(intent);
                break;
            case R.id.blog:
                break;
            case R.id.image:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}