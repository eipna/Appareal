package com.eipna.appareal.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.eipna.appareal.R;
import com.eipna.appareal.databinding.ActivityAddProductBinding;
import com.google.android.material.shape.MaterialShapeDrawable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AddProductActivity extends BaseActivity {

    private ActivityAddProductBinding binding;

    private Uri productImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddProductBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Drawable drawable = MaterialShapeDrawable.createWithElevationOverlay(this);
        binding.appBar.setStatusBarForeground(drawable);

        binding.productImage.setOnClickListener(view -> uploadImage());

        binding.addProduct.setOnClickListener(view -> {
            if (binding.fieldDescription.getText().toString().isEmpty() || binding.fieldPrice.getText().toString().isEmpty() || binding.fieldStock.getText().toString().isEmpty() || productImageUri == null) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String description = binding.fieldDescription.getText().toString();
            double price = Double.parseDouble(binding.fieldPrice.getText().toString());
            int stock = Integer.parseInt(binding.fieldStock.getText().toString());
            byte[] image = getBytesFromUri();

            database.addProduct(description, price, stock, image);
            setResult(RESULT_OK);
            finish();
        });
    }

    private byte[] getBytesFromUri() {
        try {
            InputStream inputStream = getContentResolver().openInputStream(productImageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.WEBP, 50, byteBuffer);
            return byteBuffer.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private void uploadImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        uploadImageLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> uploadImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        productImageUri = data.getData();
                        binding.productImage.setImageURI(productImageUri);
                    }
                }
            });
}