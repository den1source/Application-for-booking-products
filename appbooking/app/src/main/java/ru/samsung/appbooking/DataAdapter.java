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
import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private final LayoutInflater inflater;
    private final List<Data> datas;

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

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Data clickedData = datas.get(position);
                    Toast.makeText(v.getContext(), "Button clicked for item: " + clickedData.getName(), Toast.LENGTH_SHORT).show();
                    // Perform any other desired action with the clicked data
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
        final TextView nameView, priceView, timeView;
        final Button button;

        ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.image);
            nameView = view.findViewById(R.id.name);
            priceView = view.findViewById(R.id.price);
            timeView = view.findViewById(R.id.time);
            button = view.findViewById(R.id.button);
        }
    }
}
