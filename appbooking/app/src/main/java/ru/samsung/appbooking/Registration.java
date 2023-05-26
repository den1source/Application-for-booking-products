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

    TextView name, year, log, password, password_first, result_out;
    Button button;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);
        name=findViewById(R.id.editTextName);
        year=findViewById(R.id.editTextNumber);
        button = findViewById(R.id.registration_butt);
        log = findViewById(R.id.reg_user);
        password = findViewById(R.id.reg_password);
        password_first=findViewById(R.id.password);

        result_out=findViewById(R.id.result_TEXT);

        View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (password.length() != 0 && log.length() != 0 && name.length()!=0 && year.length()!=0) {
                    if(password.equals(password_first)){
                        result_out.setText("");
                        Intent intent = new Intent(Registration.this, MainActivity.class);
                        intent.putExtra("password", password.getText().toString());
                        intent.putExtra("log", log.getText().toString());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                    else {
                        result_out.setText("Пароли не совпадают!");
                        result_out.setTextColor(Color.BLACK);
                    }
                } else {
                    result_out.setText("Заполните поля!");
                    result_out.setTextColor(Color.RED);
                }
            }
        };
        button.setOnClickListener(listener);
    }
}
