package com.eipna.appareal.ui.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.eipna.appareal.databinding.ActivityRegisterInfoBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class RegisterInfoActivity extends BaseActivity {

    private ActivityRegisterInfoBinding binding;

    private String email;
    private String password;

    private Uri validIDUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterInfoBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");

        binding.btnBack.setOnClickListener(view -> finish());

        binding.uploadValidId.setOnClickListener(view -> uploadValidID());

        binding.btnRegister.setOnClickListener(view -> {
            String firstName = binding.fieldFirstName.getText().toString();
            String middleInitial = binding.fieldMiddleInitial.getText().toString();
            String lastName = binding.fieldLastName.getText().toString();
            String contact = binding.fieldContactNumber.getText().toString();
            String address = binding.fieldAddress.getText().toString();
            String validID = binding.autoCompleteValidId.getText().toString();

            if (firstName.isEmpty() || lastName.isEmpty() || contact.isEmpty() || address.isEmpty() || validID.isEmpty()) {
                Toast.makeText(this, "Please fill out all your personal details", Toast.LENGTH_SHORT).show();
                return;
            }

            if (validIDUri == null) {
                binding.fieldValidId.setHelperText("Please upload your valid ID");
                return;
            }

            if (contact.length() != 11) {
                Toast.makeText(this, "Please enter a valid contact number", Toast.LENGTH_SHORT).show();
                return;
            }

            byte[] validIDImage = getBytesFromUri();
            String fullName = (middleInitial.isEmpty()) ? String.format("%s,%s", firstName, lastName) : String.format("%s,%s,%s", firstName, middleInitial, lastName);

            if (database.registerUser(email, password, fullName, address, contact, validID, validIDImage)) {
                Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterInfoActivity.this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Email already exists, please go back and change it", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadValidID() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        uploadValidIDLauncher.launch(intent);
    }

    private byte[] getBytesFromUri() {
        try {
            InputStream inputStream = getContentResolver().openInputStream(validIDUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.WEBP, 50, byteBuffer);
            return byteBuffer.toByteArray();
        } catch (IOException e) {
            Log.e("Register Info Activity", "Error while converting Uri to byte array", e);
        }
        return null;
    }

    private final ActivityResultLauncher<Intent> uploadValidIDLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        validIDUri = data.getData();
                        binding.fieldValidId.setHelperText(getFileFromUri(validIDUri));
                        binding.validIdPreview.setVisibility(View.VISIBLE);
                        binding.validIdPreview.setImageURI(validIDUri);
                    }
                }
            });

    private String getFileFromUri(Uri uri) {
        String fileName = null;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME);
            fileName = cursor.getString(nameIndex);
            cursor.close();
        }

        return fileName;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}