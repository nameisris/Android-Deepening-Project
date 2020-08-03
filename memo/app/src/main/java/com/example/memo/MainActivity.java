package com.example.memo;

import androidx.annotation.NonNull;
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

public class MainActivity extends AppCompatActivity {
    EditText edtEmail, edtPassword;
    Button btnRegister, btnLogin, btnCancel;
    FirebaseAuth mAuth; // 파이어베이스 사용을 위해 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance(); // 파이어베이스 인증에 대한

        // 액션바
        getSupportActionBar().setTitle("로그인");

        // 아이디 읽어오기
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnRegister =findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        btnCancel = findViewById(R.id.btnCancel);

        // btnRegister 클릭 리스너
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strEmail = edtEmail.getText().toString(); // @ 포함되어야 함
                String strPassword = edtPassword.getText().toString(); // 패스워드는 8자리 이상
                if(strEmail.indexOf('@') < 0) { // 이메일에 @가 index 중에 몇 번째에 있는지
                    Toast.makeText(MainActivity.this,"이메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
                }
                else if(strPassword.length() < 8){ // 패스워드가 8자리보자 작다면
                    Toast.makeText(MainActivity.this,"비밀번호를 8자리 이상 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    registerUser(strEmail, strPassword);
                }
            }
        });

        // btnLogin 클릭 리스너
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strEmail = edtEmail.getText().toString(); // @ 포함되어야 함
                String strPassword = edtPassword.getText().toString(); // 패스워드는 8자리 이상
                if(strEmail.indexOf('@') < 0) { // 이메일에 @가 index 중에 몇 번째에 있는지 (0번째보다 작으면 = @가 없다면)
                    Toast.makeText(MainActivity.this,"이메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
                }
                else if(strPassword.length() < 8){ // 패스워드의 길이가 8자리보자 작다면
                    Toast.makeText(MainActivity.this,"비밀번호를 8자리 이상 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    loginUser(strEmail, strPassword);
                }
            }
        });
    }

    // 가입 메소드
    public void registerUser(String strEmail, String strPassword){ // strEmail과 strPassword를 사용하여 유저를 생성
        mAuth.createUserWithEmailAndPassword(strEmail, strPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    Toast.makeText(MainActivity.this,"등록 성공", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MainActivity.this,"등록 실패", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 로그인 메소드
    public void loginUser(final String strEmail, String strPassword){ // strEmail과 strPassword를 사용하여 유저 로그인
        mAuth.signInWithEmailAndPassword(strEmail, strPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this,"로그인 성공", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    Intent intent = new Intent(MainActivity.this, MemoActivity.class);
                    intent.putExtra("email", strEmail);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(MainActivity.this,"로그인 실패", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}