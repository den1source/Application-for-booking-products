package ru.samsung.appbooking;

import android.content.Context;
import android.content.SharedPreferences;

public class UserDataManager {
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_LOGIN = "login";
    private static final String KEY_PASSWORD = "password";

    public static void saveUserData(Context context, String login, String password) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_LOGIN, login);
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }

    public static String getSavedLogin(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_LOGIN, "");
    }

    public static String getSavedPassword(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_PASSWORD, "");
    }

    public static void clearUserData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_LOGIN);
        editor.remove(KEY_PASSWORD);
        editor.apply();
    }
}
