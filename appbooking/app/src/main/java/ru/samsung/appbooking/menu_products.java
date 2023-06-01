package ru.samsung.appbooking;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

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

public class menu_products extends AppCompatActivity {
    ArrayList<String> product = new ArrayList<>();
    ArrayList<String> price = new ArrayList<>();
    ArrayList<String> time = new ArrayList<>();
    ArrayList<Integer> ids = new ArrayList<>();

    private int c, size;
    Main_menu main_menu = new Main_menu();
    ArrayList<Data> datas = new ArrayList<Data>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fetchData();
    }

    private void fetchData() {
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://10.0.2.2:8080/data").newBuilder();
        urlBuilder.addQueryParameter("what_do", "number_of_products");
        urlBuilder.addQueryParameter("num", String.valueOf(main_menu.get_image_index()));
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
        size = arrayList.size();
        for (int i = 0; i < size; i += 4) {
            ids.add(Integer.valueOf(arrayList.get(i)));
            product.add(arrayList.get(i + 1));
            price.add(arrayList.get(i + 2));
            time.add(arrayList.get(i + 3));
        }

        for (int i : ids) {
            int finalI = i;
            new Thread(() -> {
                post(finalI);
            }).start();
        }
    }

    private void post(int imageIndex) {
        c = 0;
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
                            c++;
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

            String imagePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/product" + num + ".jpg";
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

            if (c == (size/4)) {
                start_();
            }
        }
    }

    public void start_() {
        setContentView(R.layout.menu_products);
        datas.clear();
        for (int i = 0; i < (size/4); i++) {
            datas.add(new Data(product.get(i), price.get(i), time.get(i), getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/product" + ids.get(i) + ".jpg"));
        }

        RecyclerView recyclerView = findViewById(R.id.list);
        DataAdapter adapter = new DataAdapter(this, datas);
        recyclerView.setAdapter(adapter);
    }
}
