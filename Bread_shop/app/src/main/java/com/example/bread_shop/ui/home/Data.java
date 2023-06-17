package com.example.bread_shop.ui.home;

import android.content.Context;

public class Data {

    private String name; // название
    private int id;  // столица
    private String path; // ресурс флага
    private int time;
    private double price;
    private int kol_vo;
    private Context context;


    public Data(int id, String name, String path, Context context){
        this.name=name;
        this.id=id;
        this.path=path;
        this.context=context;
    }

    public Data(int id, String name, int time, double price, String path, Context context){
        this.name=name;
        this.id=id;
        this.time=time;
        this.price=price;
        this.path=path;
        this.context=context;
        kol_vo=0;
    }

    public void kol_plus(){
        kol_vo++;
    }
    public void kol_minus(){
        kol_vo--;
    }

    /*public void setKol_vo(int kol_vo) {
        this.kol_vo = kol_vo;
    }*/

    public int getKol_vo() {
        return kol_vo;
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
