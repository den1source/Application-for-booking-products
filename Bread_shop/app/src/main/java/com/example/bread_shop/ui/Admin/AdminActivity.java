package com.example.bread_shop.ui.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.bread_shop.R;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
    }

    public void add_new_category(View v){
        Intent i = new Intent(AdminActivity.this, AdminAddNewCategoryActivity.class);
        startActivityForResult(i, 0);
    }

    public void add_new_product(View v){
        Intent i = new Intent(AdminActivity.this, AdminAddNewProductActivity.class);
        startActivityForResult(i, 0);
    }

    public void delete_category(View v){
        Intent i = new Intent(AdminActivity.this, AdminDeleteCategoryActivity.class);
        startActivityForResult(i, 0);
    }

    public void delete_product(View v){
        Intent i = new Intent(AdminActivity.this, AdminDeleteProductActivity.class);
        startActivityForResult(i, 0);
    }

}