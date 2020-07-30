package com.example.ex06;

import android.app.AlertDialog;
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

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class BookFragment extends Fragment {
    EditText edtSearch;
    String url = "https://dapi.kakao.com/v3/search/book?target=title";
    String query = "안드로이드"; // 검색어
    int size = 10;
    ArrayList<HashMap<String, String>> array;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book, container, false);

        edtSearch = view.findViewById(R.id.edtSearch);
        edtSearch.setHint("도서 검색어");

        // 카카오 스레드 실행
        new KakaoThread().execute();

        // 더보기 버튼
        ImageView btnMore = view.findViewById(R.id.btnMore);
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                size = size + 10;
                new BookFragment.KakaoThread().execute();
            }
        });

        // 검색 버튼
        ImageView btnSearch = view.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                query = edtSearch.getText().toString();
                size = 10;
                new BookFragment.KakaoThread().execute();
            }
        });
        return view;
    }

    // 카카오 스레드
    class KakaoThread extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {
            String s = Kakao.connect(url + "&query=" + query + "&size=" + size); // Kakao.connet()에 url + 설정값을 줌
            return s;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            bookParser(s);

            // 파싱작업 후에 뷰 생성
            BookAdapter ad = new BookAdapter();
            RecyclerView list = getActivity().findViewById(R.id.list_book);
            list.setAdapter(ad);
            list.setLayoutManager(new LinearLayoutManager(getActivity()));
            // System.out.println("데이터 개수 : " + array.size());
        }
    }

    // 데이터 파싱
    public void bookParser(String s){
        array = new ArrayList<>();
        try {
            JSONArray jArray = new JSONObject(s).getJSONArray("documents"); // 검색결과가 들은 s에서 데이터를 가져와 jArray에 넣어줌

            for(int i = 0;i < jArray.length();i++){
                JSONObject obj = jArray.getJSONObject(i); // jArray의 i번째 오브젝트의 값을 obj로 받아옴

                HashMap<String, String> map = new HashMap<>();
                map.put("title", obj.getString("title")); // obj에서 가져온 "title"이라는 데이터를 "title"이라는 이름으로 map에 put
                map.put("thumbnail", obj.getString("thumbnail"));
                map.put("publisher", obj.getString("publisher"));
                map.put("price", obj.getString("price"));
                map.put("contents", obj.getString("contents"));
                array.add(map); // ArrayList인 array에 map을 add
            }

        }catch (Exception e) {}
    }

    // 책 어댑터
    class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder>{

        @NonNull
        @Override
        public BookAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_book, parent, false); // 디자인한 item_book.xml을 펼침
            return new ViewHolder(view); // 화면 상에 출력
        }

        @Override
        public void onBindViewHolder(@NonNull final BookAdapter.ViewHolder holder, int position) {
            HashMap<String, String> map = array.get(position); // map에 array의 값을 줌
            holder.title.setText(Html.fromHtml(map.get("title"))); // array의 값을 받은 map에서의 값을 텍스트로 set
            holder.publisher.setText(Html.fromHtml(map.get("publisher"))); // Html.fromHtml() 이용하면 출력되는 텍스트의 태그들을 제거 가능
            holder.price.setText(Html.fromHtml(map.get("price")));
            Picasso.with(getContext()).load(map.get("thumbnail")).into(holder.thumbnail);

            final String contents = map.get("contents");
            holder.thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder box = new AlertDialog.Builder(getContext());
                    box.setTitle(holder.title.getText().toString());
                    box.setMessage(contents);
                    box.setPositiveButton("확인", null);
                    box.show();
                }
            });
    }

        @Override
        public int getItemCount() {
            return array.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView title, publisher, price;
            ImageView thumbnail;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                title = itemView.findViewById(R.id.title);
                thumbnail = itemView.findViewById(R.id.thumbnail);
                publisher = itemView.findViewById(R.id.publisher);
                price = itemView.findViewById(R.id.price);
            }
        }
    }
}