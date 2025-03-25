package com.eipna.appareal.ui.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.eipna.appareal.R;
import com.eipna.appareal.databinding.ActivityProductBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ProductActivity extends BaseActivity {

    private ActivityProductBinding binding;

    private int userID = -1;

    private int IDExtra;
    private String descriptionExtra;
    private double priceExtra;
    private int stockExtra;
    private byte[] imageExtra;

    private int quantity = 1;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        userID = preferenceUtil.getLoggedIntUserID();
        IDExtra = getIntent().getIntExtra("id", -1);
        descriptionExtra = getIntent().getStringExtra("description");
        priceExtra = getIntent().getDoubleExtra("price", -1);
        stockExtra = getIntent().getIntExtra("stock", -1);
        imageExtra = getIntent().getByteArrayExtra("image");

        assert imageExtra != null;
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageExtra, 0, imageExtra.length);
        binding.productImage.setImageBitmap(bitmap);

        binding.productDescription.setText(descriptionExtra);
        binding.productPrice.setText(String.format("%s%.2f", "₱", priceExtra));

        binding.productStock.setText(String.format("Stock: %s", stockExtra));

        if (stockExtra == 0) {
            binding.addQuantity.setEnabled(false);
            binding.deductQuantity.setEnabled(false);
            binding.labelQuantity.setEnabled(false);
            binding.labelQuantity.setText("0");
        }

        binding.addQuantity.setOnClickListener(view -> {
            if (quantity == stockExtra) {
                return;
            }
            quantity++;
            binding.labelQuantity.setText(String.valueOf(quantity));
        });

        binding.deductQuantity.setOnClickListener(view -> {
            if (quantity == 1) {
                return;
            }

            quantity--;
            binding.labelQuantity.setText(String.valueOf(quantity));
        });

        binding.addToCart.setOnClickListener(view -> {
            if (stockExtra == 0) {
                Toast.makeText(this, "Product is currently unavailable", Toast.LENGTH_SHORT).show();
                return;
            }
            
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(ProductActivity.this)
                    .setTitle("Add to cart")
                    .setMessage("This will add " + quantity + " items of this product to your cart")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Add", (dialogInterface, i) -> {
                        database.deductProductStock(IDExtra, quantity);
                        database.addProductToCart(userID, IDExtra, quantity);
                        finish();
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }
}