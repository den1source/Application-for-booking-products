package com.example.bread_shop;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity
{
    private Button registerBtn;
    private EditText usernameInput, phoneInput, passwordInput;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerBtn = (Button) findViewById(R.id.register_btn);
        usernameInput = (EditText) findViewById(R.id.register_username_input);
        phoneInput = (EditText) findViewById(R.id.register_phone_input);
        passwordInput = (EditText) findViewById(R.id.register_password_input);
        loadingBar = new ProgressDialog(this);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });
    }

    private void CreateAccount() {
        String username = usernameInput.getText().toString();
        String phone = phoneInput.getText().toString();
        String password = passwordInput.getText().toString();

        if(TextUtils.isEmpty(username))
        {
            Toast.makeText(this, "Введите имя", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(this, "Введите номер", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Создание аккаунта");
            loadingBar.setMessage("Пожалуйста, подождите...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();


        }

    }
}