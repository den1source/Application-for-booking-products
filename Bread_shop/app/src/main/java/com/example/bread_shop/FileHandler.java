package com.example.bread_shop;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class FileHandler {//  id    kol_vo

    private static final String FILENAME = "data.dat";

    public static void saveData(Context context, ArrayList<Integer> strings, ArrayList<Integer> integers) {
        if (checkFileExists(context)) {
            try {
                ArrayList<Object> arr_old_data = loadData(context);
                ArrayList<Integer> old_data_1 = (ArrayList<Integer>) arr_old_data.get(0);
                ArrayList<Integer> old_data_2 = (ArrayList<Integer>) arr_old_data.get(1);

                for (int i = 0; i < old_data_1.size(); i++) {
                    if (!strings.contains(old_data_1.get(i))) {
                        strings.add(old_data_1.get(i));
                        integers.add(old_data_2.get(i));
                    } else {
                        int index = strings.indexOf(old_data_1.get(i));
                        if (index != -1) {
                            strings.set(index, old_data_1.get(i));
                            integers.set(index, old_data_2.get(i));
                        } else {
                            strings.add(old_data_1.get(i));
                            integers.add(old_data_2.get(i));
                        }
                    }
                }


                System.out.println("!!!!!!!!!!!!save_kol!!!!!!!!!!!" + integers);
                FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(strings);
                oos.writeObject(integers);
                oos.close();
                fos.close();
            } catch (IOException e) {
                Log.e("FileHandler", "Error saving data: " + e.getMessage());
            }
        } else {
            try {
                System.out.println("!!!!!!!!!!!!save_kol!!!!!!!!!!!" + integers);
                FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(strings);
                oos.writeObject(integers);
                oos.close();
                fos.close();
            } catch (IOException e) {
                Log.e("FileHandler", "Error saving data: " + e.getMessage());
            }
        }
    }

    public static ArrayList<Object> loadData(Context context) {

        ArrayList<Object> data = new ArrayList<>();
        try {
            FileInputStream fis = context.openFileInput(FILENAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            ArrayList<Integer> strings = (ArrayList<Integer>) ois.readObject();
            ArrayList<Integer> integers = (ArrayList<Integer>) ois.readObject();
            data.add(strings);
            data.add(integers);
            ois.close();
            fis.close();
            System.out.println("!!!!!!!!!!!!load!!!!!!!!!!!" + strings);
        } catch (IOException | ClassNotFoundException e) {
            Log.e("FileHandler", "Error loading data: " + e.getMessage());
        }
        return data;
    }


    public static boolean checkFileExists(Context context) {
        File file = context.getFileStreamPath(FILENAME);
        return file.exists();
    }

    public static void deleteFile(Context context) {
        File file = context.getFileStreamPath(FILENAME);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (!deleted) {
                Log.e("FileHandler", "Error deleting file");
            }
        }
    }

    public static void delete_this_product(Context context, ArrayList<Integer> ids, ArrayList<Integer> kolvo) throws IOException {
        ArrayList<Object> arr_old_data = loadData(context);
        ArrayList<Integer> old_data_1 = (ArrayList<Integer>) arr_old_data.get(0);
        ArrayList<Integer> old_data_2 = (ArrayList<Integer>) arr_old_data.get(1);
        for (int i = 0; i < old_data_1.size(); i++) {
            if (ids.contains(old_data_1.get(i))) {
                int index = ids.indexOf(old_data_1.get(i));
                ids.set(index, old_data_1.get(i));
                kolvo.set(index, old_data_2.get(i));
            }
            else {
                ids.clear();
                kolvo.size();
            }
        }


        System.out.println("!!!!!!!!!!!!save_kol!!!!!!!!!!!" + kolvo);
        FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(ids);
        oos.writeObject(kolvo);
        oos.close();
        fos.close();
    }



}
