package com.example.ex06;

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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class LocalFragment extends Fragment {
    EditText edtSearch;
    String url = "https://dapi.kakao.com/v2/local/search/keyword.json";
    String query = "천안";
    int page = 1;
    ArrayList<HashMap<String, String>> array;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local, container, false);

        edtSearch = view.findViewById(R.id.edtSearch);
        edtSearch.setHint("지역 검색어");

        // 스레드 실행
        new KakaoThread().execute();

        // 더보기 버튼
        ImageView btnMore = view.findViewById(R.id.btnMore);
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page = page + 1;
                new LocalFragment.KakaoThread().execute();
            }
        });

        // 검색 버튼
        ImageView btnSearch = view.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                query = edtSearch.getText().toString();
                page = 1;
                new LocalFragment.KakaoThread().execute();
            }
        });

        return view;
    }

    // 카카오 스레드
    class KakaoThread extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {
            String s = Kakao.connect(url + "?query=" + query + "&page=" + page);
            return s;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            localParser(s);

            LocalAdapter ad = new LocalAdapter();
            RecyclerView list = getActivity().findViewById(R.id.list_local);
            list.setAdapter(ad);
            list.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }

    // 데이터 파싱
    public void localParser(String s){
        array = new ArrayList<>();

        try {
            JSONArray jArray = new JSONObject(s).getJSONArray("documents"); // s에 들은 documents의 값을 오브젝트로 jArray에 넣어줌

            for(int i = 0;i < jArray.length();i++){
                JSONObject obj = jArray.getJSONObject(i); // jArray의 i번째 오브젝트 값을 obj에 가져옴

                HashMap<String, String> map = new HashMap<>(); // obj에서 값을 가져와 HashMap형의 값을 갖는 array에 넣어주기 위한 바구니
                map.put("place_name", obj.getString("place_name")); // obj의 "place_name"이 갖는 String 값을 map 안에 "place_name"이라는 이름으로 넣어줌
                map.put("address_name", obj.getString("address_name"));
                map.put("phone", obj.getString("phone"));
                map.put("x", obj.getString("x"));
                map.put("y", obj.getString("y"));
                array.add(map); // 값을 받은 바구니인 map을 array에 넣어줌
            }
        }catch (Exception e) {}
    }

    // 지역 어댑터
    class LocalAdapter extends RecyclerView.Adapter<LocalAdapter.ViewHolder>{

        @NonNull
        @Override
        public LocalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_local, parent, false); // item_local을 inflate
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LocalAdapter.ViewHolder holder, int position) {
            HashMap<String, String> map = array.get(position);

            holder.place_name.setText(map.get("place_name"));
            holder.address_name.setText(map.get("address_name"));
            holder.phone.setText(map.get("phone"));
        }

        @Override
        public int getItemCount() {
            return array.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView place_name, address_name, phone;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                place_name = itemView.findViewById(R.id.place_name);
                address_name = itemView.findViewById(R.id.address_name);
                phone = itemView.findViewById(R.id.phone);
            }
        }
    }
}