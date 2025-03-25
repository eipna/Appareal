package com.eipna.appareal.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.eipna.appareal.R;
import com.eipna.appareal.databinding.ActivityUserBinding;
import com.eipna.appareal.ui.fragments.UserCatalogFragment;
import com.eipna.appareal.ui.fragments.UserCartFragment;
import com.eipna.appareal.ui.fragments.UserPurchasesFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class UserActivity extends BaseActivity {

    private ActivityUserBinding binding;

    private boolean isInPurchases, isInCart, isInCatalog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.container_view, new UserCatalogFragment())
                    .commit();

        binding.toolbar.setNavigationOnClickListener(view -> showLogoutDialog());

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            if (item.getItemId() == R.id.catalog) {
                isInCatalog = true;
                isInPurchases = false;
                isInCart = false;

                selectedFragment = new UserCatalogFragment();
                binding.toolbar.setTitle("Catalog");
                invalidateOptionsMenu();
            }

            if (item.getItemId() == R.id.purchases) {
                isInCatalog = false;
                isInPurchases = true;
                isInCart = false;

                selectedFragment = new UserPurchasesFragment();
                binding.toolbar.setTitle("My Purchases");
                invalidateOptionsMenu();
            }

            if (item.getItemId() == R.id.cart) {
                isInCatalog = false;
                isInPurchases = false;
                isInCart = true;

                selectedFragment = new UserCartFragment();
                binding.toolbar.setTitle("My Cart");
                invalidateOptionsMenu();
            }

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
                    preferenceUtil.setLoggedInUserID(-1);
                    Toast.makeText(UserActivity.this, "Logging out", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UserActivity.this, LoginActivity.class));
                    finish();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isInCart) menu.findItem(R.id.search).setVisible(false);
        if (isInPurchases) menu.findItem(R.id.search).setVisible(false);
        if (isInCatalog) menu.findItem(R.id.search).setVisible(true);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_catalog, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        assert searchView != null;
        searchView.setQueryHint("Search products");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                UserCatalogFragment userCatalogFragment = (UserCatalogFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.container_view);
                if (userCatalogFragment != null) {
                    userCatalogFragment.search(newText);
                }
                return true;
            }
        });
        return true;
    }
}