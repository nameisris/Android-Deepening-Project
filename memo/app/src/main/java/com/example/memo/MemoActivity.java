package com.example.memo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MemoActivity extends AppCompatActivity {
    FloatingActionButton btnAdd;
    String strEmail;

    FirebaseDatabase database;
    DatabaseReference myRef;
    RecyclerView listMemo;
    MemoAdapter memoAdapter;
    ArrayList<Memo> arrayMemo = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        // intent 값 가져오기
        Intent intent = getIntent();
        strEmail = intent.getStringExtra("email");

        // 액션바
        getSupportActionBar().setTitle("메모장 : " + strEmail);

        memoAdapter = new MemoAdapter(arrayMemo, this); // 어댑터 생성
        listMemo = findViewById(R.id.listMemo);
        listMemo.setLayoutManager(new LinearLayoutManager(this)); // 리사이클 뷰 사용을 하기 위해 필요함
        listMemo.setAdapter(memoAdapter); // 리사이클 뷰인 listMemo에 momoAdapter를 set

        // 데이터를 읽어들이는 작업
        // 먼저 유저를 읽어들이는 작업
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // 인증해서 현재 유저의 인스턴스 가져오기
        database = FirebaseDatabase.getInstance();
        // 어디에서 가져올지
        myRef = database.getReference("memos/" + user.getUid());  // memos 아래에 있는 유저들을 가져옴 (참조함)
        // 데이터를 읽어들이는 부분
        readData();

        // btnAdd 클릭 리스너
        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MemoActivity.this, AddActivity.class);
                intent.putExtra("email", strEmail);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { // AddActivity에서 finish()를 한 이후 현재 메소드로 돌아옴
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
            Toast.makeText(MemoActivity.this, "저장 완료", Toast.LENGTH_SHORT).show();
        }
        else if(requestCode == 2 && resultCode == RESULT_OK){
            Toast.makeText(MemoActivity.this, "수정 완료", Toast.LENGTH_SHORT).show();
        }
        readData();
    }

    public void readData(){
        arrayMemo.clear();
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Memo memo = (Memo)dataSnapshot.getValue(Memo.class); // Memo.class에 맞게 가져오겠다는 뜻
                memo.setKey(dataSnapshot.getKey()); // 키 값을 set 해줌
                arrayMemo.add(memo); // arraMemo에 memo를 add 해줌
                memoAdapter.notifyDataSetChanged(); // 바뀐 데이터에 대해 notify 해줌
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}