package com.eipna.appareal.ui.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.eipna.appareal.data.Database;
import com.eipna.appareal.util.PreferenceUtil;

public abstract class BaseActivity extends AppCompatActivity {

    protected Database database;
    protected PreferenceUtil preferenceUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = new Database(this);
        preferenceUtil = new PreferenceUtil(this);
    }
}