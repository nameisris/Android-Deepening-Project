package com.example.chatprogram;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

public class PersonFragment extends Fragment {
    public PersonFragment() {}

    Retrofit retrofit;
    RemoteService remoteService;
    List<UserVO> arrayUser = new ArrayList<>(); // UserVO형 데이터를 저장할 arrayUser
    UserAdapter userAdapter = new UserAdapter();
    String strQuery = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_person, container, false);

        // 리스트뷰, 어댑터
        ListView list = view.findViewById(R.id.listUser);
        list.setAdapter(userAdapter);

        // Retrofit 정의
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        remoteService = retrofit.create(RemoteService.class);
        // list
        callData(strQuery);

        // read
        Call<UserVO> call = remoteService.readUser(getActivity().getIntent().getStringExtra("email"));
        call.enqueue(new Callback<UserVO>() {
            @Override
            public void onResponse(Call<UserVO> call, Response<UserVO> response) {
                UserVO vo = response.body();
                TextView txtName = view.findViewById(R.id.txtCurrentUserName);
                txtName.setText(vo.getName());
                CircularImageView image = view.findViewById(R.id.currentUserImage);
                Picasso.with(getActivity()).load(BASE_URL + "image/" + vo.getImage()).into(image);
            }

            @Override
            public void onFailure(Call<UserVO> call, Throwable t) {

            }
        });

        return view;
    }

    // 매번 콜하지 않도록 메소드를 만듦
    public void callData(String query) {
        Call<List<UserVO>> call = remoteService.listUser(query); // remoteService의 listProduct() 메소드를 콜
        call.enqueue(new Callback<List<UserVO>>() {
            @Override
            public void onResponse(Call<List<UserVO>> call, Response<List<UserVO>> response) {
                arrayUser =  response.body(); // 내용이 쏙 들어감
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<UserVO>> call, Throwable t) {

            }
        });
    }

    // 리스트뷰 어댑터
    class UserAdapter extends BaseAdapter{

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
        public View getView(final int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.item_user, viewGroup, false);

            // 아이디 가져오기
            CircularImageView image = view.findViewById(R.id.image);
            TextView txtName = view.findViewById(R.id.txtName);
            TextView txtNumber = view.findViewById(R.id.txtNumber);

            // 데이터 set
            txtName.setText(arrayUser.get(i).getName());
            Picasso.with(getActivity()).load(BASE_URL + "image/" + arrayUser.get(i).getImage()).into(image);
            txtNumber.setText(arrayUser.get(i).getNumber());

            ImageView btnCall = view.findViewById(R.id.btnCall);
            btnCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri uri = Uri.parse("tel:" + arrayUser.get(i).getNumber());
                    Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                    startActivity(intent);
                }
            });
            return view;
        }
    }
}