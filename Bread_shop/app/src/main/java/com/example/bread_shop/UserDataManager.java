package com.example.bread_shop;

import android.content.Context;
import android.content.SharedPreferences;

public class UserDataManager {
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_phone = "phone";
    private static final String KEY_PASSWORD = "password";

    public static void saveUserData(Context context, String phone, String password) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_phone, phone);
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }

    public static String getSavedphone(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_phone, "");
    }

    public static String getSavedPassword(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_PASSWORD, "");
    }

    public static void clearUserData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_phone);
        editor.remove(KEY_PASSWORD);
        editor.apply();
    }
}
