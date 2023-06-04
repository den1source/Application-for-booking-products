package com.example.bread_shop;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AdminAddNewCategoryActivity extends AppCompatActivity {
    private EditText name_category;
    private Button add_image, post_new_category;
    private static final int REQUEST_SELECT_IMAGE = 1;
    private byte[] image;
    private ProgressDialog loadingBar;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_category);
        name_category=findViewById(R.id.name_cat);
        loadingBar = new ProgressDialog(this);
    }

    public void get_image(View v){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_SELECT_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            // Выбрано изображение из галереи, теперь можно обработать его
            // Например, можно передать его в функцию загрузки по байтам, как описано ранее
            byte[] imageBytes = loadImageBytesFromUri(selectedImageUri);
            if (imageBytes != null) {
                image=imageBytes;
            } else {
                // Обработайте случай, когда изображение не удалось загрузить
            }
        }
    }

    private byte[] loadImageBytesFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            return outputStream.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void post_new_category(View v){
        loadingBar.setTitle("Обработка");
        loadingBar.setMessage("Пожалуйста, подождите...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        String name=name_category.getText().toString();
        if(name.length()==0){
            loadingBar.dismiss();
            Toast.makeText(AdminAddNewCategoryActivity.this, "Введите название категории", Toast.LENGTH_SHORT).show();
        }
        else if(image.length==0){
            loadingBar.dismiss();
            Toast.makeText(AdminAddNewCategoryActivity.this, "Выберите изображение", Toast.LENGTH_SHORT).show();
        }
        else {
            check_name(name);
        }
    }

    public void check_name(String name){
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://10.0.2.2:8080/type").newBuilder();
        urlBuilder.addQueryParameter("what_do", "check_name");
        urlBuilder.addQueryParameter("name", name);
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
                                get_res(responseData, name);
                            } catch (IOException e) {
                                e.printStackTrace();
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

    public void get_res(String res, String name) throws IOException {

        if(res.equals("false")){

            uploadImage(image, name);
        }
        else {
            loadingBar.dismiss();
            Toast.makeText(AdminAddNewCategoryActivity.this, "Данное имя уже существует", Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadImage(byte[] imageBytes, String name) throws IOException {
        if(name!=null){
            OkHttpClient client = new OkHttpClient();

            String imageUrl = "http://10.0.2.2:8080/download_image";

            MediaType mediaType = MediaType.parse("application/octet-stream");
            RequestBody requestBody = RequestBody.create(mediaType, imageBytes);

            HttpUrl.Builder urlBuilder = HttpUrl.parse(imageUrl).newBuilder();
            urlBuilder.addQueryParameter("name", name); // Добавляем параметр "name"

            String url = urlBuilder.build().toString();

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .addHeader("Content-Type", "application/octet-stream")
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
                                res(responseData);
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
        else{
            loadingBar.dismiss();
            Toast.makeText(AdminAddNewCategoryActivity.this, "Нет имени", Toast.LENGTH_SHORT).show();
        }
    }

    private void res(String res){
        if(res.equals("true")) {
            name_category.setText("");
            image=null;
            loadingBar.dismiss();
            Toast.makeText(AdminAddNewCategoryActivity.this, "Успешно!", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.dismiss();
            Toast.makeText(AdminAddNewCategoryActivity.this, "Ошибка на сервере", Toast.LENGTH_SHORT).show();
        }
    }

}