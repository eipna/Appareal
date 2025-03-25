package com.eipna.appareal.ui.fragments;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.eipna.appareal.R;
import com.eipna.appareal.data.CartItem;
import com.eipna.appareal.data.Database;
import com.eipna.appareal.data.Order;
import com.eipna.appareal.data.Product;
import com.eipna.appareal.databinding.FragmentUserCartBinding;
import com.eipna.appareal.ui.adapters.CartAdapter;
import com.eipna.appareal.util.PreferenceUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class UserCartFragment extends Fragment implements CartAdapter.Listener {

    private FragmentUserCartBinding binding;
    private Database database;
    private PreferenceUtil preferenceUtil;
    private ArrayList<CartItem> cartItems;
    private CartAdapter cartAdapter;

    private int userID = -1;
    private long orderID = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserCartBinding.inflate(getLayoutInflater());

        preferenceUtil = new PreferenceUtil(requireContext());
        userID = preferenceUtil.getLoggedIntUserID();

        database = new Database(requireContext());
        cartItems = new ArrayList<>(database.getUserCart(userID));
        cartAdapter = new CartAdapter(requireContext(), this, cartItems);

        return binding.getRoot();
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.checkout.setVisibility(cartItems.isEmpty() ? View.GONE : View.VISIBLE);

        binding.emptyIndicator.setVisibility(cartItems.isEmpty() ? View.VISIBLE : View.GONE);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(cartAdapter);

        binding.checkout.setOnClickListener(view1 -> {
            View paymentView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_checkout, null, false);

            MaterialTextView totalText = paymentView.findViewById(R.id.total_text);
            MaterialAutoCompleteTextView paymentMethod = paymentView.findViewById(R.id.payment_method);

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Checkout")
                    .setView(paymentView)
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Checkout", null);

            AlertDialog dialog = builder.create();
            dialog.setOnShowListener(dialogInterface -> {
                double total = 0.0;
                StringBuilder items = new StringBuilder();

                ArrayList<CartItem> finalCart = new ArrayList<>(database.getUserCart(userID));

                for (CartItem cartItem : finalCart) {
                    total += database.getProductPrice(cartItem.getProductID()) * cartItem.getQuantity();
                    items.append("\n- ").append(database.getProductDescription(cartItem.getProductID())).append("\nQuantity: ").append(cartItem.getQuantity()).append("\n");
                }

                totalText.setText("You're total amount to be payed is ₱" + total);

                double finalTotal = total;
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view2 -> {
                    String modeOfPayment = paymentMethod.getText().toString();
                    if (modeOfPayment.isEmpty()) {
                        Toast.makeText(requireContext(), "please select a payment method", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    orderID = database.addOrder(userID, finalTotal, "Verified", modeOfPayment, "no date", items.toString());
                    database.clearUserCart(userID);

                    cartItems.clear();
                    binding.emptyIndicator.setVisibility(View.VISIBLE);
                    binding.checkout.setVisibility(View.GONE);
                    cartAdapter.notifyDataSetChanged();

                    dialog.dismiss();
                    showOrderReceipt();
                });
            });
            dialog.show();
        });
    }

    private void showOrderReceipt() {
        Order order = database.getOrder((int) orderID);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Order receipt")
                .setMessage("Total: " + order.getTotal() + "\n\nStatus: " + order.getStatus() + "\n\nMode of Payment: Cash on Delivery (COD)" + "\n\nItems: " + order.getItems())
                .setPositiveButton("Close", (dialogInterface, i) -> orderID = -1);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onRemoveItem(int position) {
        CartItem cartItem = cartItems.get(position);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Remove item")
                .setMessage("This will remove the selected item from the cart")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        database.removeProductToCart(cartItem.getID());
                        database.addProductStock(cartItem.getProductID(), cartItem.getQuantity());
                        cartItems.clear();
                        cartItems.addAll(database.getUserCart(userID));
                        binding.emptyIndicator.setVisibility(cartItems.isEmpty() ? View.VISIBLE : View.GONE);
                        cartAdapter.notifyDataSetChanged();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}