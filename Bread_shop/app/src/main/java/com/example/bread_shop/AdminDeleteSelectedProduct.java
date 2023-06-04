package com.example.bread_shop;
//
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AdminDeleteSelectedProduct extends AppCompatActivity {
    private ArrayList<Integer> ids = new ArrayList<>();
    private ArrayList<String> vids = new ArrayList<>();
    private ArrayList<String> price=new ArrayList<>();
    private ArrayList<String> time=new ArrayList<>();

    private List<String> selectedItems = new ArrayList<>();
    private ProgressDialog loadingBar;
    Button button_cancale;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_delete_category);
        loadingBar = new ProgressDialog(this);
        button_cancale = findViewById(R.id.button_cancale);
        getData();

    }

    private void getData() {
        ids.clear();
        vids.clear();
        time.clear();
        price.clear();

        AdminDeleteProductActivity adm=new AdminDeleteProductActivity();

        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://10.0.2.2:8080/data").newBuilder();
        urlBuilder.addQueryParameter("what_do", "number_of_products");
        urlBuilder.addQueryParameter("num", String.valueOf(adm.getName_product()));
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
                vids.add(arrayList.get(i + 1));
                price.add(arrayList.get(i + 2));
                time.add(arrayList.get(i + 3));
            }
            start();
        }
        else {
            Toast.makeText(AdminDeleteSelectedProduct.this, "Данная категория пуста(", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(AdminDeleteSelectedProduct.this,AdminDeleteProductActivity.class);
            startActivityForResult(i, 0);
        }

    }

    private void start() {
        ListView listView = findViewById(R.id.listView);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item_for_delete_category, R.id.textView, vids) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                CheckBox checkBox = view.findViewById(R.id.checkBox);
                TextView textView = view.findViewById(R.id.textView);

                // Получите выбранный элемент из ArrayList данных
                String selectedItem = vids.get(position);

                // Установите текст элемента списка
                textView.setText(vids.get(position)+"- "+price.get(position)+"₽,"+time.get(position)+"⏰");

                // Проверьте, является ли элемент выбранным
                boolean isSelected = selectedItems.contains(selectedItem);
                checkBox.setChecked(isSelected);

                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isSelected) {
                            // Если элемент уже выбран, удалите его из списка выбранных элементов
                            selectedItems.remove(selectedItem);
                        } else {
                            // Если элемент не выбран, добавьте его в список выбранных элементов
                            selectedItems.add(selectedItem);
                        }
                    }
                });

                return view;
            }
        };

        listView.setAdapter(adapter);

        button_cancale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedItems.clear();
                adapter.notifyDataSetChanged();
            }
        });
    }



    public void delete(View view) {
        loadingBar.setTitle("Обработка");
        loadingBar.setMessage("Пожалуйста, подождите...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        if (selectedItems.size() != 0) {
            String name_ = "";
            String id_="";
            for (String str : selectedItems) {
                name_+=(str+",");
                id_+=(ids.get(vids.indexOf(str))+",");
            }
            OkHttpClient client = new OkHttpClient();
            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://10.0.2.2:8080/product").newBuilder();
            urlBuilder.addQueryParameter("what_do", "delete_product");
            urlBuilder.addQueryParameter("str", name_);
            urlBuilder.addQueryParameter("id", id_);
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
                                get_otvet(responseData);
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }
            });

        } else {
            loadingBar.dismiss();
            Toast.makeText(AdminDeleteSelectedProduct.this, "Ничего не выбрано!", Toast.LENGTH_SHORT).show();
        }
    }

    private void get_otvet(String res) {
        loadingBar.dismiss();
        System.out.println("11111111111"+res);
        if (res.equals("true")) {
            selectedItems.clear();
            getData();
            Toast.makeText(AdminDeleteSelectedProduct.this, "Успешно!", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(AdminDeleteSelectedProduct.this, "Ошибка на сервере!", Toast.LENGTH_SHORT).show();
        }
    }
}
