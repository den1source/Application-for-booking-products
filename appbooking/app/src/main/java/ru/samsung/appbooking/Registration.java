package ru.samsung.appbooking;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class Registration extends AppCompatActivity {
    add_data_for_registration add_data = new add_data_for_registration();
    EditText name, last_name, year, log, password, password_first;
    TextView result_out;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);
        name = findViewById(R.id.editTextName);
        last_name = findViewById(R.id.editTextLastName);
        year = findViewById(R.id.editTextNumber);
        button = findViewById(R.id.registration_butt);
        log = findViewById(R.id.reg_user);
        password = findViewById(R.id.reg_password);
        password_first = findViewById(R.id.reg_password_enter);

        result_out = findViewById(R.id.result_TEXT);

        View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (password.length() != 0 && log.length() != 0 && last_name.length() != 0 && name.length() != 0 && year.length() != 0) {
                    if (password.getText().toString().equals(password_first.getText().toString())) {
                        if (!add_data.contains_login_in_BD(log.getText().toString())) {
                            add_data.add_data(name.getText().toString(), last_name.getText().toString(), Integer.parseInt(year.getText().toString()), log.getText().toString(), password.getText().toString());
                            result_out.setText("");
                            Intent intent = new Intent(Registration.this, MainActivity.class);
                            intent.putExtra("password", password.getText().toString());
                            intent.putExtra("log", log.getText().toString());
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            result_out.setText("Логин '" + log.getText().toString() + "' уже занят");
                            result_out.setTextColor(Color.RED);
                        }
                    } else {
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
