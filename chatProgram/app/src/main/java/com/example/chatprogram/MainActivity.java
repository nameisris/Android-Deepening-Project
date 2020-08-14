package com.example.chatprogram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    // FirebaseDatabase mDatabase;
    // FirebaseStorage mStorage;
    EditText edtEmail, edtPassword;
    Button btnRegister, btnLogin, btnCancel;
    String strEmail, strPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 액션바
        getSupportActionBar().setTitle("채팅 프로그램");

        // 인스턴스 가져오기
        mAuth =  FirebaseAuth.getInstance();

        // 아이디 가져오기
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        btnCancel = findViewById(R.id.btnCancel);

        // btnRegister 클릭 리스너
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);

                /*
                strEmail = edtEmail.getText().toString();
                strPassword = edtPassword.getText().toString();
                if (strEmail.indexOf('@') < 0) { // 이메일에 @가 index 중에 몇 번째에 있는지
                    Toast.makeText(MainActivity.this,"이메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
                }
                else if (strPassword.length() < 8){ // 패스워드가 8자리보자 작다면
                    Toast.makeText(MainActivity.this,"비밀번호를 8자리 이상 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    registerUser(strEmail, strPassword);
                }
                */
            }
        });

        // btnLogin 클릭 리스너
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strEmail = edtEmail.getText().toString();
                strPassword = edtPassword.getText().toString();
                if (strEmail.indexOf('@') < 0) { // 이메일에 @가 index 중에 몇 번째에 있는지
                    Toast.makeText(MainActivity.this,"이메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
                }
                else if (strPassword.length() < 8) { // 패스워드가 8자리보자 작다면
                    Toast.makeText(MainActivity.this,"비밀번호를 8자리 이상 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    loginUser(strEmail, strPassword);
                }
            }
        });

        // btnCancel 클릭 리스너
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "취소되었습니다.", Toast.LENGTH_SHORT).show();
                // edtEmail, edtPassword 텍스트 공백으로 설정
                edtEmail.setText("");
                edtPassword.setText("");
            }
        });
    }

    // 유저 로그인 메소드
    public void loginUser(String strEmail, String strPassword) {
        mAuth.signInWithEmailAndPassword(strEmail, strPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            // strEmail과 strPassword를 이용해 유저 생성
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                    intent.putExtra("email", edtEmail.getText().toString());
                    startActivity(intent);
                }
                else {
                    Toast.makeText(MainActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}