    package com.example.bread_shop.ui.korzina;


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
    import java.util.ArrayList;
    import java.util.List;

    public class Adapter_for_korzina extends RecyclerView.Adapter<Adapter_for_korzina.ViewHolder> {
        private final LayoutInflater inflater;
        private final List<data_for_korzina> datas;
        private ArrayList<Integer> selectProduct;
        private ArrayList<Integer> selectKol_vo=new ArrayList<>();

        private FileHandler fileHandler=new FileHandler();

        Adapter_for_korzina(Context context, List<data_for_korzina> datas) {
            this.datas = datas;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public Adapter_for_korzina.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.items_products, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(Adapter_for_korzina.ViewHolder holder, int position) {

            data_for_korzina data = datas.get(position);

            Picasso.get().load(new File(data.getPath())).into(holder.imageView);
            holder.nameView.setText(data.getName());
            holder.priceView.setText(data.getPrice() + " руб.");
            holder.timeView.setText(data.getTime() + " мин.");

            int quantity = data.getKol_vo();
            holder.kol_vo.setText(String.valueOf(quantity));
            holder.kol_vo.setVisibility(View.VISIBLE);
            holder.minusButton.setVisibility(View.VISIBLE);
            holder.plusButton.setVisibility(View.VISIBLE);
            holder.button.setVisibility(View.GONE);
            holder.checkButton.setVisibility(View.VISIBLE);
            ArrayList<Object> arr=FileHandler.loadData(data.getContext());
            selectProduct= (ArrayList<Integer>) arr.get(0);
            holder.plusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int newQuantity = quantity + 1;
                    data.setKol_vo(newQuantity);

                    holder.kol_vo.setText(String.valueOf(newQuantity));

                    holder.minusButton.setVisibility(View.VISIBLE);
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

                        if (newQuantity == 0) {
                            holder.minusButton.setVisibility(View.GONE);
                            int i=selectProduct.indexOf(data.getId());
                            selectProduct.remove(i);
                            selectKol_vo.remove(i);
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
                    int index = selectProduct.indexOf(data.getId());
                    //System.out.println(selectProduct);
                    if (index != -1) {
                        //selectKol_vo.set(index, data.getKol_vo());
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
