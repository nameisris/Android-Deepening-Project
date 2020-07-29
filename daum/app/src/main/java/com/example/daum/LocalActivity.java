package com.example.daum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class LocalActivity extends AppCompatActivity {
    String url = "https://dapi.kakao.com/v2/local/search/keyword.json";
    String query = "선문대학교";
    int size = 10;
    ArrayList<HashMap<String, String>> array;
    MyAdapter ad;
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // activity_main.xmml에서 만든 리스트뷰에 대한 레이아웃을 그대로 사용

        getSupportActionBar().setTitle("지역검색");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 백 버튼

        list = findViewById(R.id.list);

        new DaumThread().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu); // menu의 main.xml을 이용해 메뉴 생성
        MenuItem search = menu.findItem(R.id.search); // menu의 아이템인 search 아이디를 받아옴
        SearchView searchView = (SearchView)search.getActionView(); // search의 액션뷰를 받아옴
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) { // 검색 (s에는 입력한 문자열이 들어있음)
                query = s; // 검색어 재설정
                size = 10; // 사이즈 재설정 (검색 작업 이전에 항목 더보기 작업을 했을 수도 있기에)
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
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // 카카오 스레드
    class DaumThread extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            String result = Daum.connect(url + "?query=" + query + "&size=" + size);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            parser(s); // parser() 메소드를 통해 doInBackground에서 검색한 데이터를 array에 넣어줌
            // System.out.println("데이터 개수 : " + array.size());
            ad = new MyAdapter();
            list.setAdapter(ad);
            super.onPostExecute(s);
        }
    }

    // parserJSON
    // 가져온 데이터를 ArrayList인 array에 넣어주는 작업
    public void parser(String result){
        array = new ArrayList<>();
        // parsing을 할 때 에러가 날 수 있으므로 try catch문 사용
        try {
            JSONArray jArray = new JSONObject(result).getJSONArray("documents"); // result의 documents를 jArray에 받아옴

            for(int i = 0;i < jArray.length();i++){
                JSONObject obj = jArray.getJSONObject(i);

                HashMap<String, String> map = new HashMap<>();
                map.put("name", obj.getString("place_name")); // 장소명
                map.put("address", obj.getString("address_name")); // 주소명
                map.put("tel", obj.getString("phone")); // 전화번호
                map.put("x", obj.getString("x")); // x 좌표
                map.put("y", obj.getString("y")); // y 좌표

                array.add(map); // HashMap<> 형인 map을 넣어줌
            }
        } catch(Exception e) {}
    }

    // 어댑터
    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return array.size(); // array의 size인 10개
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
            view = getLayoutInflater().inflate(R.layout.item_local, null);

            // 리스트뷰 항목 아이디 가져오기
            TextView name = view.findViewById(R.id.name);
            TextView address = view.findViewById(R.id.address);
            TextView tel = view.findViewById(R.id.tel);

            // 각 항목 텍스트 set
            final HashMap<String, String> map = array.get(i); // array의 i번째 데이터 가져옴
            name.setText(map.get("name"));
            address.setText(map.get("address"));
            tel.setText(map.get("tel"));

            // 전화 걸기 이벤트 (tel을 클릭했을때)
            tel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri number = Uri.parse("tel:" + map.get("tel"));
                    Intent intent = new Intent(Intent.ACTION_DIAL, number); // tel의 데이터가 들어간 number를 intent에 넣어줌
                    startActivity(intent);
                }
            });

            // 지도 검색 이벤트
            ImageView maps = view.findViewById(R.id.map);
            maps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(LocalActivity.this, MapsActivity.class);
                    intent.putExtra("x", map.get("x")); // 경도 (map으로부터 x 받아옴)
                    intent.putExtra("y", map.get("y")); // 위도
                    intent.putExtra("title", map.get("name"));
                    startActivity(intent);
                }
            });
            return view;
        }
    }
}