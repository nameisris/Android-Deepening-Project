package com.example.chatprogram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static com.example.chatprogram.RemoteService.BASE_URL;

public class ChatActivity extends AppCompatActivity {
    ArrayList<Message> arrayMessage = new ArrayList<>(); // 데이터를 넣어줄 ArrayList
    ImageView btnSend; // 전송 버튼
    ImageView btnSearch; // 검색 버튼
    EditText edtContent; // 채팅 내용
    RecyclerView listChat; // 전송할 데이터를 출력할 리사이클러 뷰

    String strEmail; // 누가 보냈는지
    String query = ""; // 검색문?
    String strID; // 로그인한 유저 아이디

    // mySQL DB
    Retrofit retrofit;
    RemoteService remoteService;

    FirebaseDatabase database; // 데이터베이스
    FirebaseAuth mAuth; // 유저 인증을 위한

    DatabaseReference myRef; // 어디에 저장할지
    ChatAdapter chatAdapter; // 어댑터

    // crawling
    ArrayList<HashMap<String, String>> arrayDaum = new ArrayList<>();
    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Daum 스레드 생성
        new DaumThread().execute();

        // Intent로 로그인한 사용자의 String형 이메일 값 받아오기
        Intent intent = getIntent();
        strEmail = intent.getStringExtra("email");

        // 아이디 읽어오기
        edtContent = findViewById(R.id.edtContent);
        listChat = findViewById(R.id.listChat);

        // 현재 로그인한 유저의 아이디 값 가져오기
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        strID = user.getUid();

        // 어댑터 생성
        chatAdapter = new ChatAdapter(this, arrayMessage, strEmail); // 이메일 값 비교를 위해 현재 유저의 이메일인 strEmail을 넣어줌
        // 어댑터 연결
        listChat.setLayoutManager(new LinearLayoutManager(this));
        listChat.setAdapter(chatAdapter);

        // Retrofit 정의
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        remoteService = retrofit.create(RemoteService.class);

        // 데이터베이스 가져오기
        database = FirebaseDatabase.getInstance();

        myRef = database.getReference("Messages");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { // child가 add될 때마다
                Message message = snapshot.getValue(Message.class); // Message.class와 똑같이 값을 가져옴
                arrayMessage.add(message); // 리사이클 뷰인 listChat과 adapt될 arrayChat에 chat의 값 add

                listChat.scrollToPosition(arrayMessage.size()-1); // 리사이클러 뷰인 listChat에 대한 스크롤 위치 설정
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

        // btnSend 클릭 리스너 (메세지를 전송할 때마다의 수행 작업)
        btnSend = findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strContent = edtContent.getText().toString();
                final String strName;

                if (strContent.equals("")) { // 입력한 내용이 없다면
                    Toast.makeText(ChatActivity.this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else {

                    Message message = new Message();
                    message.setName(strEmail);
                    message.setContent(edtContent.getText().toString());
                    // 현재 날짜 설정
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String strDate = sdf.format(new Date()); // 현재 날짜를 출력해주는 객체 new Date()를 통해 현재 날짜를 String형 strDate에 넣어줌
                    message.setWdate(strDate); // 날짜 set

                    // insert 작업 (파이어베이스의 데이터베이스 안에 데이터 저장)
                    // meRef의 저장 경로 설정
                    myRef = database.getReference("Messages").child(strDate); // "chats"밑에 strDate로 경로를 생성함과 동시에 저장 경로로 설정
                    // 저장 경로가 설정된 myRef를 이용해 데이터 저장
                    myRef.setValue(message); // "chats" 밑의 날짜 밑에 chat을 넣어줌 (chat은 email, content, wdate가 set되어있음)
                    edtContent.setText(""); // 내용 입력창을 공백으로 set
                }
            }
        });

        // btnSearch 클릭 리스너
        btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, SearchActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 1 && resultCode == RESULT_OK) {

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // 다음 날씨 스레드
    class DaumThread extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                // 해당 링크의 페이지에 출력되는 값들을
                // HashMap<String, String>형인 map에 더해주는 작업을 한 뒤
                // ArrayList<HashMap<String, String>>형인 arrayDaum에 map을 더해줌
                HashMap<String, Object> object=new HashMap<String, Object>();
                Document doc=Jsoup.connect("http://www.daum.net").get();
                Elements elements=doc.select(".list_weather"); // list_weahter 안의
                for(Element e:elements.select("li")) { // li 안의 (li가 여러 개이므로 for문 사용)
                    HashMap<String, String> map=new HashMap<String, String>();
                    map.put("part", e.select(".txt_part").text()); // part라는 이름으로 txt_part 값을 select
                    map.put("temper", e.select(".txt_temper").text());
                    map.put("wa", e.select(".ir_wa").text());
                    map.put("ico", e.select(".ico_ws").text());
                    arrayDaum.add(map);
                }
            }catch(Exception e) {
                System.out.println(e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // System.out.println("날씨데이터갯수 : " + arrayDaum.size());
            getSupportActionBar().setTitle("다음날씨");

            BackThread backThread = new BackThread();
            backThread.setDaemon(true);
            backThread.start();
        }
    }

    // 백 스레드
    class BackThread extends Thread{
        @Override
        public void run() {
            super.run();
            index=0;
            while(true){
                handler.sendEmptyMessage(0);
                index++;
                if(index==arrayDaum.size()){ // 만약 arrayDaum의 사이즈와 같아진다면
                    index=0; // 인덱스를 0으로 초기화 (arrayDaum.size()번째 인덱스까지 반복하고 다시 0번 인덱스부터 반복 시작)
                }
                try{Thread.sleep(3000);}catch(Exception e){}
            }
        }
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull android.os.Message msg) {
            super.handleMessage(msg);
            String part=arrayDaum.get(index).get("part");
            String temper=arrayDaum.get(index).get("temper");
            String ico=arrayDaum.get(index).get("ico");
            getSupportActionBar().setTitle(part + temper + "도/ "+ ico);
        }
    };

}