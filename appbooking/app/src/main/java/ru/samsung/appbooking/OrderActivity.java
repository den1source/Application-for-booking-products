package ru.samsung.appbooking;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

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

public class OrderActivity extends AppCompatActivity {
    Data_of_user_product data_w_r=new Data_of_user_product();
    int size, c;
    ArrayList<String> product = new ArrayList<>();
    ArrayList<String> price = new ArrayList<>();
    ArrayList<String> time = new ArrayList<>();
    ArrayList<Integer> ids = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_products);

        ArrayList<ArrayList<?>> data=data_w_r.readArrayListFromFile(this);
        ArrayList<Integer> list2 = (ArrayList<Integer>) data.get(1);
        System.out.println("11111111111111111111");
        for(int id:list2){
            new Thread(()->{
                fetchData(id);
            }).start();
        }

    }

    private void fetchData(int id) {
        c=0;
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

    public void get_arrays(String res){
        c++;
        Gson gson = new Gson();
        ArrayList<String> arrayList = gson.fromJson(res, new TypeToken<ArrayList<String>>() {
        }.getType());
        size = arrayList.size();
        for (int i = 0; i < size; i += 4) {
            int x=Integer.parseInt(arrayList.get(i));
            ids.add(x);
            int index=ids.indexOf(x);
            product.add(index, arrayList.get(i + 1));
            price.add(index, arrayList.get(i + 2));
            time.add(index, arrayList.get(i + 3));
        }
        if(c==1){
            System.out.println(ids);
            System.out.println(product);
            System.out.println(price);
            System.out.println(time);
        }
    }

}
