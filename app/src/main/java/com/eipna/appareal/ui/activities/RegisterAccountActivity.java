package com.eipna.appareal.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;

import com.eipna.appareal.databinding.ActivityRegisterAccountBinding;

import java.util.regex.Pattern;

public class RegisterAccountActivity extends BaseActivity {

    private ActivityRegisterAccountBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterAccountBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        binding.btnNext.setOnClickListener(view -> {
            String email = binding.fieldEmail.getText().toString();
            String password = binding.fieldPassword.getText().toString();
            String confirmPassword = binding.fieldConfirmPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill out all the fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 8 || confirmPassword.length() < 8) {
                Toast.makeText(this, "Password must be longer than 8 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords must match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidEmail(email)) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent nextIntent = new Intent(RegisterAccountActivity.this, RegisterInfoActivity.class);
            nextIntent.putExtra("email", email);
            nextIntent.putExtra("password", password);
            startActivity(nextIntent);
        });

        binding.btnBackLogin.setOnClickListener(view -> finish());
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern emailPattern = Pattern.compile(emailRegex);

        if (email == null || email.isEmpty()) {
            return false;
        }
        return emailPattern.matcher(email).matches();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}