package com.eipna.appareal.ui.fragments;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.eipna.appareal.R;
import com.eipna.appareal.data.Database;
import com.eipna.appareal.data.Order;
import com.eipna.appareal.databinding.FragmentSMOrdersBinding;
import com.eipna.appareal.ui.adapters.OrderAdapter;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;

public class SMOrdersFragment extends Fragment implements OrderAdapter.Listener {

    private FragmentSMOrdersBinding binding;
    private Database database;
    private ArrayList<Order> orders;
    private OrderAdapter orderAdapter;

    private String _estimatedDate = null;
    private String _status = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSMOrdersBinding.inflate(getLayoutInflater());

        database = new Database(requireContext());
        orders = new ArrayList<>(database.getAllOrders());
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onOrderClick(int position) {
        Order order = orders.get(position);

        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_order, null, false);

        MaterialAutoCompleteTextView autoCompleteStatus = view.findViewById(R.id.autoComplete_order_status);
        TextInputEditText estimatedDate = view.findViewById(R.id.field_date);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Edit order")
                .setView(view)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", null);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onShow(DialogInterface dialogInterface) {
                autoCompleteStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (i == 0 || i == 1) {
                            estimatedDate.setText("");
                        }
                    }
                });

                estimatedDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (autoCompleteStatus.getText().toString().equals("Shipment")) {
                            CalendarConstraints.Builder builder1 = new CalendarConstraints.Builder()
                                    .setValidator(DateValidatorPointForward.now());

                            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                                    .setTitleText("Select estimated date")
                                    .setCalendarConstraints(builder1.build())
                                    .build();

                            datePicker.addOnPositiveButtonClickListener(selection -> {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                                String date = dateFormat.format(new Date(selection));
                                estimatedDate.setText(date);
                                _estimatedDate = date;
                            });
                            datePicker.show(requireActivity().getSupportFragmentManager(), "DATE_PICKER");
                        } else {
                            Toast.makeText(requireContext(), "Status must be \"Shipment\" to add an estimated date", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view1 -> {
                    if (autoCompleteStatus.getText().toString().isEmpty()) {
                        Toast.makeText(requireContext(), "Please enter an order status", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (autoCompleteStatus.getText().toString().equals("Shipment") && estimatedDate.getText().toString().isEmpty()) {
                        Toast.makeText(requireContext(), "Please enter an estimated date for shipment", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    database.editUserOrder(order.getID(), _estimatedDate, autoCompleteStatus.getText().toString());
                    orders.clear();
                    orders.addAll(database.getAllOrders());
                    orderAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                });
            }
        });
        dialog.show();
    }
}