package com.example.product1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.product1.RemoteService.BASE_URL;

public class MainActivity extends AppCompatActivity {
    Retrofit retrofit;
    RemoteService remoteService;
    List<ProductVO> arrayProduct = new ArrayList<>(); // 데이터를 저장할 arrayProduct
    ProductAdapter productAdapter = new ProductAdapter();
    String strOrder = "code";
    String strQuery = "";
    final static int ADD_ACTIVITY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 액션바
        getSupportActionBar().setTitle("상품관리");

        // 리스트뷰, 어댑터
        ListView list = findViewById(R.id.listProduct);
        list.setAdapter(productAdapter);

        // Retrofit 정의
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        remoteService = retrofit.create(RemoteService.class);
        callData(strOrder, strQuery);

        // 플로팅 액션 버튼
        FloatingActionButton btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivityForResult(intent, ADD_ACTIVITY);
            }
        });

    }

    // 매번 콜하지 않도록 메소드를 만듦
    public void callData(String order, String query) {
        Call<List<ProductVO>> call = remoteService.listProduct(order, query); // remoteService의 listProduct() 메소드를 콜
        call.enqueue(new Callback<List<ProductVO>>() {
            @Override
            public void onResponse(Call<List<ProductVO>> call, Response<List<ProductVO>> response) {
                arrayProduct =  response.body(); // 내용이 쏙 들어감
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<ProductVO>> call, Throwable t) {

            }
        });
    }

    // 어댑터
    class ProductAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return arrayProduct.size();
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
        public View getView(final int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.item_product, viewGroup, false);

            // 아이디 가져오기
            TextView txtCode = view.findViewById(R.id.txtCode);
            TextView txtName = view.findViewById(R.id.txtName);
            TextView txtPrice = view.findViewById(R.id.txtPrice);

            // 데이터 set
            txtCode.setText(arrayProduct.get(i).getCode());
            txtName.setText(arrayProduct.get(i).getPname());
            txtPrice.setText(arrayProduct.get(i).getPrice() + "만원"); // price가 int형이므로 문자열을 붙여 Text에 드갈수 있게 함

            // 이미지 set
            ImageView image = view.findViewById(R.id.image);
            Picasso.with(MainActivity.this).load(BASE_URL + "image/" + arrayProduct.get(i).getImage()).into(image);

            // 아이템 항목 선택
            RelativeLayout item = view.findViewById(R.id.item);
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, ReadActivity.class);
                    intent.putExtra("code", arrayProduct.get(i).getCode());
                    startActivityForResult(intent, 1);
                }
            });

            // 아이템 항목 삭제
            ImageView imgDelete = view.findViewById(R.id.imgDelete);
            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            return view;
        }
    }

    // 상단바 우측 메뉴 생성
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_name, menu);

        // 검색
        MenuItem itemSearch = menu.findItem(R.id.itemSearch);
        SearchView searchView = (SearchView)itemSearch.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // 검색창의 검색어가 바뀔때마다
                // 검색창에 입력한 텍스트인 s가 매개변수로 들어감
                strQuery = s;
                callData(strOrder, strQuery);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    // 상단바 우측 메뉴 선택
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.itemCode:
                strOrder = "code";
                break;
            case R.id.itemPname:
                strOrder = "pname";
                break;
            case R.id.itemDesc:
                strOrder = "desc";
                break;
            case R.id.itemAsc:
                strOrder = "asc";
                break;
        }
        callData(strOrder, strQuery);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == ADD_ACTIVITY && resultCode == RESULT_OK){
            Toast.makeText(this, "상품등록완료", Toast.LENGTH_SHORT).show();
            callData("pcode", "");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}