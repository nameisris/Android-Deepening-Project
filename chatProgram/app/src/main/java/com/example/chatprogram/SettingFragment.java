package com.example.chatprogram;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.chatprogram.RemoteService.BASE_URL;

public class SettingFragment extends Fragment {
    public SettingFragment() {}
    // mySQL
    Retrofit retrofit;
    RemoteService remoteService;
    String strFile;

    List<UserVO> arrayUser = new ArrayList<>(); // UserVO형 데이터를 저장할 arrayUser

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_setting, container, false);

        // Retrofit 정의
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        remoteService = retrofit.create(RemoteService.class);

        // Call
        Call<UserVO> call = remoteService.readUser(getActivity().getIntent().getStringExtra("email"));
        call.enqueue(new Callback<UserVO>() {
            @Override
            public void onResponse(Call<UserVO> call, Response<UserVO> response) {
                UserVO vo = response.body();
                TextView txtName = view.findViewById(R.id.txtName);
                txtName.setText(vo.getName());
                TextView txtEmail = view.findViewById(R.id.txtEmail);
                txtEmail.setText(vo.getEmail());
                TextView txtNumber = view.findViewById(R.id.txtNumber);
                txtNumber.setText(vo.getNumber());
                CircularImageView profileImage = view.findViewById(R.id.profileImage);
                Picasso.with(getActivity()).load(BASE_URL + "image/" + vo.getImage()).into(profileImage);
            }

            @Override
            public void onFailure(Call<UserVO> call, Throwable t) {

            }
        });

        // 사진 변경 리스너
        Button btnChangeImage = view.findViewById(R.id.btnChangeImage);
        btnChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // 로그아웃 버튼
        Button btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        return view;
    }

}