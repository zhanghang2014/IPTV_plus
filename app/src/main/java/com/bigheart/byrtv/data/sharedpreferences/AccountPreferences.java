package com.bigheart.byrtv.data.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.bigheart.byrtv.util.ChannelSortType;

/**
 * Created by BigHeart on 15/12/8.
 */

public class AccountPreferences {
    private final String PREFERENCE_NAME = "user_info";
    private final String USER_ACCOUNT = "user_account", USER_PAW = "user_psw", ALL_CHANNEL_ORDER_TYPE = "all_channel_order_type";

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

    public void setAllChannelOrderType(ChannelSortType type) {
        pref.edit().putInt(ALL_CHANNEL_ORDER_TYPE, type.ordinal()).commit();
    }

    /*
    * 同目录下AppSettingPreferences可获取到用户设置的值
    * By InG
    * */
    public ChannelSortType getAllChannelOrderType() {
        int index = pref.getInt(ALL_CHANNEL_ORDER_TYPE, 0);
        switch (index) {
            case 0:
                return ChannelSortType.SORT_BY_PEOPLE_NUM;
            case 1:
                return ChannelSortType.SORT_BY_ALPHA;
            default:
                return ChannelSortType.SORT_BY_PEOPLE_NUM;
        }
    }

    public void clear() {
        pref.edit().clear().commit();
    }

}
