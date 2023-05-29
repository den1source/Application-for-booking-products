package ru.samsung.appbooking;

import android.annotation.SuppressLint;
import android.os.Bundle;
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
            FrameLayout frameLayout = createImageWithButton(i);
            scrollLayout.addView(frameLayout);
        }

        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }


    private FrameLayout createImageWithButton(final int imageIndex) {
        FrameLayout frameLayout = new FrameLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, -50, 0, -360); // Устанавливаем отрицательный отступ снизу
        frameLayout.setLayoutParams(layoutParams);

        ImageView imageView = new ImageView(this);
        FrameLayout.LayoutParams imageLayoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(imageLayoutParams);
        // Здесь можно установить изображение для каждой картинки, например:
        imageView.setImageResource(R.drawable.image);
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
