package com.example.bread_shop.ui.account;

import android.content.Context;

public class Data_for_order {
    private long id;  // столица
    private double sum;
    private Context context;


    public Data_for_order(long id, double sum, Context context){
        this.id=id;
        this.sum=sum;
        this.context=context;
    }

    public long getId() {
        return id;
    }

    public Context getContext() {
        return context;
    }

    public double getSum() {
        return sum;
    }
}
