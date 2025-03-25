package com.eipna.appareal.ui.fragments;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eipna.appareal.data.Database;
import com.eipna.appareal.data.Product;
import com.eipna.appareal.databinding.FragmentSMCatalogBinding;
import com.eipna.appareal.ui.activities.AddProductActivity;
import com.eipna.appareal.ui.adapters.CatalogAdapter;

import java.util.ArrayList;

public class SMCatalogFragment extends Fragment implements CatalogAdapter.Listener {

    private FragmentSMCatalogBinding binding;

    private Database database;
    private ArrayList<Product> products;
    private CatalogAdapter catalogAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSMCatalogBinding.inflate(getLayoutInflater());

        database = new Database(requireContext());
        products = new ArrayList<>(database.getProducts());
        catalogAdapter = new CatalogAdapter(requireContext(), this, products);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.emptyIndicator.setVisibility(products.isEmpty() ? View.VISIBLE : View.GONE);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(catalogAdapter);

        binding.addProduct.setOnClickListener(view1 -> addProductLauncher.launch(new Intent(requireContext(), AddProductActivity.class)));
    }

    private final ActivityResultLauncher<Intent> addProductLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    refreshList();
                }
            });

    @SuppressLint("NotifyDataSetChanged")
    private void refreshList() {
        products.clear();
        products.addAll(database.getProducts());
        binding.emptyIndicator.setVisibility(products.isEmpty() ? View.VISIBLE : View.GONE);
        catalogAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onProductClick(int position) {

    }
}