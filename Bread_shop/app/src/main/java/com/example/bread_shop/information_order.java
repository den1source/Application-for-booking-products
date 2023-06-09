package com.example.bread_shop;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bread_shop.ui.account.order;
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

public class information_order extends AppCompatActivity {

    private long num_order;
    private ArrayList<String> text;
    private order order=new order();


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_order);
        System.out.println("DO!!");
        stt();


    }

    public void stt(){
        loadingBar = new ProgressDialog(this);


        loadingBar.setTitle("Получение информации");
        loadingBar.setMessage("Пожалуйста, подождите...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        getTexts();


    }



    private void getTexts() {//получ текст

        num_order=order.getId_or();
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://10.0.2.2:8080/order").newBuilder();
        urlBuilder.addQueryParameter("what_do", "pr_of_order");
        urlBuilder.addQueryParameter("id", String.valueOf(num_order));
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
        text = gson.fromJson(responseData, new TypeToken<ArrayList<String>>() {
        }.getType());

        post(num_order);
    }

    private void post(long imageIndex) {
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

    TextView text_;
    ImageView image;
    private ProgressDialog loadingBar;

    private void setimage(){
        setContentView(R.layout.info_order);
        text_=findViewById(R.id.text_);
        image=findViewById(R.id.qr_);
        String tt="";
        for (String i:text){
            tt+=(i+"\n");
        }

        text_.setText(tt);
        Picasso.get().load(new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/qr_code" + num_order + ".jpg")).into(image);

        FileHandler.deleteFile(information_order.this);
        loadingBar.dismiss();
    };

}
