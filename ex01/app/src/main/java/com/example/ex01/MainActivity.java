package com.example.ex01;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    Database db;
    SQLiteDatabase sql;
    MyAdapter ad;
    Cursor cur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 액션바 설정
        getSupportActionBar().setTitle("일기장");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 백버튼?
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_logo);

        db = new Database(this);
        sql = db.getReadableDatabase();
        cur = sql.rawQuery("select * from diary order by wdate desc", null); // 최근순으로 정렬 (desc)
        ad = new MyAdapter(this, cur);
        ListView list = findViewById(R.id.list);

        // 리스트 클릭 리스너
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // int id = cur.getInt(0); // 0번째 항목은 id
                Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
                intent.putExtra("id", cur.getInt(0)); // intent에 id라는 이름의 id가 포함됨
                startActivityForResult(intent, 2);
            }
        });
        list.setAdapter(ad); // cur의 데이터베이스의 데이터 갯수만큼 리스트 생성
        registerForContextMenu(list);

        // activity_main.xml의 플로팅액션 버튼에 대한 클릭리스너
        FloatingActionButton add = findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivityForResult(intent, 1); // 다시 돌아오기 위해 startActivity() 메소드가 아닌 startActivityForResult() 메소드 사용
            }
        });
    }


    // 어댑터 생성
    class MyAdapter extends CursorAdapter{

        public MyAdapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            View view = getLayoutInflater().inflate(R.layout.item, viewGroup, false); // newView에선 item 개수만큼 cursor를 만들어줌
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView wdate = view.findViewById(R.id.wdate);
            wdate.setText(cursor.getString(1));

            TextView subject = view.findViewById(R.id.subject);
            subject.setText(cursor.getString(2));

            // item.xml의 delete ImageView에 대한 클릭 리스너
            final int id = cursor.getInt(0); // 아이디값 받아오기
            ImageView delete = view.findViewById(R.id.delete);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder box = new AlertDialog.Builder(MainActivity.this); // 현재 액티비티인 MainActivity에 box 생성
                    box.setTitle("질의 : " + id);
                    box.setMessage("삭제하시겠습니까?");
                    box.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String str = "delete from diary where _id=" + id; // 삭제문
                            sql.execSQL(str); // 삭제문 str 실행 (삭제를 선택한 항목에 대해 데이터베이스에서 삭제
                            // 변경된 sql에 대한 어댑터 재설정? (데이터베이스에서 삭제한 항목에 대해 리스트뷰 재출력)
                            cur = sql.rawQuery("select * from diary order by wdate desc", null);
                            ad.changeCursor(cur);
                        }
                    });
                    box.setNegativeButton("아니오", null);
                    box.show();

                }
            });
        }
    }

    // 컨텍스트 메뉴 생성
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, 1, 0, "최근순 정렬"); // 인덱스 번호 1
        menu.add(0, 2, 0, "과거순 정렬");
        menu.add(0, 3, 0, "제목순 정렬");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    // 컨텍스트 메뉴 항목 선택 이벤트
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        String str = "";
        switch(item.getItemId()){
            case 1:
                str = "select * from diary order by wdate desc";
                break;
            case 2:
                str = "select * from diary order by wdate"; // asc
                break;
            case 3:
                str = "select * from diary order by subject";
                break;
        }
        cur = sql.rawQuery(str, null);
        ad.changeCursor(cur);
        return super.onContextItemSelected(item);
    }

    // 옵션 메뉴 생성
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu); // menu형태인 main.xml을 메뉴로 등록
        // SearchView에 이벤트 등록
        MenuItem search = menu.findItem(R.id.search); // search 아이디 읽어오기
        SearchView searchView = (SearchView)search.getActionView(); // 액션뷰 가져와서 넣어주기
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() { // 리스너 달아주기
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                String str = "select * from diary where subject like '%" + s + "'"; // 부분일치(like) 검색
                str += " order by wdate desc"; // 최신날짜순 정렬
                // 어댑터 새로 적용?
                cur = sql.rawQuery(str, null);
                ad.changeCursor(cur);
                return false; // onQueryTextChange가 끝나면 원래 상태로 돌아가기 위해? (검색할 때만 검색한 리스트가 보이도록 하게?)
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    // RESULT예 대한 결과??

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 1 && resultCode == RESULT_OK){ // AddActivity -> MainActivity이며 resultCode가 RESULT_OK일 경우
            // 어댑터 재설정 (메인 액티비티 상의 리스트뷰를 변경된 데이터베이스의 값으로 다시 출력해주기 위해)
            // 데이터베이스에 새로 추가된 값에 대하여 액티비티 상의 리스트뷰를 새로이 출력
            cur = sql.rawQuery("select * from diary order by wdate desc", null);
            ad.changeCursor(cur);
            Toast.makeText(this, "저장완료", Toast.LENGTH_SHORT).show();
        }
        else if(requestCode == 1 && resultCode == RESULT_CANCELED){ // AddActivity -> MainActivity이며 resultCode가 RESULT_CANCELED일 경우
            Toast.makeText(this, "저장취소", Toast.LENGTH_SHORT).show();
        }
        else if(requestCode == 2 && resultCode == RESULT_OK){ // UpdateActivity -> MainActivity이며 resultCode가 RESULT_OK일 경우
            // 어댑터 재설정 (메인 액티비티 상의 리스트뷰를 변경된 데이터베이스의 값으로 다시 출력해주기 위해)
            // 데이터베이스에서 수정된 값에 대하여 액티비티 상의 리스트뷰를 새로이 출력
            cur = sql.rawQuery("select * from diary order by wdate desc", null);
            ad.changeCursor(cur);
            Toast.makeText(this, "수정완료", Toast.LENGTH_SHORT).show();
        }
        else if(requestCode == 2 && resultCode == RESULT_CANCELED){ // UpdateActivity -> MainActivity이며 resultCode가 RESULT_CANCELED일 경우
            Toast.makeText(this, "수정취소", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}