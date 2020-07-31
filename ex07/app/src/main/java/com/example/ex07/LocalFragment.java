package com.example.ex07;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class LocalFragment extends Fragment {
    String url = "https://dapi.kakao.com/v2/local/search/keyword.json";
    String query = "선문대학교";
    int page = 1;
    int total = 0;
    boolean is_end;

    EditText edtSearch;
    TextView txtTotal;
    ImageView btnSearch;
    FloatingActionButton btnMore;
    RecyclerView listLocal;

    ArrayList<HashMap<String, String>> arrayLocal = new ArrayList<>();
    LocalAdapter localAdapter = new LocalAdapter(); // 지역 어댑터 생성

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local, container, false);

        edtSearch = view.findViewById(R.id.edtSearch);
        txtTotal = view.findViewById(R.id.txtTotal);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnMore = view.findViewById(R.id.btnMore);
        listLocal = view.findViewById(R.id.listLocal);
        // listLocal에 레이아웃매니저 set해주고 로컬 어댑터 set해줌
        listLocal.setLayoutManager(new LinearLayoutManager(getActivity()));
        listLocal.setAdapter(localAdapter);

        // 버튼 설정
        btnSearch = view.findViewById(R.id.btnSearch);
        btnMore = view.findViewById(R.id.btnMore);

        // 검색 버튼 클릭 리스너
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrayLocal.clear(); // 배열을 null로 초기화하고 size를 0으로 설정
                // 검색어를 입력한 query와 1로 초기화한 page를 통해 다시 스레드를 실행하기 이전에 배열을 clear해줌
                query = edtSearch.getText().toString();
                page = 1;
                new LocalFragment.KakaoThread().execute();
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
                    new LocalFragment.KakaoThread().execute();
                }
            }
        });

        // 스레드 실행
        new KakaoThread().execute();

        return view;
    }

    // 카카오 스레드
    class KakaoThread extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            String s = Kakao.connect(url + "?query=" + query + "&page=" + page);
            return s;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            localParsing(s);
            localAdapter.notifyDataSetChanged();
            // txtTotal.setText("검색 수 : " + total + " / 마지막 페이지 : " + page);
        }
    }

    // 파싱
    public void localParsing(String s){
        try {
            // s의 "meta"로부터 데이터를 jObject에 가져옴
            JSONObject jObject = new JSONObject(s).getJSONObject("meta"); // s를 가지고 파싱
            total = jObject.getInt("total_count");
            is_end = jObject.getBoolean("is_end");

            // 데이터 가져오기
            JSONArray jArray = new JSONObject(s).getJSONArray("documents");
            for(int i = 0;i < jArray.length();i++){
                JSONObject obj =jArray.getJSONObject(i);

                HashMap<String, String> map = new HashMap<>();
                map.put("place_name", obj.getString("place_name"));
                map.put("address_name", obj.getString("address_name"));
                map.put("phone", obj.getString("phone"));
                map.put("x", obj.getString("x"));
                map.put("y", obj.getString("y"));
                // 검색 데이터 결과가 들어간 map을 arrayLocal에 더해줌
                arrayLocal.add(map);
            }

        } catch (Exception e) {}
    }

    class LocalAdapter extends RecyclerView.Adapter<LocalAdapter.ViewHolder>{

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.item_local, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            HashMap<String, String> map = arrayLocal.get(position);
            holder.name.setText(map.get("place_name"));
            holder.address.setText(map.get("address_name"));
            holder.tel.setText(map.get("phone"));
        }

        @Override
        public int getItemCount() {
            return arrayLocal.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView name, address, tel;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.name);
                address = itemView.findViewById(R.id.address);
                tel = itemView.findViewById(R.id.tel);
            }
        }
    }
}