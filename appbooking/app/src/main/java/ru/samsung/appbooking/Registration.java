package ru.samsung.appbooking;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Registration extends AppCompatActivity {
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
                    if(password.getText().toString().equals(password_first.getText().toString())){
                        String nameValue = name.getText().toString();
                        String lastNameValue = last_name.getText().toString();
                        String yearValue = (year.getText().toString());
                        String loginValue = log.getText().toString();
                        String passwordValue = password.getText().toString();

                        check_post_data(nameValue, lastNameValue, Integer.parseInt(yearValue), loginValue, passwordValue);
                    }
                    else {
                        result_out.setText("Пароли не совпадают!");
                        result_out.setTextColor(Color.RED);
                    }

                } else {
                    result_out.setText("Заполните поля!");
                    result_out.setTextColor(Color.RED);
                }
            }
        };
        button.setOnClickListener(listener);
    }

    public void check_post_data(String name, String last_name, int year, String login, String password) {
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://10.0.2.2:8080/auth").newBuilder();
        urlBuilder.addQueryParameter("what_do", "check_and_add_data");
        urlBuilder.addQueryParameter("lastname", last_name);
        urlBuilder.addQueryParameter("name", name);
        urlBuilder.addQueryParameter("year", String.valueOf(year));
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
                            processResponseData(responseData);
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

    public void processResponseData(String responseData) {
        if (responseData.equals("good")) {
            result_out.setText("");
            Toast.makeText(this, "Регистрация успешна!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Registration.this, MainActivity.class);
            intent.putExtra("password", password.getText().toString());
            intent.putExtra("log", log.getText().toString());
            setResult(RESULT_OK, intent);
            finish();
        } else if (responseData.equals("already")) {
            result_out.setText("Пользователь с данным логином уже существует");
        } else {
            result_out.setText("Ошибка на стороне сервера(");
        }
    }
}
