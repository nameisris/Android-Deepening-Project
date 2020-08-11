package com.example.chatprogram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.loader.content.CursorLoader;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;

import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity {
    FirebaseAuth mAuth;

    Retrofit retrofit;
    RemoteService remoteService;

    EditText edtName, edtEmail, edtPassword, edtNumber;
    String strName, strEmail, strPassword, strNumber;
    ImageView profileImage;
    String strFile;
    Uri imageUri;

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

        // 프로필 이미지 설정
        // imgCamera 리스너
        ImageView imgCamera = findViewById(R.id.imgCamera);
        imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence info[] = new CharSequence[] { "카메라에서 가져오기", "앨범에서 사진 선택", "취소"};
                AlertDialog.Builder box = new AlertDialog.Builder(RegisterActivity.this);
                box.setTitle("질의");
                box.setItems(info, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which)
                        {
                            case 0:
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                strFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/attachimage.jpg";

                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // API 24 이상일 경우
                                    imageUri = FileProvider.getUriForFile(RegisterActivity.this, getApplicationContext().getPackageName() + ".provider", new File(strFile));
                                }
                                else { // API 24 미만일 경우
                                    imageUri = Uri.fromFile(new File(strFile));
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                    startActivityForResult(intent, 1);
                                }
                                startActivityForResult(intent, 1);
                                break;
                            case 1:
                                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent, 2);
                                break;
                            case 2:
                                break;
                        }
                        dialog.dismiss();
                    }
                });
                box.show();
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
                if (strName.equals("") || strEmail.equals("") || strPassword.equals("") || strNumber.equals("")) { // 한 항목이라도 비어있다면
                    Toast.makeText(RegisterActivity.this,"모든 내용을 기입해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if (strEmail.indexOf('@') < 0) { // 이메일에 @가 index 중에 몇 번째에 있는지
                    Toast.makeText(RegisterActivity.this,"이메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
                }
                else if (strPassword.length() < 8) { // 패스워드가 8자리보다 작다면
                    Toast.makeText(RegisterActivity.this,"비밀번호를 8자리 이상 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else if (strNumber.length() != 11) {
                    Toast.makeText(RegisterActivity.this,"올바른 휴대전화 번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    registerUser(strEmail, strPassword);
                    Toast.makeText(RegisterActivity.this, "계정 생성 완료", Toast.LENGTH_SHORT).show();
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

    // 유저 가입 메소드
    public void registerUser(String strEmail, String strPassword) {
        mAuth.createUserWithEmailAndPassword(strEmail, strPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            // strEmail과 strPassword를 이용해 유저 생성
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser(); // 인스턴스를 가져온 mAuth로부터 현재 유저를 가져옴
                    Toast.makeText(RegisterActivity.this, "사용자 등록 성공", Toast.LENGTH_SHORT).show();
                    }
                else {
                    Toast.makeText(RegisterActivity.this, "사용자 등록 실패", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    profileImage.setImageBitmap(BitmapFactory.decodeFile(strFile));
                    profileImage.setImageURI(imageUri); // 이미지 띄움
                    break;
                case 2:
                    try{
                        profileImage.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData()));
                    }catch (Exception e){}
                    break;
            }
            Toast.makeText(RegisterActivity.this, "사진 선택 완료", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(RegisterActivity.this, "취소되었습니다.", Toast.LENGTH_SHORT).show();
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