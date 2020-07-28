package com.example.ex01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class AddActivity extends AppCompatActivity {
    int mYear, mMonth, mDay;
    TextView wdate; // 현재 날짜
    // 내용 저장을 위한 데이터베이스 오픈
    Database db;
    SQLiteDatabase sql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        db = new Database(this);
        sql = db.getWritableDatabase(); // 데이터베이스의 값인 db를 sql로 읽어들여옴

        getSupportActionBar().setTitle("일기쓰기");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 백버튼 가져오기

        // 달력
        GregorianCalendar cal = new GregorianCalendar();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH); // 달의 날

        wdate = findViewById(R.id.wdate);
        wdate.setText(String.format("%04d/%02d/%02d", mYear, mMonth + 1, mDay));
        // %04d는 4자리로 채우겠다는 것 (4자리가 안되면 0으로 채운다)
        // %02d는 2자리로 채우겠다는 것 (2자리가 안되면 0으로 채운다, ex: 07월)
    }

    // OptionItem의 Select된 항목에 대한 설정 (클릭된 항목)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) // OptionItem의 id가 home이라면 (백버튼이라면)
            finish();
        return super.onOptionsItemSelected(item);
    }

    // mClick 메소드
    public void mClick(View v){
        switch (v.getId()){
            case R.id.calendar: // 달력
                new DatePickerDialog(this, setDate, mYear, mMonth, mDay).show(); // 어디에,
                break;
            case R.id.btn1: // 저장 버튼
                TextView wdate = findViewById(R.id.wdate);
                EditText subject = findViewById(R.id.subject);
                EditText content = findViewById(R.id.content);

                String strWdate = wdate.getText().toString();
                String strSubject = subject.getText().toString();
                String strContent = content.getText().toString();

                if(strSubject.equals("") || strContent.equals("")){ // (||는 OR를 의미)
                    Toast.makeText(this, "제목이나 내용을 입력하세요!", Toast.LENGTH_SHORT).show();
                }
                else{
                    String str = "insert into diary(wdate, subject, content) values(";
                    str += "'" + strWdate + "', ";
                    str += "'" + strSubject + "', ";
                    str += "'" + strContent + "')";
                    sql.execSQL(str); // 데이터베이스에 새로 입력한 내용에 대한 insert 작업 실행
                    setResult(RESULT_OK); // MainActivity로 resultCode가 RESULR_OK로 감
                    finish();
                }
                break;
            case R.id.btn2: // 취소 버튼
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