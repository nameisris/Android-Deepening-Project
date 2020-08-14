package com.example.chatprogram;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.chatprogram.RemoteService.BASE_URL;

public class MessageFragment extends Fragment {
    public MessageFragment() {}

    Retrofit retrofit;
    RemoteService remoteService;
    List<UserVO> arrayUser = new ArrayList<>(); // UserVO형 데이터를 저장할 arrayUser
    MessageAdapter messageAdapter = new MessageAdapter();
    String strQuery = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_message, container, false);

        /*
        // 리스트뷰, 어댑터
        ListView list = view.findViewById(R.id.listUser);
        list.setAdapter(messageAdapter);

        // Retrofit 정의
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        remoteService = retrofit.create(RemoteService.class);

        // read
        Call<UserVO> call = remoteService.readUser(getActivity().getIntent().getStringExtra("email"));
        call.enqueue(new Callback<UserVO>() {
            @Override
            public void onResponse(Call<UserVO> call, Response<UserVO> response) {
                UserVO vo = response.body();
                TextView txtName = view.findViewById(R.id.txtName);
                txtName.setText(vo.getName());
                CircularImageView image = view.findViewById(R.id.image);
                Picasso.with(getActivity()).load(BASE_URL + "image/" + vo.getImage()).into(image);
            }

            @Override
            public void onFailure(Call<UserVO> call, Throwable t) {

            }
        });
        */

        FloatingActionButton btnTemp = view.findViewById(R.id.btnTemp);
        btnTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("email", getActivity().getIntent().getStringExtra("email"));
                startActivity(intent);
            }
        });

        return view;
    }


    // 메세지 어댑터
    class MessageAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return arrayUser.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.item_message, viewGroup, false);

            return view;
        }
    }
}