package com.example.user1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.opengl.ETC1;
import android.os.Bundle;
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

public class AddActivity extends AppCompatActivity {
    Retrofit retrofit;
    RemoteService remoteService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // 액션바
        getSupportActionBar().setTitle("사용자 등록");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 백버튼

        // Retrofit 빌더 생성
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        // API 인터페이스 생성
        remoteService = retrofit.create(RemoteService.class);

        // 플로팅 버튼
        FloatingActionButton btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder box = new AlertDialog.Builder(AddActivity.this);
                box.setTitle("질의");
                box.setMessage("저장하시겠습니까?");
                box.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        UserVO userVO = new UserVO();
                        EditText edtId = findViewById(R.id.edtId);
                        userVO.setId(edtId.getText().toString());
                        EditText edtName = findViewById(R.id.edtName);
                        userVO.setName(edtName.getText().toString());
                        EditText edtPassword = findViewById(R.id.edtPassword);
                        userVO.setPassword(edtPassword.getText().toString());

                        Call<Void> call = remoteService.insertUser(userVO.getId(), userVO.getName(), userVO.getPassword());
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
}