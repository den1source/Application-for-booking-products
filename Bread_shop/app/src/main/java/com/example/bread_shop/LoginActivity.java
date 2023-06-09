package com.example.bread_shop;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.bread_shop.ui.home.UserHomeActivity;
import com.rey.material.widget.CheckBox;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private Button loginBtn;
    private EditText usernameInput, phoneInput, passwordInput;
    private ProgressDialog loadingBar;
    private TextView AdminLink, NotAdminLink, LoginPhone;

    private String parentDbName = "Users";
    private CheckBox checkBoxRememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginBtn = (Button) findViewById(R.id.login_btn);
        phoneInput = (EditText) findViewById(R.id.login_phone_input);
        passwordInput = (EditText) findViewById(R.id.login_password_input);
        loadingBar = new ProgressDialog(this);
        checkBoxRememberMe = (CheckBox) findViewById(R.id.login_checkbox);
        //Paper.init(this);

        AdminLink = (TextView) findViewById(R.id.admin_panel_link);
        NotAdminLink = (TextView) findViewById(R.id.not_admin_panel_link);

        if (UserDataManager.getSavedphone(LoginActivity.this) != "") {
            checkBoxRememberMe.setChecked(true);
            phoneInput.setText(UserDataManager.getSavedphone(LoginActivity.this));
            passwordInput.setText(UserDataManager.getSavedPassword(LoginActivity.this));
        } else {
            checkBoxRememberMe.setChecked(false);
        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminLink.setVisibility(View.INVISIBLE);
                NotAdminLink.setVisibility(View.VISIBLE);
                loginBtn.setText("Вход для админа");
                parentDbName = "Admins";
            }
        });

        NotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminLink.setVisibility(View.VISIBLE);
                NotAdminLink.setVisibility(View.INVISIBLE);
                loginBtn.setText("Войти");
                parentDbName = "Users";
            }
        });
    }

    private void loginUser() {
        String phone = phoneInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Введите номер", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Вход в приложение");
            loadingBar.setMessage("Пожалуйста, подождите...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidateUser(phone, password);
        }
    }

    private void ValidateUser(final String phone, final String password) {
        check_post_data(phone, password);

    }

    public void check_post_data(String phone, String password) {
        String v = "enter_chk";

        if (parentDbName.equals("Admins")) v = "admin_chk";

        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://10.0.2.2:8080/auth").newBuilder();
        urlBuilder.addQueryParameter("what_do", v);
        urlBuilder.addQueryParameter("phone", phone);
        urlBuilder.addQueryParameter("pass", password);
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
                                processResponseData(responseData, phone, password);
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

    public void processResponseData(String responseData, String phone, String password) throws InterruptedException {
        //System.out.println(responseData);
        switch (responseData) {
            case "ok":
                if (checkBoxRememberMe.isChecked()) {
                    UserDataManager.saveUserData(LoginActivity.this, parentDbName, phone, password);
                } else {
                    UserDataManager.clearUserData(LoginActivity.this);
                }

                loadingBar.dismiss();
                Toast.makeText(LoginActivity.this, "Успешный вход!", Toast.LENGTH_SHORT).show();

                if(parentDbName.equals("Admins")){
                    Intent i = new Intent(LoginActivity.this, AdminActivity.class);
                    startActivityForResult(i, 0);
                }
                else {
                    UserDataManager.saveUsername(LoginActivity.this, phone);
                    Intent i = new Intent(LoginActivity.this, UserHomeActivity.class);
                    startActivityForResult(i, 0);
                }
                break;
            case "ph_no":
                loadingBar.dismiss();
                Toast.makeText(LoginActivity.this, "Неверный пароль", Toast.LENGTH_SHORT).show();
                break;
            case "pass_no":
                loadingBar.dismiss();
                String v="Такого номера нет";
                if(parentDbName.equals("Admins")) v="Такого логина нет";
                Toast.makeText(LoginActivity.this, v, Toast.LENGTH_SHORT).show();
                break;
            default:
                loadingBar.dismiss();
                Toast.makeText(LoginActivity.this, "Ошибка на сервере(", Toast.LENGTH_SHORT).show();
                break;
        }
    }


}