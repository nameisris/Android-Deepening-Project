package com.example.user1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.user1.RemoteService.BASE_URL;


public class MainActivity extends AppCompatActivity {
    Retrofit retrofit; // 네트워크에 접속하기 위한
    RemoteService remoteService;
    List<UserVO> arrayUser = new ArrayList<>();
    UserAdapter userAdapter = new UserAdapter();
    ListView listUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 액션바
        getSupportActionBar().setTitle("사용자 관리");

        // 아이디 값
        listUser = findViewById(R.id.listUser);
        // 어댑터 set
        listUser.setAdapter(userAdapter);

        // Retrofit 빌더 생성
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        // API 인터페이스 생성
        remoteService = retrofit.create(RemoteService.class);

        // 플로팅 액션 버튼
        FloatingActionButton btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        // 인터페이스에 구현한 listUser() 메서드 실행
        Call<List<UserVO>> call = remoteService.listUser();
        call.enqueue(new Callback<List<UserVO>>() { // 앞서 만든 요청을 수행
            @Override
            public void onResponse(Call<List<UserVO>> call, Response<List<UserVO>> response) {
                arrayUser = response.body();
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<UserVO>> call, Throwable t) {

            }
        });
    }

    class UserAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return arrayUser.size(); // 데이터의 개수만큼 뷰를 만들기 위해
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
        public View getView(int i, View view, ViewGroup viewGroup) { // arrayUser의 size만큼 뷰를 만듬
            view = getLayoutInflater().inflate(R.layout.item_user, viewGroup, false);
            TextView txtId=view.findViewById(R.id.txtId);
            TextView txtName=view.findViewById(R.id.txtName);
            txtId.setText(arrayUser.get(i).getId());
            txtName.setText(arrayUser.get(i).getName());

            final String strId = arrayUser.get(i).getId();
            ImageView btnRead = view.findViewById(R.id.btnRead);
            btnRead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, ReadActivity.class);
                    intent.putExtra("id", strId);
                    startActivityForResult(intent, 2);
                }
            });
            return view;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 1 && resultCode == RESULT_OK){
            Toast.makeText(this, "저장 완료", Toast.LENGTH_SHORT).show();
        }
        else if(requestCode == 2 && resultCode == RESULT_OK){
            Toast.makeText(this, "수정 완료", Toast.LENGTH_SHORT).show();
        }
        else if(requestCode == 2 && resultCode == 3){
            Toast.makeText(this, "삭제 완료", Toast.LENGTH_SHORT).show();
        }
        // 변경된 데이터베이스의 테이블 값에 대해 어댑터에 알림
        Call<List<UserVO>> call = remoteService.listUser();
        call.enqueue(new Callback<List<UserVO>>() { // 앞서 만든 요청을 수행
            @Override
            public void onResponse(Call<List<UserVO>> call, Response<List<UserVO>> response) {
                arrayUser = response.body();
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<UserVO>> call, Throwable t) {

            }
        });
        super.onActivityResult(requestCode, resultCode, data);
    }
}