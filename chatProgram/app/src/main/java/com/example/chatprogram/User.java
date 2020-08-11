package com.example.chatprogram;

public class User {
    String uId;
    String name;
    String email;
    String password;
    String number;
    // String content; // 상태메세지
    String profileImageUrl; // 프로필사진

    // setter
    public void setuId(String uId) {
        this.uId = uId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNumber(String number) {
        this.number = number;
    }
    /*
    public void setContent(String content) {
        this.content = content;
    }
    */
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    // getter

    public String getuId() {
        return uId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getNumber() {
        return number;
    }
    /*
    public String getContent() {
        return content;
    }
    */
    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}
