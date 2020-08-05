package com.example.user1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.user1.RemoteService.BASE_URL;

public class ReadActivity extends AppCompatActivity {
    Retrofit retrofit;
    RemoteService remoteService;
    EditText edtId, edtName, edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // 액션바
        getSupportActionBar().setTitle("사용자 수정");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 아이디 읽어오기
        edtId = findViewById(R.id.edtId);
        edtName = findViewById(R.id.edtName);
        edtPassword = findViewById(R.id.edtPassword);

        // Retrofit 빌더 생성
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        // API 인터페이스 생성
        remoteService = retrofit.create(RemoteService.class);

        Intent intent = getIntent();
        Call<UserVO> call  = remoteService.readUser((intent.getStringExtra("id")));
        call.enqueue(new Callback<UserVO>() {
            @Override
            public void onResponse(Call<UserVO> call, Response<UserVO> response) {
                UserVO userVO = response.body();
                edtId.setText(userVO.getId());
                edtId.setEnabled(false); // 해당 EditText창의 텍스트를 건들지 못하게 함 (수정 불가능)
                edtName.setText(userVO.getName());
                edtPassword.setText(userVO.getPassword());

            }

            @Override
            public void onFailure(Call<UserVO> call, Throwable t) {

            }
        });

        // 플로팅 액션 버튼
        FloatingActionButton btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder box = new AlertDialog.Builder(ReadActivity.this);
                box.setTitle("질의");
                box.setMessage("수정하시겠습니까?");
                box.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        UserVO vo = new UserVO();
                        vo.setId(edtId.getText().toString());
                        vo.setName(edtName.getText().toString());
                        vo.setPassword(edtPassword.getText().toString());

                        Call<Void> call = remoteService.updateUser(vo.getId(), vo.getName(), vo.getPassword());
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                setResult(RESULT_OK);
                                finish();
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {

                            }
                        });
                    }
                });
                box.setNegativeButton("아니오", null);
                box.show();
            }
        });
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

    // menu_read.xml 메뉴 등록

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_read, menu);
        return super.onCreateOptionsMenu(menu);
    }
}