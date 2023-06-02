package ru.samsung.appbooking;

import android.content.Context;

public class Data {
    private String name;
    private String price;
    private String time;
    private String path;
    private int Quantity;
    private int id;
    private Context context;
    //private int imageResource;

    public Data(int id, String name, String price, String time, String path, Context context) {
        this.id=id;
        this.name = name;
        this.price = price;
        this.time = time;
        this.path = path;
        Quantity=0;
        this.context=context;
    }

    public String getTime() {
        return time;
    }

    public String getPrice() {
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
