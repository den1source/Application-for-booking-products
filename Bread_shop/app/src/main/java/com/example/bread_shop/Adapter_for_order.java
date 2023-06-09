package com.example.bread_shop;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.bread_shop.ui.korzina.data_for_korzina;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Adapter_for_order extends RecyclerView.Adapter<Adapter_for_order.ViewHolder> {
    private final LayoutInflater inflater;
    private final List<data_for_korzina> datas;
    private ArrayList<Integer> selectProduct = new ArrayList<>();
    private ArrayList<Integer> selectKol_vo=new ArrayList<>();

    private FileHandler fileHandler=new FileHandler();

    Adapter_for_order(Context context, List<data_for_korzina> datas) {
        this.datas = datas;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public Adapter_for_order.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.items_products, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Adapter_for_order.ViewHolder holder, int position) {
        System.out.println("ВЫюранный"+ selectProduct);
        data_for_korzina data = datas.get(position);

        Picasso.get().load(new File(data.getPath())).into(holder.imageView);
        holder.nameView.setText(data.getName());
        holder.priceView.setText(data.getPrice() + " руб.");
        holder.timeView.setText(data.getTime() + " мин.");

        int quantity = data.getKol_vo();
        if (quantity > 0) {
            holder.kol_vo.setText(String.valueOf(quantity));
            holder.kol_vo.setVisibility(View.VISIBLE);
            holder.minusButton.setVisibility(View.VISIBLE);
            holder.checkButton.setVisibility(View.VISIBLE);
            holder.plusButton.setVisibility(View.VISIBLE);
            holder.button.setVisibility(View.GONE);
        } else {
            holder.kol_vo.setVisibility(View.GONE);
            holder.minusButton.setVisibility(View.GONE);
            holder.checkButton.setVisibility(View.GONE);
            holder.plusButton.setVisibility(View.GONE);
            holder.button.setVisibility(View.VISIBLE);
        }

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProduct.add(data.getId());
                selectKol_vo.add(data.getKol_vo());
                data.setKol_vo(1);
                notifyDataSetChanged();
                Toast.makeText(v.getContext(), "Button clicked for item: " + data.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newQuantity = quantity + 1;
                data.setKol_vo(newQuantity);
                if(selectProduct.size()>0 && selectProduct.contains(data.getId())){
                    int index=selectProduct.indexOf(data.getId());
                    selectKol_vo.set(index, newQuantity);
                }
                holder.kol_vo.setText(String.valueOf(newQuantity));
                holder.minusButton.setVisibility(View.VISIBLE);
                holder.checkButton.setVisibility(View.VISIBLE);
                notifyDataSetChanged();
            }
        });

        holder.minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 0) {
                    int newQuantity = quantity - 1;
                    data.setKol_vo(newQuantity);
                    holder.kol_vo.setText(String.valueOf(newQuantity));
                    if(selectProduct.size()>0 && selectProduct.contains(data.getId())){
                        int index=selectProduct.indexOf(data.getId());
                        selectKol_vo.set(index, newQuantity);
                    }



                    if (newQuantity == 0) {
                        holder.minusButton.setVisibility(View.GONE);
                        holder.checkButton.setVisibility(View.GONE);
                        int i=selectProduct.indexOf(data.getId());
                        selectProduct.remove(i);
                        selectKol_vo.remove(i);
                        //selectProduct.remove(data.getId());
                        //selectKol_vo.remove(data.getKol_vo());
                        FileHandler.saveData(v.getContext(), selectProduct, selectKol_vo);
                    }

                    notifyDataSetChanged();
                }
            }
        });

        holder.checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = data.getName();
                if (!selectProduct.contains(name)) {
                    selectProduct.add(data.getId());
                    selectKol_vo.add(data.getKol_vo());
                    FileHandler.saveData(v.getContext(), selectProduct, selectKol_vo);
                    Toast.makeText(v.getContext(), name + ", добавлен в корзину", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(v.getContext(), name + ", уже в корзине", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;
        final TextView nameView, priceView, timeView, kol_vo;
        final Button button, plusButton, minusButton, checkButton;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.image);
            nameView = view.findViewById(R.id.name);
            priceView = view.findViewById(R.id.price);
            timeView = view.findViewById(R.id.time);
            button = view.findViewById(R.id.button);
            plusButton = view.findViewById(R.id.plusButton);
            minusButton = view.findViewById(R.id.minusButton);
            checkButton = view.findViewById(R.id.okey_b);
            kol_vo = view.findViewById(R.id.quantityTextView);
        }
    }
}
