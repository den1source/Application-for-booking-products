package ru.samsung.appbooking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    EditText log, password;
    TextView res1;
    Button button;
    Switch save;

    static String login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.registr);
        save = findViewById(R.id.switch1);
        log = findViewById(R.id.user);
        password = findViewById(R.id.password);
        res1 = findViewById(R.id.result_main_activity);

        if (UserDataManager.getSavedLogin(MainActivity.this) != "") {
            save.setChecked(true);
            log.setText(UserDataManager.getSavedLogin(MainActivity.this));
            password.setText(UserDataManager.getSavedPassword(MainActivity.this));
        } else {
            save.setChecked(false);
        }

        save.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && log != null && password != null) {
                    String login = log.getText().toString();
                    String password1 = password.getText().toString();
                    UserDataManager.saveUserData(MainActivity.this, login, password1);
                } else {
                    UserDataManager.clearUserData(MainActivity.this);
                }
            }
        });


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
            case RESULT_OK: {
                log.setText(data.getStringExtra("log"));
                password.setText(data.getStringExtra("password"));
                break;
            }
        }
    }

    public void enter_to_app(View v) throws SQLException, ClassNotFoundException, ExecutionException, InterruptedException {
        button = findViewById(R.id.enter_check);
        log = findViewById(R.id.user);
        password = findViewById(R.id.password);
        res1 = findViewById(R.id.result_main_activity);
        if (log.length() == 0 || password.length() == 0) {
            res1.setText("Введите логин/пароль");
            res1.setTextColor(Color.RED);
            res1.setText("");
            Intent i = new Intent(MainActivity.this, Main_Act_2.class);
            startActivityForResult(i, 0);
        } else {
            check_post_data(password.getText().toString(), log.getText().toString());
        }
    }



    public void check_post_data(String login, String password) {
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://10.0.2.2:8080/auth").newBuilder();
        urlBuilder.addQueryParameter("what_do", "enter_chk");
        urlBuilder.addQueryParameter("login", login);
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
                                processResponseData(responseData);
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

    public void processResponseData(String responseData) throws InterruptedException {
        if (responseData.equals("go")) {

            login=log.getText().toString();

            res1.setText("");
            Intent i = new Intent(MainActivity.this, Main_Act_2.class);
            startActivityForResult(i, 0);
        } else if (responseData.equals("no")) {
            res1.setText("Такого пользователя не существует");
        } else {
            res1.setText("Ошибка на стороне сервера(");
        }
    }

}