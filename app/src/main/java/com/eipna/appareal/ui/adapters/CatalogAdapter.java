package com.eipna.appareal.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eipna.appareal.R;
import com.eipna.appareal.data.Product;
import com.google.android.material.textview.MaterialTextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CatalogAdapter extends RecyclerView.Adapter<CatalogAdapter.ViewHolder> {

    Context context;
    ArrayList<Product> products;
    Listener listener;

    public interface Listener {
        void onProductClick(int position);
    }

    public CatalogAdapter(Context context, Listener listener, ArrayList<Product> products) {
        this.context = context;
        this.products = products;
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void search(ArrayList<Product> searchedProducts) {
        products = searchedProducts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CatalogAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_catalog, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull CatalogAdapter.ViewHolder holder, int position) {
        Product product = products.get(position);

        Bitmap bitmap = BitmapFactory.decodeByteArray(product.getImage(), 0, product.getImage().length);
        holder.cover.setImageBitmap(bitmap);

        holder.price.setText("₱" + NumberFormat.getInstance().format(product.getPrice()));
        holder.description.setText(product.getDescription());

        holder.itemView.setOnClickListener(view -> listener.onProductClick(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        MaterialTextView price, description;
        ImageView cover;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            price = itemView.findViewById(R.id.product_price);
            description = itemView.findViewById(R.id.product_description);
            cover = itemView.findViewById(R.id.product_image);
        }
    }
}