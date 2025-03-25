package com.eipna.appareal.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtil {

    private final SharedPreferences sharedPreferences;

    public PreferenceUtil(Context context) {
        this.sharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
    }

    public void setLoggedInUserID(int userID) {
        sharedPreferences.edit().putInt("logged_in_user_id", userID).apply();
    }

    public int getLoggedIntUserID() {
        return sharedPreferences.getInt("logged_in_user_id", -1);
    }
}