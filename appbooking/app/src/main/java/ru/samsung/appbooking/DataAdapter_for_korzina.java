package ru.samsung.appbooking;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class DataAdapter_for_korzina extends RecyclerView.Adapter<DataAdapter_for_korzina.ViewHolder> {
    private final LayoutInflater inflater;
    private final List<Data> datas;
    ArrayList<String> name_prd = new ArrayList<>();
    Data_of_user_product data_w_r=new Data_of_user_product();


    DataAdapter_for_korzina(Context context, List<Data> datas) {
        this.datas = datas;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public DataAdapter_for_korzina.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_for_korzina, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataAdapter_for_korzina.ViewHolder holder, int position) {
        Data data = datas.get(position);
        Picasso.get().load(new File(data.getPath())).into(holder.imageView);
        holder.nameView.setText(data.getName());
        holder.priceView.setText(data.getPrice()*data.getQuantity() + " руб.");
        holder.timeView.setText(data.getTime() + " мин.");
        holder.quantityTextView.setText(String.valueOf(data.getQuantity()));

        holder.quantityTextView.setVisibility(View.VISIBLE);
        holder.minusButton.setVisibility(View.VISIBLE);
        holder.checkButton.setVisibility(View.VISIBLE);
        holder.plusButton.setVisibility(View.VISIBLE);

        if (data.getQuantity() > 0) {
            holder.quantityTextView.setText(String.valueOf(data.getQuantity()));
        } else {
            holder.quantityTextView.setVisibility(View.GONE);
            holder.minusButton.setVisibility(View.GONE);
            holder.checkButton.setVisibility(View.GONE);
            holder.plusButton.setVisibility(View.GONE);
            holder.text_delete.setTextColor(Color.RED);
            holder.text_delete.setText("ТОВАР УДАЛЕН");
        }


        holder.plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    Data clickedData = datas.get(currentPosition);
                    int quantity = clickedData.getQuantity();
                    quantity++;
                    clickedData.setQuantity(quantity);
                    holder.quantityTextView.setText(String.valueOf(quantity));
                    holder.minusButton.setVisibility(View.VISIBLE);
                    holder.checkButton.setVisibility(View.VISIBLE);
                    data_w_r.change(clickedData.getName(), clickedData.getQuantity(), clickedData.getPrice(), v.getContext());
                    notifyDataSetChanged();
                }
            }
        });

        holder.minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    Data clickedData = datas.get(currentPosition);
                    int quantity = clickedData.getQuantity();
                    quantity--;
                    clickedData.setQuantity(quantity);
                    holder.quantityTextView.setText(String.valueOf(quantity));

                    if (quantity == 0) {
                        holder.minusButton.setVisibility(View.GONE);
                        holder.checkButton.setVisibility(View.GONE);
                        name_prd.remove(clickedData.getName());
                        data_w_r.change_delete_product(clickedData.getName(), v.getContext());
                        notifyDataSetChanged();
                    }
                }
            }
        });

        holder.checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    Data clickedData = datas.get(currentPosition);
                    String name = clickedData.getName();
                    Toast.makeText(v.getContext(), name + " добавлен в корзину", Toast.LENGTH_SHORT).show();
                    name_prd.add(clickedData.getName());
                    notifyDataSetChanged();
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
        final TextView nameView, priceView, timeView, quantityTextView, text_delete;
        final Button plusButton, minusButton, checkButton;

        ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.image);
            nameView = view.findViewById(R.id.name);
            priceView = view.findViewById(R.id.price);
            timeView = view.findViewById(R.id.time);
            plusButton = view.findViewById(R.id.plusButton);
            minusButton = view.findViewById(R.id.minusButton);
            checkButton = view.findViewById(R.id.checkButton);
            quantityTextView = view.findViewById(R.id.quantityTextView);
            text_delete = view.findViewById(R.id.text_delete);
        }
    }
}
