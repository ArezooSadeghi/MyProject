package com.example.sipmobileapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SipMobileAppPreferences {

    private static final String USER_LOGIN_KEY = "userLoginKey";
    private static final String CENTER_NAME = "centerName";
    private static final String SICK_ID = "sickID";
    private static final String ATTACH_ID = "attachID";

    public static String getUserLoginKey(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getString(USER_LOGIN_KEY, null);
    }

    public static void setUserLoginKey(Context context, String userLoginKey) {
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putString(USER_LOGIN_KEY, userLoginKey).apply();
    }

    public static String getCenterName(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getString(CENTER_NAME, null);
    }

    public static void setCenterName(Context context, String centerName) {
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putString(CENTER_NAME, centerName).apply();
    }

    public static int getSickID(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getInt(SICK_ID, 0);
    }

    public static void setSickID(Context context, int sickID) {
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putInt(SICK_ID, sickID).apply();
    }

    public static int getAttachID(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getInt(ATTACH_ID, 0);
    }

    public static void setAttachID(Context context, int attachID) {
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putInt(ATTACH_ID, attachID).apply();
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(
                context.getPackageName(),
                Context.MODE_PRIVATE);
    }
}
