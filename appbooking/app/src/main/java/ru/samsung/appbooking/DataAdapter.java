package ru.samsung.appbooking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private final LayoutInflater inflater;
    private final List<Data> datas;
    ArrayList<String> name_prd=new ArrayList<>();
    Data_of_user_product data_w_r=new Data_of_user_product();


    DataAdapter(Context context, List<Data> datas) {
        this.datas = datas;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataAdapter.ViewHolder holder, int position) {
        Data data = datas.get(position);

        Picasso.get().load(new File(data.getPath())).into(holder.imageView);
        holder.nameView.setText(data.getName());
        holder.priceView.setText(data.getPrice() + " руб.");
        holder.timeView.setText(data.getTime() + " мин.");

        if (data.getQuantity() > 0) {
            holder.quantityTextView.setText(String.valueOf(data.getQuantity()));
            holder.quantityTextView.setVisibility(View.VISIBLE);
            holder.minusButton.setVisibility(View.VISIBLE);
            holder.checkButton.setVisibility(View.VISIBLE);
            holder.plusButton.setVisibility(View.VISIBLE);
            holder.button.setVisibility(View.GONE);
        } else {
            holder.quantityTextView.setVisibility(View.GONE);
            holder.minusButton.setVisibility(View.GONE);
            holder.checkButton.setVisibility(View.GONE);
            holder.plusButton.setVisibility(View.GONE);
            holder.button.setVisibility(View.VISIBLE);
        }

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    Data clickedData = datas.get(currentPosition);
                    //Toast.makeText(v.getContext(), "Button clicked for item: " + clickedData.getName(), Toast.LENGTH_SHORT).show();
                    clickedData.setQuantity(1);
                    notifyDataSetChanged();
                }
            }
        });

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
                        if(name_prd.size()!=0){
                            name_prd.remove(name_prd.indexOf(clickedData.getName()));
                            data_w_r.delete_product(clickedData.getName(), clickedData.getContext());
                        }
                    }
                    notifyDataSetChanged();
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
                    //int quantity = clickedData.getQuantity();
                    Toast.makeText(v.getContext(), name + ",добавлен в корзину", Toast.LENGTH_SHORT).show();
                    name_prd.add(clickedData.getName());
                    data_w_r.add_product(clickedData.getName(), clickedData.getId(), clickedData.getQuantity(), Double.parseDouble(clickedData.getPrice()),clickedData.getContext());
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
        final TextView nameView, priceView, timeView, quantityTextView;
        final Button button, plusButton, minusButton, checkButton;

        ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.image);
            nameView = view.findViewById(R.id.name);
            priceView = view.findViewById(R.id.price);
            timeView = view.findViewById(R.id.time);
            button = view.findViewById(R.id.button);
            plusButton = view.findViewById(R.id.plusButton);
            minusButton = view.findViewById(R.id.minusButton);
            checkButton = view.findViewById(R.id.checkButton);
            quantityTextView = view.findViewById(R.id.quantityTextView);
        }
    }
}
