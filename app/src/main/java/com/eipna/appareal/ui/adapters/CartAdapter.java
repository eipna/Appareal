package com.eipna.appareal.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eipna.appareal.R;
import com.eipna.appareal.data.CartItem;
import com.eipna.appareal.data.Database;
import com.eipna.appareal.data.Product;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Objects;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    Context context;
    ArrayList<CartItem> cartItems;
    Listener listener;
    Database database;

    public interface Listener {
        void onRemoveItem(int position);
    }

    public CartAdapter(Context context, Listener listener, ArrayList<CartItem> cartItems) {
        this.context = context;
        this.listener = listener;
        this.cartItems = cartItems;
        this.database = new Database(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_cart, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);

        Product product = database.getProduct(cartItem.getProductID());

        Bitmap bitmap = BitmapFactory.decodeByteArray(product.getImage(), 0, product.getImage().length);
        holder.cover.setImageBitmap(bitmap);

        holder.description.setText(product.getDescription());
        holder.price.setText("₱" + NumberFormat.getInstance().format(product.getPrice()));

        holder.label.setText(String.valueOf(cartItem.getQuantity()));

        holder.add.setOnClickListener(view -> {
            if (database.getProductStock(cartItem.getProductID()) == 0) {
                return;
            }

            database.decrementProductStock(product.getID(), cartItem.getID());
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            notifyItemChanged(position);
        });

        holder.deduct.setOnClickListener(view -> {
            if (cartItem.getQuantity() == 1) {
                return;
            }

            database.incrementProductStock(product.getID(), cartItem.getID());
            cartItem.setQuantity(cartItem.getQuantity() - 1);
            notifyItemChanged(position);
        });

        holder.remove.setOnClickListener(view -> listener.onRemoveItem(position));
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView cover;
        MaterialTextView description, price;
        MaterialButton add, deduct, label, remove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.product_image);
            description = itemView.findViewById(R.id.product_description);
            price = itemView.findViewById(R.id.product_price);

            add = itemView.findViewById(R.id.add_quantity);
            deduct = itemView.findViewById(R.id.deduct_quantity);
            label = itemView.findViewById(R.id.label_quantity);
            remove = itemView.findViewById(R.id.remove_from_cart);
        }
    }
}