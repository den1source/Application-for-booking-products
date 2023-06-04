package com.example.bread_shop;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class AdminAddNewCategoryActivity extends AppCompatActivity {
    private EditText name_category;
    private Button add_image, post_new_category;
    private static final int REQUEST_SELECT_IMAGE = 1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_category);
        name_category=findViewById(R.id.name_cat);

    }

    public void get_image(View v){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_SELECT_IMAGE);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            // Выбрано изображение из галереи, теперь можно обработать его
            // Например, можно передать его в функцию загрузки по байтам, как описано ранее
            byte[] imageBytes = loadImageBytesFromUri(selectedImageUri);
            if (imageBytes != null) {
                // Делайте что-то с массивом байтов изображения
            } else {
                // Обработайте случай, когда изображение не удалось загрузить
            }
        }
    }

    private byte[] loadImageBytesFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            return outputStream.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void post_new_category(View v){
        String name=name_category.getText().toString();
        if(name.length()==0){
            Toast.makeText(AdminAddNewCategoryActivity.this, "Введите название категории", Toast.LENGTH_SHORT).show();
        }
        else {

        }
    }
}