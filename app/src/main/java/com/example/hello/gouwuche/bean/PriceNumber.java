package com.example.hello.gouwuche.bean;

/**
 * Created by dell on 2018/3/10.
 */

public class PriceNumber {
    private int price;
    private int num;

    public PriceNumber(int price, int num) {
        this.price = price;
        this.num = num;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
