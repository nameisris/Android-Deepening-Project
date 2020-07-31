package com.example.ex07;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class BookFragment extends Fragment {
    EditText edtSearch;
    TextView txtTotal;
    ImageView btnSearch;
    FloatingActionButton btnMore;
    RecyclerView listBook;
    BookAdapter bookAdapter = new BookAdapter(); // 책 어댑터 생성

    String url = "https://dapi.kakao.com/v3/search/book?target=title";
    String query = "안드로이드";
    int page = 1;
    int total = 0;
    boolean is_end;
    ArrayList<HashMap<String, String>> arrayBook = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book, container, false);
        edtSearch = view.findViewById(R.id.edtSearch);
        edtSearch.setHint("도서 검색");

        txtTotal = view.findViewById(R.id.txtTotal);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnMore = view.findViewById(R.id.btnMore);
        listBook = view.findViewById(R.id.listBook);
        // listBook에 레이아웃매니저 set해주고 도서 어댑터 set해줌
        listBook.setLayoutManager(new LinearLayoutManager(getActivity()));
        listBook.setAdapter(bookAdapter);

        // 버튼 설정
        btnSearch = view.findViewById(R.id.btnSearch);
        btnMore = view.findViewById(R.id.btnMore);

        // 검색 버튼 클릭 리스너
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrayBook.clear(); // 배열을 null로 초기화하고 size를 0으로 설정
                // 검색어를 입력한 query와 1로 초기화한 page를 통해 다시 스레드를 실행하기 이전에 배열을 clear해줌
                query = edtSearch.getText().toString();
                page = 1;
                new BookFragment.KakaoThread().execute();
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
                    new BookFragment.KakaoThread().execute();
                }
            }
        });

        // 스레드 실행
        new KakaoThread().execute();

        return view;
    }

    // 카카오 스레드
    class KakaoThread extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {
            String s = Kakao.connect(url + "&query=" + query + "&page=" + page);
            return s;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            bookParsing(s);
            bookAdapter.notifyDataSetChanged();;
            txtTotal.setText("검색 수 : " + total + " / 마지막 페이지 : " + page);
            // System.out.println("데이터 개수 : " + arrayBook.size());
        }
    }

    // 파싱
    public void bookParsing(String s){ // 파싱할 데이터 s
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
                map.put("contents", obj.getString("contents"));
                map.put("price", obj.getString("price"));
                map.put("publisher", obj.getString("publisher"));
                map.put("thumbnail", obj.getString("thumbnail"));
                arrayBook.add(map); // 데이터가 더해진 map을 이번엔 arrayBook에 더해줌
            }
        }catch (Exception e) {}
    }

    // 도서 어댑터
    class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder>{

        @NonNull
        @Override
        public BookAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.item_book, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final BookAdapter.ViewHolder holder, int position) {
            final HashMap<String, String> map = arrayBook.get(position);
            holder.title.setText(map.get("title"));
            holder.publisher.setText(map.get("publisher"));
            holder.price.setText(map.get("price"));

            // 책 표지 가져오기
            String thumbnail = map.get("thumbnail");
            // 만약 가져온 thumbnail 값이 공백이 아니라면 (표지가 없는 책이 아니라면) 정상적으로 책 표지를 출력
            // 표지가 없는 책들은 조건문에 맞지 않으므로 기본 이미지를 그대로 출력
            if(!thumbnail.equals("")){ // 만약 가져온 thumbnail의 값이 공백이 아니라면
                Picasso.with(getActivity()).load(thumbnail).into(holder.thumbnail); // 현재 액티비티에 String형 thumbnail을 holder.thumbnail로 넣음
            }

            // 책 표지 클릭 리스너
            final String contents = map.get("contents");
            holder.thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LinearLayout layout = (LinearLayout)getLayoutInflater().inflate(R.layout.item_content, null);
                    // item_content.xml을 LinearLayout형으로 layout에 가져옴
                    TextView contents = layout.findViewById(R.id.contents); // item.contents.xml의 contents 아이디를 가져옴
                    contents.setText(map.get("contents")); // 검색한 책의 내용을 contents의 텍스트로 set
                    ImageView thumbnail = layout.findViewById(R.id.thumbnail); // 썸네일 아이디 가져오기
                    Picasso.with(getActivity()).load(map.get("thumbnail")).into(thumbnail); // Picassp를 이용하여 검색한 책의 썸네일 가져옴

                    // 알림창 설정
                    AlertDialog.Builder box = new AlertDialog.Builder(getContext());
                    box.setTitle("책 내용");
                    box.setPositiveButton("닫기", null);
                    box.setView(layout); // layout으로 box의 View를 set (알림창에 미리 만들어둔 item_book.xml 레이아웃이 나오도록 설정)
                    box.show(); // 알림창 box를 show
                }
            });
        }

        @Override
        public int getItemCount() {
            return arrayBook.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView title, publisher, price;
            ImageView thumbnail;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.title);
                publisher = itemView.findViewById(R.id.publisher);
                price = itemView.findViewById(R.id.price);
                thumbnail = itemView.findViewById(R.id.thumbnail);
            }
        }
    }
}