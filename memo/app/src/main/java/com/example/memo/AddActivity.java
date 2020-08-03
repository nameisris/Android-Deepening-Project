package com.example.memo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;

    EditText edtContent;
    String strID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // intent 값 가져오기
        Intent intent = getIntent();
        String strEmail = intent.getStringExtra("email");

        // 액션바
        getSupportActionBar().setTitle("메모쓰기 : " + strEmail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 백버튼

        // 아이디 읽어오기
        edtContent = findViewById(R.id.edtContent);
        // 현재 로그인한 유저의 아이디 값 가져오기
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        strID = user.getUid();

        // 플로팅 액션 버튼
        FloatingActionButton btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strContent = edtContent.getText().toString();
                if(strContent.equals("")){
                    Toast.makeText(AddActivity.this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else{
                    Memo memo = new Memo(); // 미리 만들어둔 Memo 클래스
                    memo.setContent(strContent);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    memo.setCreateDate(sdf.format(new Date())); // 현재 날짜의 시간을 받아옴
                    database = FirebaseDatabase.getInstance();
                    myRef = database.getReference("memos").child(strID).push();
                    myRef.setValue(memo);

                    // MemoActivity의 onActivityResult로 돌아가므로
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}