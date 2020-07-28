package com.example.ex01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateActivity extends AppCompatActivity {
    Database db;
    SQLiteDatabase sql;
    Cursor cur;
    TextView wdate;
    EditText subject, content;
    int mYear, mMonth, mDay;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add); // activity_add의 xml을 사용

        Intent intent = getIntent(); // 이동되어온 Intent 받아옴 (Intent에는 id값이 포함되어있음)
        id = intent.getIntExtra("id", 0); // id란 이름의 값의 int 데이터를 intent에서 가져옴
        // defaultValue : 값이 없을때 디폴트 값이 0

        getSupportActionBar().setTitle("일기수정" + id);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 백버튼

        // 데이터베이스로부터 데이터 select해옴
        db = new Database(this);
        sql = db.getWritableDatabase();
        cur = sql.rawQuery("select * from diary where _id=" + id, null);

        wdate = findViewById(R.id.wdate);
        subject = findViewById(R.id.subject);
        content = findViewById(R.id.content);
        if(cur.moveToNext()){
            // cur을 다음으로 움직여서 데이터를 가져와라 (조건 : 데이터가 있다면)
            // 이동된(변경된)? 값이 있다면? // 수정하고자 하는 값이 수정창에 들어옴
            wdate.setText(cur.getString(1));
            subject.setText(cur.getString(2));
            content.setText(cur.getString(3));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) // 백버튼을 선택했을 경우
            finish();
        return super.onOptionsItemSelected(item);
    }

    // mClick 메소드
    public void mClick(View v){
        switch(v.getId()){
            case R.id.calendar:
                String strWdate = wdate.getText().toString(); // 현재 날짜에 대해 String형으로 가져옴
                mYear = Integer.parseInt(strWdate.substring(0, 4)); // OOOO/OO/OO에서 0번째부터 4번째의 앞의 것까지 가져오라는 뜻
                mMonth = Integer.parseInt(strWdate.substring(5, 7)); // substring이란 string의 일부를 가져오겠다는 뜻
                mDay = Integer.parseInt(strWdate.substring(8, 10)); // Integer형으로 가져오기 위해 parseInt 사용
                new DatePickerDialog(this, setDate, mYear, mMonth-1, mDay).show(); // +1을 해주어 출력된 mMonth에 대해 다시 -1로 입력
                break;
            case R.id.btn1:
                strWdate = wdate.getText().toString();
                String strSubject = subject.getText().toString();
                String strContent = content.getText().toString();

                if(strSubject.equals("") || strContent.equals("")){ // (||는 OR를 의미)
                    Toast.makeText(this, "제목이나 내용을 입력하세요!", Toast.LENGTH_SHORT).show();
                }
                else{
                    String str = "update diary set ";
                    str += "wdate='" + strWdate + "', ";
                    str += "subject='" + strSubject + "', ";
                    str += "content='" + strContent + "' ";
                    str += " where _id=" + id;
                    sql.execSQL(str);
                    setResult(RESULT_OK);
                    finish();
                }
                break;
            case R.id.btn2:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
    }

    // 날짜 변경
    DatePickerDialog.OnDateSetListener setDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            mYear = i;
            mMonth = i1;
            mDay = i2;
            wdate.setText(String.format("%04d/%02d/%02d", mYear, mMonth + 1, mDay));
        }
    };
}