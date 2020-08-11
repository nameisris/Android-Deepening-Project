package com.example.chatprogram;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RemoteService {
    public static final String BASE_URL="http://192.168.0.4:8088/userInformation/";
    @GET("list.jsp")
    Call<List<UserVO>> listUser();
    @GET("read.jsp")
    Call<UserVO> readUser(@Query("uid") String uid);
    @POST("insert.jsp")
    Call<Void> insertUser(@Query("uid") String uid,
                          @Query("uname") String uname,
                          @Query("uemail") String uemail,
                          @Query("unumber") String unumber,
                          @Query("uimage") String uimage);
    @POST("delete.jsp")
    Call<Void> deleteUser(@Query("uid") String uid);
    @POST("update.jsp")
    Call<Void> updateUser(@Query("id") String id,
                          @Query("name") String name, @Query("password") String password);
}
