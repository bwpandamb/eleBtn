package com.charlesma;

public class FoodModel {
    private int num;
    private String desc;

    public FoodModel(int num, String desc) {
        this.num = num;
        this.desc = desc;
    }

    public FoodModel() {
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
