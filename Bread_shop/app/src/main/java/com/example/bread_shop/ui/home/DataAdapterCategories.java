package com.example.bread_shop.ui.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.bread_shop.R;
import com.example.bread_shop.UserDataManager;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class DataAdapterCategories extends RecyclerView.Adapter<DataAdapterCategories.ViewHolder> {
    private final LayoutInflater inflater;
    private final List<Data> datas;
    private final Context context; // Добавить поле Context

    public DataAdapterCategories(Context context, List<Data> datas) {
        this.datas = datas;
        this.inflater = LayoutInflater.from(context);
        this.context = context; // Инициализировать поле Context
    }
    @Override
    public DataAdapterCategories.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataAdapterCategories.ViewHolder holder, int position) {
        Data data = datas.get(position);
        Picasso.get().load(new File(data.getPath())).into(holder.ImageView);
        holder.Button.setText(data.getName());

        holder.Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductUser.class);
                intent.putExtra("id", data.getId());
                context.startActivity(intent);
            }
        });
    }




    @Override
    public int getItemCount() {
        return datas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView ImageView;
        final Button Button;
        ViewHolder(View view){
            super(view);
            ImageView = view.findViewById(R.id.imageView);
            Button = view.findViewById(R.id.button_text);
        }
    }
}
