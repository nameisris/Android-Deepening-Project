package com.example.memo;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReadActivity extends AppCompatActivity {
    Memo memo = new Memo();
    EditText edtContent;
    String strKey, strContent;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add); // activity_add.xml을 그대로 사용

        // 액션바
        getSupportActionBar().setTitle("메모 읽기");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 백버튼

        // intent로 받아온 값을 메모 수정 창의 텍스트 창에 그대로 출력
        Intent intent = getIntent();
        strKey = intent.getStringExtra("key");
        strContent = intent.getStringExtra("content");
        memo.setKey(strKey);
        memo.setContent(strContent);
        memo.setCreateDate(intent.getStringExtra("createDate")); // 날짜 받아옴

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("memos/" + user.getUid() + "/" + strKey); // intent로 받아온 strKey값에 대해 memos 밑의 users 아이디 밑의 strKey에 위치한 값을 수정

        edtContent = findViewById(R.id.edtContent);
        edtContent.setText(memo.getContent()); // 수정할 내용이 EditText형 edtContent 안에 들어감

        // btnSave 클릭 리스너
        FloatingActionButton btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder box = new AlertDialog.Builder(ReadActivity.this);
                box.setTitle("질의");
                box.setMessage("저장하시겠습니까?");
                box.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        strContent = edtContent.getText().toString();
                        memo.setContent(strContent);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        memo.setCreateDate(sdf.format(new Date()));
                        myRef.setValue(memo); // 레퍼런스에 있는 값을 새로 초기화된 memo로 set 해줌
                        setResult(RESULT_OK);
                        finish();
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
            case R.id.itemDelete:
                AlertDialog.Builder box = new AlertDialog.Builder(this);
                box.setTitle("질의");
                box.setMessage("삭제하시겠습니까?");
                box.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        myRef.removeValue(); // myRef에 들어있는 Value 값을 remove
                        finish();
                    }
                });
                box.setNegativeButton("아니오", null);
                box.show();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_read, menu);
        return super.onCreateOptionsMenu(menu);
    }
}