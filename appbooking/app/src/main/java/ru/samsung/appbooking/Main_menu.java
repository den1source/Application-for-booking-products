package ru.samsung.appbooking;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

@SuppressLint("MissingInflatedId")
public class Main_menu extends AppCompatActivity {
    int size,c; // Здесь можно изменить значение `X` на желаемое количество картинок с кнопками

    public boolean checkImageFolder(String folderPath, int requiredImageCount) {
        File folder = new File(folderPath);

        // Проверяем, является ли путь директорией и существует ли директория
        if (folder.isDirectory() && folder.exists()) {
            // Создаем фильтр для файлов с расширением .jpg или .jpeg
            FilenameFilter imageFilter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    String lowercaseName = name.toLowerCase();
                    return lowercaseName.endsWith(".jpg") || lowercaseName.endsWith(".jpeg");
                }
            };

            // Получаем список файлов изображений, отфильтрованных по расширению
            File[] imageFiles = folder.listFiles(imageFilter);

            // Проверяем количество изображений
            if (imageFiles != null && imageFiles.length == requiredImageCount) {
                // Количество изображений совпадает с требуемым числом
                return true;
            }
        }

        // Количество изображений не совпадает с требуемым числом или директория не найдена
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://10.0.2.2:8080/auth").newBuilder();
        urlBuilder.addQueryParameter("what_do", "number_of_types");
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
                            setSize(responseData);
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

    public void setSize(String res){//получить кол-во товаров
        size=Integer.parseInt(res);
        if(checkImageFolder(String.valueOf(getExternalFilesDir(Environment.DIRECTORY_PICTURES)),size)) start();
        else {
            for(int i=0;i<size;i++){
                int finalI = i;
                new Thread(()->{
                    post_(finalI);
                }).start();

            }
        }
    }



    public void post_(int imageIndex){
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://10.0.2.2:8080/image").newBuilder();
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
                            saveImage(responseData, String.valueOf(imageIndex));
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

    private void saveImage(ResponseBody responseData, String num) {
        c++;
        String imagePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + num + ".jpg";
        File file = new File(imagePath);

        try (OutputStream fos = new FileOutputStream(file)) {
            // Сохраняем данные из ResponseBody в файл
            fos.write(responseData.bytes());
            fos.close();
            Toast.makeText(Main_menu.this, "Image saved successfully.", Toast.LENGTH_SHORT).show();



            if (c == size) {
                start();
            }
        } catch (IOException e) {
            Toast.makeText(Main_menu.this, "Failed to save image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }




    public void start() {

        ScrollView scrollView = findViewById(R.id.scrollView);
        LinearLayout scrollLayout = findViewById(R.id.scrollLayout);

        for (int i = 0; i < size; i++) {
            ImageView imageView = createImageView(i);
            scrollLayout.addView(imageView);
        }

        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

    private ImageView createImageView(final int imageIndex) {
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 16, 0, 16);
        imageView.setLayoutParams(layoutParams);

        String imagePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + imageIndex + ".jpg";
        Picasso.get().load(new File(imagePath)).into(imageView);

        imageView.setOnClickListener(view -> {
            Toast.makeText(Main_menu.this, "Нажата кнопка " + imageIndex, Toast.LENGTH_SHORT).show();
        });

        return imageView;
    }



}
