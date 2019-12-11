package com.zain.deeplearning.recycl_ifier;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class shared_preferences {
    static final String PREF_EMAIL= null;
    public static String KEY_EMAIL = "email";
    public static String KEY_PASSWORD = "password";
    public static String KEY_LOGGEDIN = "isLogin";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUserName(Context ctx, String email_id)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_EMAIL, email_id);
        editor.commit();
    }

    public static String getUserName(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_EMAIL, "");
    }
//--------------------------------------------------------------------------------------------------
    public static boolean saveEmail(String email, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(KEY_EMAIL, email);
        prefsEditor.apply();
        return true;
    }

    public static String getEmail(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(KEY_EMAIL, null);
    }

    public static boolean loggedIn(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean(KEY_LOGGEDIN, true);
        prefsEditor.apply();
        return true;
    }
    public static boolean loggedOut(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean(KEY_LOGGEDIN, false);
        prefsEditor.apply();
        return true;
    }
    public static Boolean getLoginStatus(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(KEY_LOGGEDIN,false);
    }


//    public static boolean savePassword(String password, Context context) {
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//        SharedPreferences.Editor prefsEditor = prefs.edit();
//        prefsEditor.putString(KEY_PASSWORD, password);
//        prefsEditor.apply();
//        return true;
//    }
//    public static String getPassword(Context context) {
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//        return prefs.getString(KEY_PASSWORD, null);
//    }
}
