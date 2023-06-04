package com.example.bread_shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Button joinButton, loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Objects.equals(UserDataManager.getSavedRole(MainActivity.this), "Users")) {
            Intent i = new Intent(MainActivity.this, HomeActivity.class);
            startActivityForResult(i, 0);
        }
        else if (Objects.equals(UserDataManager.getSavedRole(MainActivity.this), "Admins")) {
            Intent i = new Intent(MainActivity.this, AdminActivity.class);
            startActivityForResult(i, 0);
        }
        else {
            setContentView(R.layout.activity_main);

            joinButton = (Button) findViewById(R.id.main_join_btn);
            loginButton = (Button) findViewById(R.id.main_login_btn);

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                }
            });
            joinButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                    startActivity(registerIntent);
                }
            });
        }


    }


}