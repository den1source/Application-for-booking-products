package ru.samsung.appbooking;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OrderActivity extends AppCompatActivity {
    Data_of_user_product data_w_r=new Data_of_user_product();
    int size, c_1, c_2, size_for_l;
    ArrayList<String> product = new ArrayList<>();
    ArrayList<String> product_f = new ArrayList<>();
    ArrayList<String> price = new ArrayList<>();
    ArrayList<String> time = new ArrayList<>();
    ArrayList<Integer> ids = new ArrayList<>();
    ArrayList<Integer> kol_vo=new ArrayList<>();
    ArrayList<Data> datas = new ArrayList<Data>();



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_products);
        Data_of_user_product data_w_r_=new Data_of_user_product();
        if(data_w_r_.check_file(this)){
            ArrayList<ArrayList<?>> data=data_w_r.readArrayListFromFile(this);
            ArrayList<Integer> list2 = (ArrayList<Integer>) data.get(1);
            kol_vo = (ArrayList<Integer>) data.get(2);
            product=(ArrayList<String>) data.get(0);


            c_1=0;
            System.out.println(price);
            System.out.println(list2);//ids
            System.out.println(kol_vo);
            size=list2.size();
            for(int id:list2){
                new Thread(()->{
                    fetchData(id);
                }).start();
            }
        }
        else {
            TextView textView = findViewById(R.id.textView);
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
                            c_1++;
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

        Gson gson = new Gson();
        ArrayList<String> arrayList = gson.fromJson(res, new TypeToken<ArrayList<String>>() {
        }.getType());
        //System.out.println("000000"+arrayList);
        size_for_l = arrayList.size();
        for (int i = 0; i < size_for_l; i += 4) {
            int x=Integer.parseInt(arrayList.get(i));
            ids.add(x);
            int index=ids.indexOf(x);
            product.add(index, arrayList.get(i + 1));
            price.add(index, arrayList.get(i + 2));
            time.add(index, arrayList.get(i + 3));
        }
        if(c_1==size){
            c_2=0;
            for (int i : ids) {
                int finalI = i;
                new Thread(() -> {
                    post(finalI);
                }).start();
            }
        }
    }

    private void post(int imageIndex) {
        c_2 = 0;
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
                            c_2++;
                            new OrderActivity.ImageSaveTask().execute(responseData, String.valueOf(imageIndex));
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
            if (c_2 == (size)) {
                start_();
            }

        }
    }

    public void start_() {
        setContentView(R.layout.menu_products);
        int c=0;

        datas.clear();//
        Data_of_user_product data_w_r_=new Data_of_user_product();
        data_w_r_.clear_arrays();//

        System.out.println("!!!!!!!");
        for (int i = 0; i < size; i++) {//size_for_l/4
            c++;
            datas.add(new Data(ids.get(i),product.get(i), price.get(i), time.get(i), kol_vo.get(i), getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/product" + ids.get(i) + ".jpg", this));
        }
        if (c!=0){
            RecyclerView recyclerView = findViewById(R.id.list);
            DataAdapter_for_korzina adapter = new DataAdapter_for_korzina(this, datas);
            recyclerView.setAdapter(adapter);
        }
        else{
            TextView textView = findViewById(R.id.textView);
            textView.setText("Корзина пуста!");
        }


    }

    public ArrayList<String> getARR(){
        return product_f;
    }
    public void change_delete_elem_ARR(String name){
        int index = product_f.indexOf(name);
        product_f.remove(index);
    }



}
