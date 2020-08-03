package com.example.memo;

public class Memo {
    String key;
    String content;
    String createDate;
    String updateDate;
    // HashMap<> 메소드를 사용하지 않고 Memo class 사용

    // Getter 메소드
    public String getKey() {
        return key;
    }

    public String getContent() {
        return content;
    }

    public String getCreateDate() {
        return createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    // Setter 메소드
    public void setKey(String key) {
        this.key = key;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
}
