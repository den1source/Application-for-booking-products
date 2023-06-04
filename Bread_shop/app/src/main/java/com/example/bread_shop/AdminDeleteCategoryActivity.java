package com.example.bread_shop;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class AdminDeleteCategoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_delete_category);
        // Ваш массив данных
        String[] items = {"Элемент 1", "Элемент 2", "Элемент 3", "Элемент 4", "Элемент 5"};

// Найдите ListView в макете активности
        ListView listView = findViewById(R.id.listView);

// Создайте ArrayAdapter для привязки массива данных к ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);

// Присоедините ArrayAdapter к ListView
        listView.setAdapter(adapter);

// Установите слушателя кликов для ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Получите выбранный элемент из массива данных
                String selectedItem = items[position];

                // Отобразите сообщение с названием элемента
                Toast.makeText(getApplicationContext(), selectedItem, Toast.LENGTH_SHORT).show();
            }
        });

    }

    


}