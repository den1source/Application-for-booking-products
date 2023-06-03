package ru.samsung.appbooking;

import android.content.Context;

import java.util.ArrayList;

public class Data {
    private String name;
    private Double price;
    private String time;
    private String path;
    private int Quantity;
    private int id;
    private Context context;
    private ArrayList<String> arr;

    public Data(int id, String name, Double price, String time, String path, Context context) {
        this.id=id;
        this.name = name;
        this.price = price;
        this.time = time;
        this.path = path;
        Quantity=0;
        this.context=context;
    }

    public Data(int id, String name, Double price, String time, int kol_vo, String path, Context context) {
        this.id=id;
        this.name = name;
        this.price = price;
        this.time = time;
        this.path = path;
        this.Quantity=kol_vo;
        this.context=context;
    }

    public String getTime() {
        return time;
    }

    public double getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int Quantity){
        this.Quantity=Quantity;
    }
    public int getId(){
        return id;
    }
    public Context getContext(){
        return context;
    }
}
