package com.example.product1;

public class ProductVO {
    private String code;
    private String pname;
    private int price;
    private String image;

    public String getCode() {
        return code;
    }

    public String getPname() {
        return pname;
    }

    public int getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
