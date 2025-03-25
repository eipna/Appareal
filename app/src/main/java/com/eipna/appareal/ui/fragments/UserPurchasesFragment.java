package com.eipna.appareal.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eipna.appareal.R;
import com.eipna.appareal.data.Database;
import com.eipna.appareal.data.Order;
import com.eipna.appareal.databinding.FragmentUserPurchasesBinding;
import com.eipna.appareal.ui.adapters.OrderAdapter;
import com.eipna.appareal.util.PreferenceUtil;

import java.util.ArrayList;

public class UserPurchasesFragment extends Fragment implements OrderAdapter.Listener {

    private FragmentUserPurchasesBinding binding;
    private Database database;
    private ArrayList<Order> orders;
    private OrderAdapter orderAdapter;

    PreferenceUtil preferenceUtil;

    int userID = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserPurchasesBinding.inflate(getLayoutInflater());

        preferenceUtil = new PreferenceUtil(requireContext());
        userID = preferenceUtil.getLoggedIntUserID();

        database = new Database(requireContext());
        orders = new ArrayList<>(database.getUserOrders(userID));
        orderAdapter = new OrderAdapter(requireContext(), this, orders);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.emptyIndicator.setVisibility(orders.isEmpty() ? View.VISIBLE : View.GONE);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(orderAdapter);
    }

    @Override
    public void onOrderClick(int position) {

    }
}