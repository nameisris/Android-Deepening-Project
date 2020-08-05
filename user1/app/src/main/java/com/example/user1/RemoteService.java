package com.example.user1;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RemoteService {
    public static final String BASE_URL="http://192.168.0.4:8088/user/";
    @GET("list.jsp")
    Call<List<UserVO>> listUser();
    @GET("read.jsp")
    Call<UserVO> readUser(@Query("id") String id);
    @POST("insert.jsp")
    Call<Void> insertUser(@Query("id") String id,
                          @Query("name") String name, @Query("password") String password);
    @POST("delete.jsp")
    Call<Void> deleteUser(@Query("id") String id);
    @POST("update.jsp")
    Call<Void> updateUser(@Query("id") String id,
                          @Query("name") String name, @Query("password") String password);
}