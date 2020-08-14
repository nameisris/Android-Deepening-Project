package com.example.chatprogram;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface RemoteService {
    public static final String BASE_URL="http://192.168.0.4:8088/userInformation/";
    @GET("list.jsp")
    Call<List<UserVO>> listUser(
            @Query("query") String query);

    @GET("read.jsp")
    Call<UserVO> readUser(@Query("email") String email);

    @Multipart
    @POST("insert.jsp")
    Call<ResponseBody> uploadUser(
            @Part("email") RequestBody strEmail,
            @Part("name") RequestBody strName,
            @Part("number") RequestBody strNumber,
            @Part MultipartBody.Part image);

    @POST("delete.jsp")
    Call<Void> deleteUser(
            @Query("email") String email);

    @POST("update.jsp")
    Call<Void> updateUser(
            @Query("email") String email,
            @Part MultipartBody.Part image);
}
