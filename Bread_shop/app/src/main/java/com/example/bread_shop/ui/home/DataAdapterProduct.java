package com.example.bread_shop.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.bread_shop.FileHandler;
import com.example.bread_shop.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataAdapterProduct extends RecyclerView.Adapter<DataAdapterProduct.ViewHolder> {
    private final LayoutInflater inflater;
    private final List<Data> datas;
    private ArrayList<Integer> selectProduct = new ArrayList<>();
    private ArrayList<Integer> selectKol_vo = new ArrayList<>();

    private FileHandler fileHandler = new FileHandler();

    DataAdapterProduct(Context context, List<Data> datas) {
        this.datas = datas;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public DataAdapterProduct.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.items_products, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataAdapterProduct.ViewHolder holder, int position) {
        Data data = datas.get(position);

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

                data.kol_plus();
                selectKol_vo.add(data.getKol_vo());
                selectProduct.add(data.getId());

                holder.kol_vo.setText(String.valueOf(data.getKol_vo()));

                holder.minusButton.setVisibility(View.VISIBLE);
                holder.plusButton.setVisibility(View.VISIBLE);
                holder.checkButton.setVisibility(View.VISIBLE);
                System.out.println("!!!!!!!!!!!!!!SELET_KOLVO__"+data.getKol_vo());
                FileHandler.saveData(v.getContext(), selectProduct, selectKol_vo);

                Toast.makeText(v.getContext(), "Добавлено в корзину: " + data.getName(), Toast.LENGTH_SHORT).show();

                notifyDataSetChanged();
            }
        });

        holder.plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.kol_plus();

                holder.kol_vo.setText(String.valueOf(data.getKol_vo()));
                holder.minusButton.setVisibility(View.VISIBLE);
                holder.checkButton.setVisibility(View.VISIBLE);
                notifyDataSetChanged();
            }
        });

        holder.minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.getKol_vo() > 0) {
                    data.kol_minus();
                    holder.kol_vo.setText(String.valueOf(data.getKol_vo()));

                    if (data.getKol_vo() == 0) {
                        holder.minusButton.setVisibility(View.GONE);
                        holder.checkButton.setVisibility(View.GONE);
                        int index = selectProduct.indexOf(data.getId());
                        selectProduct.remove(index);
                        selectKol_vo.remove(index);
                        System.out.println("productdelte1111"+selectKol_vo);
                        try {
                            FileHandler.delete_this_product(v.getContext(), selectProduct, selectKol_vo);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    notifyDataSetChanged();
                }
            }
        });

        holder.checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = data.getName();
                int index = selectProduct.indexOf(data.getId());

                if (index != -1) {
                    selectKol_vo.set(index, data.getKol_vo());
                } else {
                    selectProduct.add(data.getId());
                    selectKol_vo.add(data.getKol_vo());
                }

                FileHandler.saveData(v.getContext(), selectProduct, selectKol_vo);

                if (index != -1) {
                    Toast.makeText(v.getContext(), name + ", количество изменено", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(v.getContext(), name + ", добавлено в корзину", Toast.LENGTH_SHORT).show();
                }

                notifyDataSetChanged();
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
