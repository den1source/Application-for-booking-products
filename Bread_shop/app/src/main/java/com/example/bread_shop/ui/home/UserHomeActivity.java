package com.example.bread_shop.ui.home;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.bread_shop.R;
import com.example.bread_shop.ui.account.order;
import com.example.bread_shop.ui.korzina.Korzina;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

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


public class UserHomeActivity extends AppCompatActivity {


    int vol_images;

    private ArrayList<Integer> ids = new ArrayList<>();
    private ArrayList<String> categories = new ArrayList<>();

    ArrayList<Data> data = new ArrayList<Data>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);


        try {
            post_req();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void post_req() throws InterruptedException {
        vol_images = 0;
        ids.clear();
        categories.clear();

        getTexts();


    }

    ///////////////
    private void getTexts() {//получ текст


        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://10.0.2.2:8080/data").newBuilder();
        urlBuilder.addQueryParameter("what_do", "gettypes");
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
        for (int i = 0; i < arrayList.size(); i += 2) {
            ids.add(Integer.valueOf(arrayList.get(i)));
            categories.add(arrayList.get(i + 1));
        }

        for (int i : ids) {
            new Thread(() -> {
                getImages(i);
            }).start();
        }

    }

    /////////////
    private void getImages(Integer imageIndex) {
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://10.0.2.2:8080/image").newBuilder();
        urlBuilder.addQueryParameter("what_do", "images_menu");
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

    class ImageSaveTask extends AsyncTask<Object, Void, Void> {
        private ResponseBody responseData;
        private String num;

        @Override
        protected Void doInBackground(Object... params) {
            responseData = (ResponseBody) params[0];
            num = (String) params[1];

            String imagePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/categories" + num + ".jpg";
            File file = new File(imagePath);

            try (OutputStream fos = new FileOutputStream(file)) {
                // Save data from ResponseBody to file
                fos.write(responseData.bytes());
                fos.close();
                vol_images++;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (vol_images == ids.size() && ids.size() != 0) {
                start();
            }
        }
    }

    ///////////////////

    private void start() {//item image//user home

        for (int i = 0; i < ids.size(); i++) {
            data.add(new Data(ids.get(i), categories.get(i), getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/categories" + ids.get(i) + ".jpg", this));
        }

        RecyclerView recyclerView = findViewById(R.id.list);
        DataAdapterCategories adapter = new DataAdapterCategories(this, data);
        recyclerView.setAdapter(adapter);
    }


    public void korzina(View v) {
        Intent i = new Intent(UserHomeActivity.this, Korzina.class);
        startActivityForResult(i, 0);
    }

    public void order(View v) {
        Intent i = new Intent(UserHomeActivity.this, order.class);
        startActivityForResult(i, 0);
    }






}