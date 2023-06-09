package com.example.bread_shop;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bread_shop.ui.account.order;
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

public class Statictika_for_admina extends AppCompatActivity {
    private ArrayList<String> ids = new ArrayList<>();
    private ArrayList<Integer> id_user = new ArrayList<>();
    private List<String> selectedItems = new ArrayList<>();
    private ProgressDialog loadingBar;
    Button button_cancale;
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getData();

    }

    private void getData() {
        setContentView(R.layout.statictika_for_admina);
        loadingBar = new ProgressDialog(this);
        button_cancale = findViewById(R.id.button_cancale);
        ids.clear();
        id_user.clear();
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://10.0.2.2:8080/order").newBuilder();
        urlBuilder.addQueryParameter("what_do", "all");
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
        textView = findViewById(R.id.textView7);
        Gson gson = new Gson();
        ArrayList<String> arrayList = gson.fromJson(responseData, new TypeToken<ArrayList<String>>() {
        }.getType());
        if (arrayList.size() != 0) {
            for (int i = 0; i < arrayList.size(); i += 2) {
                id_user.add(Integer.valueOf(arrayList.get(i)));
                ids.add(String.valueOf(arrayList.get(i + 1)));
            }
            start();
        } else {
            textView.setText("Заказов нет");
        }
    }

    private void start() {
        ListView listView = findViewById(R.id.listView);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item_for_delete_category, R.id.netZakazov, ids) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                CheckBox checkBox = view.findViewById(R.id.checkBox);
                TextView textView = view.findViewById(R.id.netZakazov);

                // Получите выбранный элемент из ArrayList данных
                String selectedItem = ids.get(position);

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
            Toast.makeText(Statictika_for_admina.this, "Успешно!", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(Statictika_for_admina.this, "Ошибка на сервере!", Toast.LENGTH_SHORT).show();
        }
    }


    public void delete(View view) {
        loadingBar.setTitle("Обработка");
        loadingBar.setMessage("Пожалуйста, подождите...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        if (selectedItems.size() != 0) {
            String user = "";
            String ord_num = "";
            for (String str : selectedItems) {
                int index = ids.indexOf(str);
                user += (id_user.get(index) + ",");
                ord_num += (ids.get(index) + ",");
                System.out.println(ids.get(index));
            }
            OkHttpClient client = new OkHttpClient();
            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://10.0.2.2:8080/order_do").newBuilder();
            urlBuilder.addQueryParameter("what_do", "adm_del_or");
            urlBuilder.addQueryParameter("id", user);
            urlBuilder.addQueryParameter("ord", ord_num);
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
            Toast.makeText(Statictika_for_admina.this, "Ничего не выбрано!", Toast.LENGTH_SHORT).show();
        }
    }
}
