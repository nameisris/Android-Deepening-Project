package com.example.user1;

public class UserVO {
    // 데이터베이스에 생성된 테이블의 필드와 동일하게 생성
    private String id;
    private String name;
    private String password;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
