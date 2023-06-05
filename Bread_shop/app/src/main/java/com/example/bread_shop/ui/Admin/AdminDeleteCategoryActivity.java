package com.example.bread_shop.ui.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bread_shop.R;
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

public class AdminDeleteCategoryActivity extends AppCompatActivity {

    private ArrayList<Integer> ids = new ArrayList<>();
    private ArrayList<String> vids = new ArrayList<>();
    private List<String> selectedItems = new ArrayList<>();
    private ProgressDialog loadingBar;
    Button button_cancale;


    @SuppressLint("MissingInflatedId")
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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.textView, vids) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                CheckBox checkBox = view.findViewById(R.id.checkBox);
                TextView textView = view.findViewById(R.id.textView);

                // Получите выбранный элемент из ArrayList данных
                String selectedItem = vids.get(position);

                // Установите текст элемента списка
                textView.setText(selectedItem);

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




    private void get_otvet(String res) {
        loadingBar.dismiss();
        if (res.equals("true")) {
            selectedItems.clear();
            getData();
            Toast.makeText(AdminDeleteCategoryActivity.this, "Успешно!", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(AdminDeleteCategoryActivity.this, "Ошибка на сервере!", Toast.LENGTH_SHORT).show();
        }
    }


    public void delete(View view) {
        loadingBar.setTitle("Обработка");
        loadingBar.setMessage("Пожалуйста, подождите...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        if (selectedItems.size() != 0) {
            String s = "";
            for (String str : selectedItems) {
                int index = vids.indexOf(str);
                s += (ids.get(index) + ",");
            }
            System.out.println(s);
            OkHttpClient client = new OkHttpClient();
            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://10.0.2.2:8080/type").newBuilder();
            urlBuilder.addQueryParameter("what_do", "delete_type");
            urlBuilder.addQueryParameter("str", s);
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
            Toast.makeText(AdminDeleteCategoryActivity.this, "Ничего не выбрано!", Toast.LENGTH_SHORT).show();
        }
    }
}