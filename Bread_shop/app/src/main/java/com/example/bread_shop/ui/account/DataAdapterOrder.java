package com.example.bread_shop.ui.account;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.bread_shop.FileHandler;
import com.example.bread_shop.R;
import com.example.bread_shop.ui.home.ProductUser;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataAdapterOrder extends RecyclerView.Adapter<DataAdapterOrder.ViewHolder>{
    private final LayoutInflater inflater;
    private final ArrayList<Data_for_order> datas;
    private final Context context; // Добавить поле Context
    order order=new order();

    public DataAdapterOrder(Context context, ArrayList<Data_for_order> datas) {
        this.datas = datas;
        this.inflater = LayoutInflater.from(context);
        this.context = context; // Инициализировать поле Context
    }
    @Override
    public DataAdapterOrder.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_orders, parent, false);
        return new DataAdapterOrder.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataAdapterOrder.ViewHolder holder, int position) {
        Data_for_order data = datas.get(position);
        holder.name.setText(String.valueOf(data.getId()));
        holder.price.setText(String.valueOf(data.getSum()));




        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.example.bread_shop.ui.account.order.setId_or(data.getId());
                order informationOrderActivity = (order) context; // Cast the context to order activity
                informationOrderActivity.delete_order(); // Call the information() method
            }
        });

        holder.button_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.example.bread_shop.ui.account.order.setId_or(data.getId());
                order informationOrderActivity = (order) context; // Cast the context to order activity
                informationOrderActivity.information(); // Call the information() method
            }
        });



    }





    @Override
    public int getItemCount() {
        return datas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final android.widget.Button button_about, delete;
        final TextView name, price;
        ViewHolder(View view){
            super(view);
            button_about = view.findViewById(R.id.button_about);
            delete=view.findViewById(R.id.delete_order);

            name=view.findViewById(R.id.name);
            price=view.findViewById(R.id.price);
        }
    }
}
