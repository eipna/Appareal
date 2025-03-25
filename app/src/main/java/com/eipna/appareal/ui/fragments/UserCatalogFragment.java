package com.eipna.appareal.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

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
import com.eipna.appareal.databinding.FragmentUserCatalogBinding;
import com.eipna.appareal.ui.activities.ProductActivity;
import com.eipna.appareal.ui.adapters.CatalogAdapter;

import java.util.ArrayList;

public class UserCatalogFragment extends Fragment implements CatalogAdapter.Listener {

    private FragmentUserCatalogBinding binding;
    private Database database;
    private ArrayList<Product> products;
    private CatalogAdapter catalogAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserCatalogBinding.inflate(getLayoutInflater());

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
    }
    
    public void search(String query) {
        ArrayList<Product> newList = new ArrayList<>();
        for (Product product : products) {
            if (product.getDescription().toLowerCase().contains(query.toLowerCase())) {
                newList.add(product);
            }
        }
        catalogAdapter.search(newList);
    }

    @Override
    public void onProductClick(int position) {
        Product product = products.get(position);
        Intent intent = new Intent(requireContext(), ProductActivity.class);
        intent.putExtra("id", product.getID());
        intent.putExtra("description", product.getDescription());
        intent.putExtra("price", product.getPrice());
        intent.putExtra("stock", product.getStock());
        intent.putExtra("image", product.getImage());
        startActivity(intent);
    }
}