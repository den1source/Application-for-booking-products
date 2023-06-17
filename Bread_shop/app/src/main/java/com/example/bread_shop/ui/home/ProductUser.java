package com.example.bread_shop.ui.home;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bread_shop.FileHandler;
import com.example.bread_shop.R;
import com.example.bread_shop.ui.korzina.Korzina;
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

public class ProductUser extends AppCompatActivity {
    private int id, c;
    ArrayList<Integer> ids = new ArrayList<>();
    ArrayList<String> product = new ArrayList<>();
    ArrayList<Double> price = new ArrayList<>();
    ArrayList<Integer> time = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.productuser);

        this.id = getIntent().getIntExtra("id", 0);
        c=0;
        ids.size();
        product.size();
        price.size();
        time.size();

        fetchData();
    }

    private void fetchData() {
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://10.0.2.2:8080/data").newBuilder();
        urlBuilder.addQueryParameter("what_do", "number_of_products");
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
        if(arrayList.size()!=0){
            for (int i = 0; i < arrayList.size(); i += 4) {
                ids.add(Integer.valueOf(arrayList.get(i)));
                product.add(arrayList.get(i + 1));
                price.add(Double.valueOf(arrayList.get(i + 2)));
                time.add(Integer.valueOf(arrayList.get(i + 3)));
            }

            for (int i : ids) {
                int finalI = i;
                new Thread(() -> {
                    post(finalI);
                }).start();
            }
        }
        else {
            Toast.makeText(ProductUser.this, "Все из данной категории у вас в корзине)", Toast.LENGTH_SHORT).show();
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

            String imagePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/product" + num + ".jpg";
            File file = new File(imagePath);

            try (OutputStream fos = new FileOutputStream(file)) {
                // Save data from ResponseBody to file
                fos.write(responseData.bytes());
                fos.close();
                c++;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (c == ids.size()) {
                start();
            }
        }
    }

    ArrayList<Data> datas = new ArrayList<Data>();

    public void start(){
        if(!FileHandler.checkFileExists(this)){
            for (int i = 0; i < ids.size(); i++) {
                datas.add(new Data(ids.get(i), product.get(i), time.get(i), price.get(i), getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/product" + ids.get(i) + ".jpg", this));
            }
        }
        else {
            ArrayList<Integer> select_product= (ArrayList<Integer>) FileHandler.loadData(this).get(0);
            for (int i = 0; i < ids.size(); i++) {
                if(!select_product.contains(ids.get(i))){
                    datas.add(new Data(ids.get(i), product.get(i), time.get(i), price.get(i), getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/product" + ids.get(i) + ".jpg", this));
                }
            }
        }

        RecyclerView recyclerView = findViewById(R.id.list);
        DataAdapterProduct adapter = new DataAdapterProduct(this, datas);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    public void korzina(View v){
        Intent i = new Intent(v.getContext(), Korzina.class);
        startActivityForResult(i, 0);
    }

}
