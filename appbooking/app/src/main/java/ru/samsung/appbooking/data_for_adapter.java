package ru.samsung.appbooking;

public class data_for_adapter {
    private int id;
    private String name;
    private String price;
    private String time;
    private String path;


    public data_for_adapter(String name, String price, String time,int id, String path){
        this.id=id;
        this.name=name;
        this.price=price;
        this.time=time;
        this.path=path;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public String getPrice() {
        return price;
    }

    public String getTime() {
        return time;
    }

    public String getPath(){
        return path;
    }
}
