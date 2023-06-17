package com.example.bread_shop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class AdminDeleteProductActivity extends AppCompatActivity {
    private ArrayList<Integer> ids = new ArrayList<>();
    private ArrayList<String> vids = new ArrayList<>();
    private int name_product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_category_for_add_new_product);
        getData();
    }

    public int getName_product(){
        return this.name_product;
    }

    private void getData() {

        ids.clear();
        vids.clear();

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
            vids.add(arrayList.get(i + 1));
        }
        start();
    }

    private void start() {
        ListView listView = findViewById(R.id.listView);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item_for_add_product, R.id.netZakazov, vids) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                CheckBox checkBox = view.findViewById(R.id.checkBox);
                TextView textView = view.findViewById(R.id.netZakazov);

                // Получите выбранный элемент из ArrayList данных
                String selectedItem = vids.get(position);

                // Установите текст элемента списка
                textView.setText(selectedItem);


                return view;
            }
        };

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Получите выбранный элемент из ArrayList данных
                String selectedItem = vids.get(position);

                // Отобразите сообщение с названием элемента
                Toast.makeText(getApplicationContext(), selectedItem, Toast.LENGTH_SHORT).show();
                name_product=ids.get(vids.indexOf(selectedItem));
                Intent i = new Intent(AdminDeleteProductActivity.this, AdminDeleteSelectedProduct.class);
                startActivityForResult(i, 0);
            }
        });
    }
}








