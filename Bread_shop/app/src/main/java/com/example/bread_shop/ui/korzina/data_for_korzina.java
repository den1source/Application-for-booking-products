package com.example.bread_shop.ui.korzina;

import android.content.Context;

public class data_for_korzina {
    private String name; // название
    private int id;  // столица
    private String path; // ресурс флага
    private int time;
    private double price;
    private int kol_vo;
    private Context context;



    public data_for_korzina(int id, String name, int time, double price, String path, Context context){
        this.name=name;
        this.id=id;
        this.time=time;
        this.price=price;
        this.path=path;
        this.context=context;
        kol_vo=3;
    }

    public void setKol_vo(int kol_vo) {
        this.kol_vo = kol_vo;
    }

    public int getKol_vo() {
        return this.kol_vo;
    }

    public double getPrice() {
        return price;
    }

    public int getTime() {
        return time;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getPath() {
        return path;
    }
    public Context getContext(){
        return context;
    }
}
