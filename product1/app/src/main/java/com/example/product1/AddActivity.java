package com.example.product1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.product1.RemoteService.BASE_URL;

public class AddActivity extends AppCompatActivity {
    String strFile;
    ImageView image;
    Retrofit retrofit;
    RemoteService remoteService;
    EditText code, pname, price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        permissionCheck();
        // 아이디 읽어오기
        code = findViewById(R.id.edtCode);
        pname = findViewById(R.id.edtPname);
        price = findViewById(R.id.edtPrice);
        image = findViewById(R.id.image);

        // 액션바
        getSupportActionBar().setTitle("상품등록");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 사진 버튼
        ImageView btnImage = findViewById(R.id.btnImage);
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });

        // Retrofit 정의
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        remoteService = retrofit.create(RemoteService.class);

        // 저장 버튼
        FloatingActionButton btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder box = new AlertDialog.Builder(AddActivity.this);
                box.setTitle("질의");
                box.setMessage("저장하시겠습니까?");
                box.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        File file=new File(strFile);
                        RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
                        RequestBody strCode=RequestBody.create(MediaType.parse("multipart/form-data"), code.getText().toString());
                        RequestBody strPname= RequestBody.create(MediaType.parse("multipart/form-data"),pname.getText().toString());
                        RequestBody strPrice=RequestBody.create(MediaType.parse("multipart/form-data"), price.getText().toString());
                        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("image", file.getName(), mFile);

                        Call<ResponseBody> call = remoteService.uploadProduct(strCode, strPname, strPrice, fileToUpload);
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                setResult(RESULT_OK);
                                finish();
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });
                    }
                });
                box.setNegativeButton("아니오", null);
                box.show();
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
        if(requestCode == 1){
            try{
                String[] projection = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(data.getData(), projection, null, null, null);
                cursor.moveToFirst();
                strFile = cursor.getString(cursor.getColumnIndex(projection[0]));
                cursor.close();
                image.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData()));
            }catch (Exception e){}
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