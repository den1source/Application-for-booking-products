package ru.samsung.appbooking;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView log, password, res;
    Button button;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button=findViewById(R.id.registr);
        log = findViewById(R.id.user);
        password = findViewById(R.id.password);
        res=findViewById(R.id.result_main_activity);

        View.OnClickListener listener = new View.OnClickListener() {//кнопка регистрация
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Registration.class);
                startActivityForResult(i, 0);
            }
        };
        button.setOnClickListener(listener);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
            {
                log.setText(data.getStringExtra("log"));
                password.setText(data.getStringExtra("password"));
                break;
            }
        }
    }


    public void enter_to_app(View view){
        button=findViewById(R.id.registr);
        log = findViewById(R.id.user);
        password = findViewById(R.id.password);
        res=findViewById(R.id.result_main_activity);
        if(log.length()==0 || password.length()==0){
            res.setText("Введите логин/пароль");
            res.setTextColor(Color.RED);
        }
        else {
            res.setText("ОК");//изменить!!!
            /*View.OnClickListener listener = new View.OnClickListener() {//кнопка регистрация
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MainActivity.this, Registration.class);
                    startActivityForResult(i, 0);
                }
            };
            button.setOnClickListener(listener);*/
        }
    }
}