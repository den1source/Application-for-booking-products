package ru.samsung.appbooking;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@SuppressLint("MissingInflatedId")
public class Main_menu extends AppCompatActivity {
    static int X = 10; // Здесь можно изменить значение `X` на желаемое количество картинок с кнопками
    MainActivity mainActivity = new MainActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        X = MainActivity.c;
        setContentView(R.layout.activity_main_menu);

        LinearLayout layout = findViewById(R.id.layout);
        ScrollView scrollView = findViewById(R.id.scrollView);
        LinearLayout scrollLayout = findViewById(R.id.scrollLayout);

        for (int i = 0; i < X; i++) {
            int finalI = i;
            new Thread(()->{
                FrameLayout frameLayout = createImageWithButton(finalI);
                scrollLayout.addView(frameLayout);
            }).start();
        }

        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }


    private FrameLayout createImageWithButton(final int imageIndex) {
        OkHttpClient client = new OkHttpClient();
        String imageUrl = "http://10.0.2.2:8080/download"; // Замените на URL-адрес вашего изображения

        Request request = new Request.Builder()
                .url(imageUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                InputStream inputStream = response.body().byteStream();
                String imagePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/"+imageIndex+".jpg";
                try (FileOutputStream outputStream = new FileOutputStream(imagePath)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    System.out.println("Image saved successfully.");
                } catch (IOException e) {
                    System.out.println("Failed to save image: " + e.getMessage());
                } finally {
                    inputStream.close();
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });


        FrameLayout frameLayout = new FrameLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, -50, 0, -360); // Устанавливаем отрицательный отступ снизу
        frameLayout.setLayoutParams(layoutParams);

        ImageView imageView = new ImageView(this);
        FrameLayout.LayoutParams imageLayoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(imageLayoutParams);

        String imagePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + imageIndex + ".jpg";
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        imageView.setImageBitmap(bitmap);

        frameLayout.addView(imageView);


        Button button = new Button(this);
        FrameLayout.LayoutParams buttonLayoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonLayoutParams.gravity = Gravity.CENTER;
        button.setLayoutParams(buttonLayoutParams);
        button.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Main_menu.this, "Нажата кнопка " + imageIndex, Toast.LENGTH_SHORT).show();
            }
        });
        frameLayout.addView(button);

        return frameLayout;
    }

}
