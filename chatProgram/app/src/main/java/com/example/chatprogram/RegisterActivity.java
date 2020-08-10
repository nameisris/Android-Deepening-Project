package com.example.chatprogram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.io.InputStream;

public class RegisterActivity extends AppCompatActivity {
    FirebaseAuth mAuth;

    EditText edtName, edtEmail, edtPassword, edtNumber;
    String strName, strEmail, strPassword, strNumber;
    ImageView profileImage;
    Intent intentPhoto;
    String strFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        permissionCheck();

        // 액션바
        getSupportActionBar().setTitle("계정 생성");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 백버튼

        // 인스턴스 가져오기
        mAuth =  FirebaseAuth.getInstance();

        // 아이디 가져오기
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtNumber = findViewById(R.id.edtNumber);
        profileImage = findViewById(R.id.profileImage);

        // Intent 받아오기
        final Intent intent = getIntent();

        // 프로필 이미지 설정
        // 카메라 열기 리스너
        ImageView imgCamera = findViewById(R.id.imgCamera);
        imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        // 갤러리 열기 리스너
        ImageView imgPhoto = findViewById(R.id.imgPhoto);
        imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentPhoto, 2);
            }
        });

        // btnJoin 클릭 리스너
        Button btnJoin = findViewById(R.id.btnJoin);
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strName = edtName.getText().toString();
                strEmail = edtEmail.getText().toString();
                strPassword = edtPassword.getText().toString();
                strNumber = edtNumber.getText().toString();
                if (strName.equals("")) {

                }
                else if (strEmail.indexOf('@') < 0) { // 이메일에 @가 index 중에 몇 번째에 있는지
                    Toast.makeText(RegisterActivity.this,"이메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
                }
                else if (strPassword.length() < 8) { // 패스워드가 8자리보다 작다면
                    Toast.makeText(RegisterActivity.this,"비밀번호를 8자리 이상 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else if (strNumber.length() != 11) {
                    Toast.makeText(RegisterActivity.this,"올바른 전화번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    intent.putExtra("name", strName);
                    intent.putExtra("email", strEmail);
                    intent.putExtra("password", strPassword);
                    intent.putExtra("number", strNumber);
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
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 2){
            try{
                String[] projection = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(data.getData(), projection, null, null, null);
                cursor.moveToFirst();
                strFile = cursor.getString(cursor.getColumnIndex(projection[0]));
                cursor.close();
                profileImage.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData()));

                Toast.makeText(RegisterActivity.this, "사진 선택 완료", Toast.LENGTH_SHORT).show();
            }catch (Exception e){
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void permissionCheck(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
    }
}