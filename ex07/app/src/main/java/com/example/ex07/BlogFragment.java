package com.example.ex07;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class BlogFragment extends Fragment {
    EditText edtSearch;
    String url = "https://dapi.kakao.com/v2/search/blog";
    String query = "선문대";
    int page = 1;
    int total = 0; // 검색 데이터 건수 (기본값으로 0 설정)
    boolean is_end; //  페이지의 마지막 여부를 알 수 있는 변수
    ArrayList<HashMap<String, String>> arrayBlog = new ArrayList<>(); // 파싱한 데이터가 들어갈 ArrayList 생성 (HashMap에는 String형으로 키와 값이 들어감)
    BlogAdapter blogAdapter = new BlogAdapter();
    RecyclerView listBlog; // 출력할 RecyclerView의 아이디
    TextView txtTotal; // 검색 수에 대한 토탈 값을 위한 TextView형 값
    ImageView btnSearch;
    FloatingActionButton btnMore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blog, container, false);
        edtSearch = view.findViewById(R.id.edtSearch);
        edtSearch.setHint("블로그 검색");

        listBlog = view.findViewById(R.id.listBlog);
        listBlog.setLayoutManager(new LinearLayoutManager(getActivity())); // 리사이클러뷰를 이용하므로 매니저를 등록해줘야 함
        listBlog.setAdapter(blogAdapter);

        txtTotal = view.findViewById(R.id.txtTotal); // 아이디 읽어옴

        // 버튼 설정
        btnSearch = view.findViewById(R.id.btnSearch);
        btnMore = view.findViewById(R.id.btnMore);

        // 검색 버튼 클릭 리스너
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrayBlog.clear(); // 배열을 null로 초기화하고 size를 0으로 설정
                // 검색어를 입력한 query와 1로 초기화한 page를 통해 다시 스레드를 실행하기 이전에 배열을 clear해줌
                query = edtSearch.getText().toString();
                page = 1;
                new KakaoThread().execute();
            }
        });

        // 더보기 버튼 클릭 리스너
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(is_end){ // 마지막 페이지가 맞다면 (true)
                    Toast.makeText(getActivity(), "마지막 페이지입니다.", Toast.LENGTH_SHORT).show();
                }
                else{ // 아니라면 (false)
                    page++;
                    new KakaoThread().execute();
                }
            }
        });

        // 스레드 생성
        new KakaoThread().execute();
        return view;
    }

    // 카카오 접속 스레드
    class KakaoThread extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String s = Kakao.connect(url + "?query=" + query + "&page=" + page);
            return s;
        }

        @Override
        protected void onPostExecute(String s) { // doInBackGround에서 반환한 s를 매개변수로
            super.onPostExecute(s);
            blogParsing(s); // s를 통해 파싱
            blogAdapter.notifyDataSetChanged();
            listBlog.scrollToPosition(arrayBlog.size()-1);
            // 현재 arrayBlog 사이즈에서 1만큼 뺀 위치로 스크롤을 이동 (즉, 마지막으로 이동)
            txtTotal.setText("검색 수 : " + total + " / 마지막 페이지" + is_end); // 검색 수와 마지막 페이지 여부에 대한 텍스트 set
        }
    }

    // 파싱
    public void blogParsing(String s){ // 파싱할 데이터 s
        try {
            // 데이터에 들은 토탈 값과 마지막 여부 값을 가져옴
            JSONObject jObject = new JSONObject(s).getJSONObject("meta"); // s를 가지고 파싱
            total = jObject.getInt("total_count");
            is_end = jObject.getBoolean("is_end");

            // 데이터 가져오기
            JSONArray jArray = new JSONObject(s).getJSONArray("documents"); // s를 가지고 파싱
            for(int i = 0;i < jArray.length();i++){
                JSONObject obj = jArray.getJSONObject(i);

                HashMap<String, String> map = new HashMap<>();
                map.put("title", obj.getString("title")); // HashMap<>형인 map에 데이터 더해줌
                map.put("url", obj.getString("url"));
                arrayBlog.add(map); // 데이터가 더해진 map을 이번엔 arrayBlog에 더해줌
            }
        }catch (Exception e) {}
    }

    // 블로그 어댑터
    class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.ViewHolder>{

        @NonNull
        @Override
        public BlogAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.item_blog, parent, false);
            // Fragment이므로 레이아웃보다 Activity를 먼저 가져와야 함
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BlogAdapter.ViewHolder holder, int position) { // 아이디에 값을 넣어주는 메소드

            final HashMap<String, String> map = arrayBlog.get(position);
            holder.title.setText(Html.fromHtml(map.get("title"))); // map의 "title"이란 이름의 데이터를 title의 텍스트로 set
            holder.link.setText(Html.fromHtml(map.get("url")));
            // layout 클릭 리스너
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), WebActivity.class); // getActivity()로 구한 현재 액티비티에서 WebActivity로
                    intent.putExtra("url", map.get("url")); // map을 사용하기 위해 map에 final 선ㅇ너
                    intent.putExtra("title", map.get("title"));
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return arrayBlog.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder { // 아이디를 가져오는메소드
            TextView title, link;
            LinearLayout layout;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.title);
                link = itemView.findViewById(R.id.link);
                layout = itemView.findViewById(R.id.layout);
            }
        }
    }
}