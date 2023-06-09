package com.example.bread_shop;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bread_shop.ui.home.UserHomeActivity;
import com.example.bread_shop.ui.korzina.Korzina;
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

public class all_info_about_order extends AppCompatActivity {

    long id;
    String user;
    int street;
    ArrayList<Integer> product_id;
    ArrayList<Double> sum;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadingBar = new ProgressDialog(this);
        loadingBar.setTitle("Бронирование...");
        loadingBar.setMessage("Пожалуйста, подождите...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        start();

    }

    public void start() {
        user = (UserDataManager.getUsername(all_info_about_order.this));
        street = make_oder.getid_street();

        ArrayList<Object> data = FileHandler.loadData(this);
        product_id = (ArrayList<Integer>) data.get(0);
        sum = Korzina.getPrice_some_products();

        post();
    }


    public void post(){
        String name_product_ids="";
        for (int i: product_id){
            name_product_ids+=(i+",");
        }
        String summ="";
        String kol="";
        for(double i:sum){
            summ+=(i+",");
            kol+=(3+",");
        }

        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://10.0.2.2:8080/order_make").newBuilder();
        urlBuilder.addQueryParameter("what_do", "make_order");//street
        urlBuilder.addQueryParameter("user", user);//sum
        urlBuilder.addQueryParameter("street", String.valueOf(street));//sum
        urlBuilder.addQueryParameter("name_product_ids", name_product_ids);//sum
        urlBuilder.addQueryParameter("sum", summ);//sum
        urlBuilder.addQueryParameter("kol", kol);//sum
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
                            get_res(responseData);

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

    private void get_res(String res){
        if(!res.equals("error")){
            post(Long.parseLong(res));
        }
        else {
            Intent i = new Intent(all_info_about_order.this, Korzina.class);
            startActivityForResult(i, 0);
        }
    }

    private void post(long imageIndex) {
        id=imageIndex;
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://10.0.2.2:8080/image").newBuilder();
        urlBuilder.addQueryParameter("what_do", "qr_code");
        urlBuilder.addQueryParameter("num", String.valueOf(imageIndex));
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

            String imagePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/qr_code" + num + ".jpg";
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
            setimage();
        }
    }

    TextView text;
    ImageView image;


    private void setimage(){
        setContentView(R.layout.all_info_about_order);
        text=findViewById(R.id.kol_vo_canc_zakakov);
        image=findViewById(R.id.imageView2);

        text.setText(String.valueOf(id));
        Picasso.get().load(new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/qr_code" + id + ".jpg")).into(image);

        FileHandler.deleteFile(all_info_about_order.this);
        loadingBar.dismiss();
    };

    public void nazad(View v){
        Intent i = new Intent(all_info_about_order.this, UserHomeActivity.class);
        startActivityForResult(i, 0);
    }


}
