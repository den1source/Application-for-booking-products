package ru.samsung.appbooking;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private final LayoutInflater inflater;
    private final List<data_for_adapter> product;

    Adapter(Context context, List<data_for_adapter> product) {
        this.product = product;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Adapter.ViewHolder holder, int position) {
        data_for_adapter data_for_adapters = product.get(position);
        System.out.println("!!!!"+data_for_adapters.getName());
        //holder.View_image.setImageResource(R.drawable.image);
        Picasso.get().load(new File(data_for_adapters.getPath())).into(holder.View_image);
        holder.nameView.setText(data_for_adapters.getName());
        holder.priceView.setText(data_for_adapters.getPrice());
        holder.timeView.setText(data_for_adapters.getTime());
    }

    @Override
    public int getItemCount() {
        return product.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView View_image;
        final TextView nameView, priceView, timeView;

        ViewHolder(View view) {
            super(view);
            View_image = view.findViewById(R.id.image);
            nameView = view.findViewById(R.id.name);
            priceView = view.findViewById(R.id.price);
            timeView = view.findViewById(R.id.time);

        }
    }
}
