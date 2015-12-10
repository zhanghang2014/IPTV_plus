package com.bigheart.byrtv.data.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by BigHeart on 15/12/8.
 */

public class AccountPreferences {
    private final String PREFERENCE_NAME = "user_info";
    private final String USER_ACCOUNT = "user_account", USER_PAW = "user_psw";
    private SharedPreferences pref;

    public AccountPreferences(Context c) {
        pref = c.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public void setUserAccount(String account) {
        pref.edit().putString(USER_ACCOUNT, account).commit();
    }

    public String getUserAccount() {
        return pref.getString(USER_ACCOUNT, null);
    }

    public String getUserPsw() {
        return pref.getString(USER_PAW, null);
    }

    public void setUserPsw(String pas) {
        pref.edit().putString(USER_PAW, pas).commit();
    }

    public void clear() {
        pref.edit().clear().commit();
    }

}
