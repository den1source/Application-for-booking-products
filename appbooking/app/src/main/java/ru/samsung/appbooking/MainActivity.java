package ru.samsung.appbooking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView log, password;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.enter);
        log = findViewById(R.id.user);
        password = findViewById(R.id.password);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.length() == 0 || log.length() == 0) {
                    Intent i = new Intent(MainActivity.this, Registration.class);
                    startActivityForResult(i, 0);
                } else {
                    //result.setText("Успех!");
                    //result.setTextColor(Color.GREEN);
                }
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
}