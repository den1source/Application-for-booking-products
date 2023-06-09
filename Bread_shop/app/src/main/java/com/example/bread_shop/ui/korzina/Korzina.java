package com.example.bread_shop.ui.korzina;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bread_shop.FileHandler;
import com.example.bread_shop.R;
import com.example.bread_shop.make_oder;
import com.example.bread_shop.ui.account.order;
import com.example.bread_shop.ui.home.UserHomeActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Korzina extends AppCompatActivity {

    ArrayList<String> product_name = new ArrayList<>();
    ArrayList<Double> price_one_product = new ArrayList<>();
    static ArrayList<Double> price_some_products = new ArrayList<>();
    ArrayList<Integer> time = new ArrayList<>();
    ArrayList<Integer> ids_from_file = new ArrayList<>();
    ArrayList<Integer> ids=new ArrayList<>();
    ArrayList<Integer> kol_vo_from_file = new ArrayList<>();
    int c_for_data, c_for_image;

    ArrayList<data_for_korzina> data_for_korzina = new ArrayList<data_for_korzina>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        start_act();

    }

    private void start_act(){
        setContentView(R.layout.korzina);
        data_for_korzina.clear();
        ids.clear();
        ids_from_file.clear();
        kol_vo_from_file.clear();
        product_name.clear();
        price_one_product.clear();
        price_some_products.clear();
        time.clear();
        c_for_data = 0;
        c_for_image = 0;

        if (FileHandler.checkFileExists(this)) {
            ArrayList<Object> data = FileHandler.loadData(this);

            if (data.size() != 0) {

                ids_from_file = (ArrayList<Integer>) data.get(0);
                kol_vo_from_file = (ArrayList<Integer>) data.get(1);
                System.out.println("load_from_file!!!!!!!!!!!!"+ids_from_file);
                for (int id : ids_from_file) {
                    fetchData(id);
                }
            } else {
                TextView textView = findViewById(R.id.kol_vo_canc_zakakov);
                textView.setText("Корзина пуста!");
            }
        } else {

            TextView textView = findViewById(R.id.kol_vo_canc_zakakov);
            textView.setText("Корзина пуста!");
        }

    }

    private void fetchData(int id) {
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://10.0.2.2:8080/data").newBuilder();
        urlBuilder.addQueryParameter("what_do", "get_id");
        urlBuilder.addQueryParameter("num", String.valueOf(id));
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .cacheControl(new CacheControl.Builder().maxStale(30, TimeUnit.DAYS).build())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    String responseData = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            c_for_data++;
                            get_arrays(responseData);

                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void get_arrays(String res) {
        Gson gson = new Gson();
        ArrayList<String> data_from_bd = gson.fromJson(res, new TypeToken<ArrayList<String>>() {}.getType());


        int index_for_kolvo = ids_from_file.indexOf(Integer.parseInt(data_from_bd.get(0)));
        ids.add(Integer.parseInt(data_from_bd.get(0)));
        product_name.add(data_from_bd.get(1));
        price_one_product.add(Double.valueOf(data_from_bd.get(2)));
        System.out.println("!!!!!!!!!"+kol_vo_from_file);
        price_some_products.add((Double.parseDouble(data_from_bd.get(2))) * 3);
        time.add(Integer.valueOf(data_from_bd.get(3)));

        if (c_for_data == ids_from_file.size()) {
            c_for_image = 0;
            for (int i : ids) {
                int finalI = i;
                new Thread(() -> {
                    post(finalI);
                }).start();
            }
        }
    }

    private void post(int imageIndex) {

        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://10.0.2.2:8080/image").newBuilder();
        urlBuilder.addQueryParameter("what_do", "images_products");
        urlBuilder.addQueryParameter("number", String.valueOf(imageIndex));
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .cacheControl(new CacheControl.Builder().maxStale(30, TimeUnit.DAYS).build())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    ResponseBody responseData = response.body();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            c_for_image++;
                            new ImageSaveTask().execute(responseData, String.valueOf(imageIndex));
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }

    private class ImageSaveTask extends AsyncTask<Object, Void, Void> {
        private ResponseBody responseData;
        private String num;

        @Override
        protected Void doInBackground(Object... params) {
            responseData = (ResponseBody) params[0];
            num = (String) params[1];

            String imagePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/korzina" + num + ".jpg";
            File file = new File(imagePath);

            try (OutputStream fos = new FileOutputStream(file)) {
                // Save data from ResponseBody to file
                fos.write(responseData.bytes());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (c_for_image == (ids.size())) {
                start();
            }

        }
    }

    public static ArrayList<Double> getPrice_some_products(){
        return price_some_products;
    }

    private void start() {
        int c=0;
        System.out.println("!!!!!!!!!!!!!!!"+ids);
        for (int i = 0; i < ids.size(); i++) {
            c++;
            data_for_korzina.add(new data_for_korzina(ids.get(i), product_name.get(i), time.get(i), price_some_products.get(i), getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/korzina" + ids.get(i) + ".jpg", this));
        }
        if(c==product_name.size()){
            RecyclerView recyclerView = findViewById(R.id.list);
            Adapter_for_korzina adapter = new Adapter_for_korzina(this, data_for_korzina);

            // Добавление менеджера макета (например, LinearLayoutManager)
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);

            recyclerView.setAdapter(adapter);
        }
        else {
            TextView textView = findViewById(R.id.kol_vo_canc_zakakov);
            textView.setText("Корзина пуста!");
        }
    }


    public void menu(View v){
        Intent i = new Intent(Korzina.this, UserHomeActivity.class);
        startActivityForResult(i, 0);
    }

    public void clear_korzina(View v){
        Toast.makeText(v.getContext(), "Корзина очищена", Toast.LENGTH_SHORT).show();
        FileHandler.deleteFile(v.getContext());
        start_act();
    }

    public void make_order1(View v){
        if(FileHandler.checkFileExists(Korzina.this)){
            Intent i = new Intent(Korzina.this, make_oder.class);
            startActivityForResult(i, 0);
        }
        else {
            Toast.makeText(v.getContext(), "Бронировать нечего", Toast.LENGTH_SHORT).show();
        }

    }

    public void korzina(View v) {
        Intent i = new Intent(Korzina.this, Korzina.class);
        startActivityForResult(i, 0);
    }

    public void order(View v){
        Intent i = new Intent(Korzina.this, order.class);
        startActivityForResult(i, 0);
    }


}
