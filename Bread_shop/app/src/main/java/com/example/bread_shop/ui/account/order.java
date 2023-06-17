package com.example.bread_shop.ui.account;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bread_shop.R;
import com.example.bread_shop.UserDataManager;
import com.example.bread_shop.information_order;
import com.example.bread_shop.ui.home.UserHomeActivity;
import com.example.bread_shop.ui.korzina.Korzina;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class order extends AppCompatActivity {
    private int user;
    private ArrayList<Long> id_order = new ArrayList<>();
    private ArrayList<Double> summ = new ArrayList<>();
    private ArrayList<String> text = new ArrayList<>();
    ArrayList<Data_for_order> Data_for_order = new ArrayList<Data_for_order>();
    ArrayList<Integer> product = new ArrayList<>();
    TextView textView;
    TextView kol_vo_zakazov, kol_can_zakazov;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order);
        textView = findViewById(R.id.netZakazov);
        kol_vo_zakazov=findViewById(R.id.kol_vo_zakazov);
        kol_can_zakazov=findViewById(R.id.kol_vo_canc_zakakov);

        try {
            post_req();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void post_req() throws InterruptedException {


        loadingBar = new ProgressDialog(this);

        user = Integer.parseInt(UserDataManager.getUsername(order.this));

        id_order.clear();
        summ.clear();
        product.clear();

        new Thread(()->{
            getTexts();
        }).start();
        new Thread(()->{
            getData();
        }).start();

    }

    ///////////////

    private void getData(){
        user = Integer.parseInt(UserDataManager.getUsername(order.this));
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://10.0.2.2:8080/order").newBuilder();
        urlBuilder.addQueryParameter("what_do", "get_size_order");
        urlBuilder.addQueryParameter("id", String.valueOf(user));
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
                            arr(responseData);
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


    private void arr(String res){
        Gson gson = new Gson();
        ArrayList<String> arrayList = gson.fromJson(res, new TypeToken<ArrayList<String>>() {
        }.getType());
        kol_vo_zakazov.setText(String.valueOf(arrayList.get(1)));
        kol_can_zakazov.setText(String.valueOf(arrayList.get(0)));
    }
    ///////////////
    private void getTexts() {//получ текст

        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://10.0.2.2:8080/order").newBuilder();
        urlBuilder.addQueryParameter("what_do", "get_ors");
        urlBuilder.addQueryParameter("user", String.valueOf(user));
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
                            try {
                                processResponseData(responseData);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
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


    private void processResponseData(String responseData) throws InterruptedException {
        Gson gson = new Gson();
        ArrayList<String> arrayList = gson.fromJson(responseData, new TypeToken<ArrayList<String>>() {
        }.getType());
        System.out.println(arrayList);
        if (arrayList.size() != 0) {
            for (int i = 0; i < arrayList.size(); i += 3) {
                if (!id_order.contains(Long.parseLong(arrayList.get(i)))) {
                    id_order.add(Long.parseLong(arrayList.get(i)));
                    summ.add(Double.valueOf(arrayList.get(i + 1)));

                } else {
                    summ.set(summ.size() - 1, summ.get(summ.size() - 1) + Double.parseDouble(arrayList.get(i + 1)));
                }

                //product.add(Integer.parseInt(arrayList.get(i + 3)));
            }

            start();
        } else {
            textView.setText("Заказов нет(");
        }
    }


    ///////////////////

    private void start() {//item image//user home

        for (int i = 0; i < id_order.size(); i++) {
            Data_for_order.add(new Data_for_order(id_order.get(i), summ.get(i), order.this));

            RecyclerView recyclerView = findViewById(R.id.list);
            DataAdapterOrder adapter = new DataAdapterOrder(this, Data_for_order);
            recyclerView.setAdapter(adapter);
        }
    }

    public void menu(View v) {
        Intent i = new Intent(v.getContext(), UserHomeActivity.class);
        startActivityForResult(i, 0);
    }

    public void korzina(View v) {
        Intent i = new Intent(v.getContext(), Korzina.class);
        startActivityForResult(i, 0);
    }

    /////////////////////////
    private ProgressDialog loadingBar;

    public void delete_order() {
        loadingBar.setTitle("Обработка запроса");
        loadingBar.setMessage("Пожалуйста, подождите...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://10.0.2.2:8080/order_do").newBuilder();
        urlBuilder.addQueryParameter("what_do", "delete_order");
        urlBuilder.addQueryParameter("id", String.valueOf(getId_or()));
        urlBuilder.addQueryParameter("user", String.valueOf(user));
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
                            ger_res_1(responseData);
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

    public void accept_order(View v, Long id) {
        loadingBar.setTitle("Обработка запроса");
        loadingBar.setMessage("Пожалуйста, подождите...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://10.0.2.2:8080/order_do").newBuilder();
        urlBuilder.addQueryParameter("what_do", "acc");
        urlBuilder.addQueryParameter("id", String.valueOf(id));
        urlBuilder.addQueryParameter("user", String.valueOf(user));
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
                            ger_res_1(responseData);
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

    public void ger_res_1(String res) {
        loadingBar.dismiss();
        getTexts();
        if (res.equals("true")) {
            try {
                post_req();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Toast.makeText(getApplicationContext(), "Успешно", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Ошбика", Toast.LENGTH_SHORT).show();
        }
    }

    private static long id_or;

    public static void setId_or(long id_or) {
        order.id_or = id_or;
    }

    public void information(){
        Context context=order.this;
        Intent i = new Intent(order.this, information_order.class);
        startActivityForResult(i, 0);
    }

    public long getId_or() {
        return id_or;
    }



}
