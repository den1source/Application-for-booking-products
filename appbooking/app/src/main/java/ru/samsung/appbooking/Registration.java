package ru.samsung.appbooking;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Registration extends AppCompatActivity {

    TextView log, password;
    Button button;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);
        button = findViewById(R.id.registration_butt);
        log = findViewById(R.id.reg_user);
        password = findViewById(R.id.reg_password);
        View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (password.length() != 0 && log.length() != 0) {
                    Intent intent = new Intent(Registration.this, MainActivity.class);
                    intent.putExtra("password", password.getText().toString());
                    intent.putExtra("log", log.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    //result.setText("Заполните поля!");
                    //result.setTextColor(Color.RED);
                }
            }
        };
        button.setOnClickListener(listener);
    }
}
