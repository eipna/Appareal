package com.eipna.appareal.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.eipna.appareal.R;
import com.eipna.appareal.databinding.ActivityStoreManagerBinding;
import com.eipna.appareal.ui.fragments.SMCatalogFragment;
import com.eipna.appareal.ui.fragments.SMOrdersFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationBarView;

public class StoreManagerActivity extends BaseActivity {

    private ActivityStoreManagerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStoreManagerBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        binding.toolbar.setNavigationOnClickListener(view -> showLogoutDialog());

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.container_view, new SMCatalogFragment())
                .commit();

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            if (item.getItemId() == R.id.catalog) selectedFragment = new SMCatalogFragment();
            if (item.getItemId() == R.id.orders) selectedFragment = new SMOrdersFragment();

            assert selectedFragment != null;
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.container_view, selectedFragment)
                    .commit();
            return true;
        });
    }

    private void showLogoutDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle("Logout")
                .setMessage("This action will log out your account from the application")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Logout", (dialogInterface, i) -> {
                    Toast.makeText(StoreManagerActivity.this, "Logging out", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(StoreManagerActivity.this, LoginActivity.class));
                    finish();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}