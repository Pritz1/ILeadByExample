package com.eis.ileadbyexample.Others;

import android.content.Context;
import android.content.SharedPreferences;

import com.eis.ileadbyexample.Models.User;

public class SharedPrefManager {

    private static final String SHARED_PREF_NAME = "my_shared_preff";

    private static SharedPrefManager mInstance;
    private Context mCtx;

    private SharedPrefManager(Context mCtx) {
        this.mCtx = mCtx;
    }


    public static synchronized SharedPrefManager getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(mCtx);
        }
        return mInstance;
    }


    public void saveUser(User user) {

        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("log_id", user.getLog_id());
        editor.putString("ecode", user.getEcode());
        editor.putString("mobile_no", user.getMobile_no());
        editor.putString("deldate", user.getDeldate());
        editor.putString("logged_in_status", user.getLogged_in_status());
        editor.putString("user_status", user.getUser_status());
        editor.putString("dbprefix", "xx");
        editor.apply();

    }

    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString("log_id", null) != null;
    }

    public User getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getString("log_id", null),
                sharedPreferences.getString("ecode", null),
                sharedPreferences.getString("mobile_no", null),
                sharedPreferences.getString("deldate", null),
                sharedPreferences.getString("logged_in_status", null),
                sharedPreferences.getString("user_status", null)
        );
    }

    public void clear() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

}
