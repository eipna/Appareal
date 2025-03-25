package com.eipna.appareal.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eipna.appareal.R;
import com.eipna.appareal.data.Database;
import com.eipna.appareal.data.Order;
import com.google.android.material.textview.MaterialTextView;

import java.text.NumberFormat;
import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    Context context;
    ArrayList<Order> orders;
    Database database;
    Listener listener;

    public interface Listener {
        void onOrderClick(int position);
    }

    public OrderAdapter(Context context, Listener listener, ArrayList<Order> orders) {
        this.context = context;
        this.orders = orders;
        this.database = new Database(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_order, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.ViewHolder holder, int position) {
        Order order = orders.get(position);
        String[] user = database.getUserName(order.getUserID()).split(",");

        StringBuilder builder = new StringBuilder();
        for (String s : user) {
            builder.append(s).append(" ");
        }

        holder.user.setText(String.format("User: %s", builder));
        holder.items.setText(order.getItems());

        if (order.getStatus().equals("Shipment")) {
            holder.status.setText(String.format("Status: Shipment - %s", order.getDate()));
        } else {
            holder.status.setText(String.format("Status: %s", order.getStatus()));
        }

        holder.price.setText("₱" + NumberFormat.getInstance().format(order.getTotal()));
        holder.itemView.setOnClickListener(view -> listener.onOrderClick(position));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        MaterialTextView user, status, price, items;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.text_user);
            status = itemView.findViewById(R.id.text_status);
            price = itemView.findViewById(R.id.text_total);
            items = itemView.findViewById(R.id.text_items);
        }
    }
}