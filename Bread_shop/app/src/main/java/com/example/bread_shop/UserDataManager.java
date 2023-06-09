package com.example.bread_shop;

import android.content.Context;
import android.content.SharedPreferences;

public class UserDataManager {
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_role = "role";
    private static final String KEY_phone = "phone";
    private static final String KEY_PASSWORD = "password";

    public static void saveUserData(Context context,String role, String phone, String password) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_role, role);
        editor.putString(KEY_phone, phone);
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }

    public static void saveUsername(Context context,String phone){
        SharedPreferences sharedPreferences = context.getSharedPreferences("name", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", phone);
        editor.apply();
    }

    public static String getUsername(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("name", Context.MODE_PRIVATE);
        return sharedPreferences.getString("name", "");
    }

    public static String getSavedRole(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_role, "");
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
        editor.remove(KEY_role);
        editor.remove(KEY_phone);
        editor.remove(KEY_PASSWORD);
        editor.apply();
    }
}
