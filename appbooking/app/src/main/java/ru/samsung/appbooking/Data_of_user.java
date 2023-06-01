package ru.samsung.appbooking;

import java.util.ArrayList;

public class Data_of_user {

    ArrayList<Integer> id_product=new ArrayList<>();
    ArrayList<String> name_product=new ArrayList<>();
    ArrayList<Double> sum=new ArrayList<>();

    public ArrayList<String> getName_product(){
        return name_product;
    }

    public void setName_product(ArrayList<String> Name_product){
        this.name_product=Name_product;
    }

    public ArrayList<Integer> getId_product(){
        return id_product;
    }

    public void setId_product(ArrayList<Integer> id_product){
        this.id_product=id_product;
    }

}
