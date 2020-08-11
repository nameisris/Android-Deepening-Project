package com.example.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.opengl.ETC1;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {
    ArrayList<Chat> arrayChat = new ArrayList<>(); // 데이터를 넣어줄 ArrayList
    EditText edtContent; // 채팅 내용
    ImageView btnSend; // 전송 버튼
    RecyclerView listChat; // 전송할 데이터를 출력할 리사이클러 뷰

    String strEmail; // 누가 보냈는지
    FirebaseDatabase database; // 데이터베이스
    DatabaseReference myRef; // 어디에 저장할지
    ChatAdapter chatAdapter; // 어댑터

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // 이메일 가져오기 (현재 로그인한 유저)
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        strEmail = user.getEmail();
        // 액션바
        getSupportActionBar().setTitle("채팅 : " + strEmail);

        // 아이디 읽어오기
        edtContent = findViewById(R.id.edtContent);
        listChat = findViewById(R.id.listChat);
        // 어댑터 생성
        chatAdapter = new ChatAdapter(this, arrayChat, strEmail); // 이메일 값 비교를 위해 현재 유저의 이메일인 strEmail을 넣어줌
        // 어댑터 연결
        listChat.setLayoutManager(new LinearLayoutManager(this));
        listChat.setAdapter(chatAdapter);



        // 데이터베이스 가져오기
        database = FirebaseDatabase.getInstance();

        myRef = database.getReference("chats");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { // child가 add될 때마다
                Chat chat = snapshot.getValue(Chat.class); // Chat.class와 똑같이 값을 가져옴
                arrayChat.add(chat); // 리사이클 뷰인 listChat과 adapt될 arrayChat에 chat의 값 add

                listChat.scrollToPosition(arrayChat.size()-1); // 리사이클러 뷰인 listChat에 대한 스크롤 위치 설정
                chatAdapter.notifyDataSetChanged(); // chatAdapter에 데이터가 바뀜을 알림 (바뀐 데이터로 다시 adapt)
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) { }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        // btnSend 클릭 리스너
        btnSend = findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strContent = edtContent.getText().toString();
                if (strContent.equals("")) { // 입력한 내용이 없다면
                    Toast.makeText(ChatActivity.this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Chat chat = new Chat(); // 미리 만들어둔 Chat 클래스를 이용해 chat 객체 생성
                    chat.setEmail(strEmail); // 이메일 set
                    chat.setContent(strContent); // 내용 set
                    // 현재 날짜 설정
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String strDate = sdf.format(new Date()); // 현재 날짜를 출력해주는 객체 new Date()를 통해 현재 날짜를 String형 strDate에 넣어줌
                    chat.setWdate(strDate); // 날짜 set

                    // insert 작업 (파이어베이스의 데이터베이스 안에 데이터 저장)
                    // meRef의 저장 경로 설정
                    myRef = database.getReference("chats").child(strDate); // "chats"밑에 strDate로 경로를 생성함과 동시에 저장 경로로 설정
                    // 저장 경로가 설정된 myRef를 이용해 데이터 저장
                    myRef.setValue(chat); // "chats" 밑의 날짜 밑에 chat을 넣어줌 (chat은 email, content, wdate가 set되어있음)
                    edtContent.setText(""); // 내용 입력창을 공백으로 set
                }
            }
        });
    }
}