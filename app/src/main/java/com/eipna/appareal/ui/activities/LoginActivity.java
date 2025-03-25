package com.eipna.appareal.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;

import com.eipna.appareal.databinding.ActivityLoginBinding;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        
        binding.btnRegister.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, RegisterAccountActivity.class)));

        binding.btnLogin.setOnClickListener(view -> {
            final String storeManagerUsername = "storemngr";
            final String storeManagerPassword = "St!apparel01";
            
            String email = binding.fieldEmail.getText().toString();
            String password = binding.fieldPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (binding.chkStoreManager.isChecked()) {
                if (email.equals(storeManagerUsername) && password.equals(storeManagerPassword)) {
                    Toast.makeText(this, "Logged in as store manager", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, StoreManagerActivity.class));
                    finish();
                    return;
                }
            }

            if (database.loginUser(email, password)) {
                int userID = database.getUserID(email, password);
                preferenceUtil.setLoggedInUserID(userID);

                Toast.makeText(this, "Login successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, UserActivity.class));
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}