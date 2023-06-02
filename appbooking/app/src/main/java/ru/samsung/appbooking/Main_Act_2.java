package ru.samsung.appbooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class Main_Act_2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_act_2);
    }

    public void open_menu(View v){
        Intent i = new Intent(Main_Act_2.this, Main_menu.class);
        startActivityForResult(i, 0);
    }

    public void open_korzina(View v){
        Intent i = new Intent(Main_Act_2.this, OrderActivity.class);
        startActivityForResult(i, 0);
    }

}
