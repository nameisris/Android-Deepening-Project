package com.example.ex06;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class BlogFragment extends Fragment {
    EditText edtSearch;
    String url = "https://dapi.kakao.com/v2/search/blog"; // 블로그 검색 url
    String query = "선문대학교"; // 초기 검색어
    int size = 10; // 검색 결과 갯수
    ArrayList<HashMap<String, String>> array; // 검색한 값이 들어갈 ArrayList

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blog, container, false);

        edtSearch = view.findViewById(R.id.edtSearch);
        edtSearch.setHint("블로그 검색어");

        // 카카오 스레드 실행 (AndoirdManifest.xml에서 인터넷 펄미션 줘야 실행가능)
        new KakaoThread().execute();

        // 더보기 버튼
        ImageView btnMore = view.findViewById(R.id.btnMore);
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                size = size + 10;
                new BlogFragment.KakaoThread().execute();
            }
        });

        // 검색 버튼
        ImageView btnSearch = view.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                query = edtSearch.getText().toString();
                size = 10;
                new BlogFragment.KakaoThread().execute();
            }
        });
        return view;
    }

    // 카카오 스레드
    class KakaoThread extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {
            String s = Kakao.connect(url + "?query=" + query + "&size=" + size);
            return s;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            blogParser(s);

            // 파싱작업 후에 뷰 생성
            BlogAdapter ad = new BlogAdapter();
            RecyclerView list = getActivity().findViewById(R.id.list_blog); // 액티비이의 id를 가져옴
            list.setAdapter(ad);
            list.setLayoutManager(new LinearLayoutManager(getActivity()));
            // System.out.println("데이터 개수 : " + array.size());
        }
    }

    // 데이터 파싱
    public void blogParser(String s){
        array = new ArrayList<>(); // ArrayList 생성
        try {
            JSONArray jArray = new JSONObject(s).getJSONArray("documents"); // documents에 들어가는 값들을 가져옴

            for(int i = 0;i < jArray.length();i++){ // documents로부터 데이터를 받은 jArray의 길이만큼 반복
                JSONObject obj = jArray.getJSONObject(i); // jArray의 i번째 값을 obj로 받아옴

                HashMap<String, String> map = new HashMap<>(); // 두 개의 String형 값이 들어갈 HashMap형 배열?변수 map 생성
                map.put("title", obj.getString("title")); // obj의 "title"의 값을 "title"이란 이름으로 받아옴
                map.put("link", obj.getString("url")); // obj의 "url"의 값을 "link"란 이름으로 받아옴
                array.add(map); // array에 map의 값들을 add
            }
        }catch (Exception e) {}
    }

    // 블로그 어댑터 (파싱한 데이터를 리스트뷰에 넣어줌?)
    class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.ViewHolder>{

        @NonNull
        @Override
        public BlogAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_blog, parent, false); // item_blog를 가져옴?
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final BlogAdapter.ViewHolder holder, int position) {
            HashMap<String, String> map = array.get(position); // map에 array의 값을 줌
            holder.title.setText(Html.fromHtml(map.get("title"))); // array의 값을 받은 map에서의 값을 텍스트로 set
            holder.link.setText(Html.fromHtml(map.get("link"))); // Html.fromHtml() 이용하면 출력되는 텍스트의 태그들을 제거 가능

            holder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), WebActivity.class);
                    intent.putExtra("link", holder.link.getText().toString());
                    intent.putExtra("title", holder.title.getText().toString());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return array.size(); // array.size()만큼 뷰폴더를 만들기 위해
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView title, link;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                // 아이디 값
                title = itemView.findViewById(R.id.title);
                link = itemView.findViewById(R.id.link);
            }
        }
    }
}