package ru.samsung.appbooking;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Data_of_user_product {

    ArrayList<Integer> id_product = new ArrayList<>();
    ArrayList<String> name_product = new ArrayList<>();
    ArrayList<Integer> kol_vo = new ArrayList<>();
    ArrayList<Double> sum = new ArrayList<>();

    public ArrayList<String> getName_product() {
        return name_product;
    }

    public void setName_product(ArrayList<String> Name_product) {
        this.name_product = Name_product;
    }

    public ArrayList<Integer> getId_product() {
        return id_product;
    }

    public void setId_product(ArrayList<Integer> id_product) {
        this.id_product = id_product;
    }

    public void clear_arrays() {
        name_product.clear();
        id_product.clear();
        kol_vo.clear();
        sum.clear();
    }

    /*public void add_product(String name, int id, int kol_vo_, double price_of_product, Context context) {
        name_product.clear();
        id_product.clear();
        kol_vo.clear();
        sum.clear();

        name_product.add(name);
        id_product.add(id);
        kol_vo.add(kol_vo_);
        sum.add((price_of_product) * kol_vo_);

        writeArrayListToFile(name_product, id_product, kol_vo, sum, context);
    }*/


    public void change(String name, int kol, double price, Context context) {
        ArrayList<ArrayList<?>> data = readArrayListFromFile(context);
        name_product = (ArrayList<String>) data.get(0);
        id_product = (ArrayList<Integer>) data.get(1);
        kol_vo = (ArrayList<Integer>) data.get(2);
        sum = (ArrayList<Double>) data.get(3);

        int index = name_product.indexOf(name);
        kol_vo.set(index, kol);
        sum.set(index, (kol * price));
        onlywriteArrayListToFile(name_product, id_product, kol_vo, sum, context);


    }

    public void change_delete_product(String name,Context context){
        ArrayList<ArrayList<?>> data = readArrayListFromFile(context);
        name_product = (ArrayList<String>) data.get(0);
        id_product = (ArrayList<Integer>) data.get(1);
        kol_vo = (ArrayList<Integer>) data.get(2);
        sum = (ArrayList<Double>) data.get(3);

        int index = name_product.indexOf(name);
        if(index<0){
            name_product.remove(index);
            id_product.remove(index);
            kol_vo.remove(index);
            sum.remove(index);
            onlywriteArrayListToFile(name_product, id_product, kol_vo, sum, context);
        }


    }
//name_product, id_product, kol_vo, sum, context
    public void change_add_product(String name,int id, int kol_vo, Double sum, Context context){
        ArrayList<ArrayList<?>> data = readArrayListFromFile(context);
        if(check_file(context)){
            name_product = (ArrayList<String>) data.get(0);
            id_product = (ArrayList<Integer>) data.get(1);
            this.kol_vo = (ArrayList<Integer>) data.get(2);
            this.sum = (ArrayList<Double>) data.get(3);
        }
        else clear_arrays();


        name_product.add(name);
        id_product.add(id);
        this.kol_vo.add(kol_vo);
        this.sum.add(sum*kol_vo);
        onlywriteArrayListToFile(name_product, id_product, this.kol_vo, this.sum, context);
    }

    private void onlywriteArrayListToFile(ArrayList<String> list1, ArrayList<Integer> list2, ArrayList<Integer> list3, ArrayList<Double> list4, Context context) {
        try {
            // Открытие файла для записи во внутреннем хранилище приложения
            FileOutputStream fos = context.openFileOutput("data.txt", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            // Запись списков в файл
            oos.writeObject(list1);
            oos.writeObject(list2);
            oos.writeObject(list3);
            oos.writeObject(list4);

            // Закрытие потоков
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static ArrayList<ArrayList<?>> readArrayListFromFile(Context context) {
        ArrayList<ArrayList<?>> data = new ArrayList<>();

        try {
            // Открытие файла для чтения из внутреннего хранилища приложения
            FileInputStream fis = context.openFileInput("data.txt");
            ObjectInputStream ois = new ObjectInputStream(fis);

            // Чтение списков из файла
            ArrayList<String> list1 = (ArrayList<String>) ois.readObject();
            ArrayList<Integer> list2 = (ArrayList<Integer>) ois.readObject();
            ArrayList<Integer> list3 = (ArrayList<Integer>) ois.readObject();
            ArrayList<Double> list4 = (ArrayList<Double>) ois.readObject();

            // Добавление списков в результирующий список
            data.add(list1);
            data.add(list2);
            data.add(list3);
            data.add(list4);

            // Закрытие потоков
            ois.close();
            fis.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return data;
    }

    public boolean check_file(Context context) {
        File file = new File(context.getFilesDir(), "data.txt");
        return file.exists();
    }

}
