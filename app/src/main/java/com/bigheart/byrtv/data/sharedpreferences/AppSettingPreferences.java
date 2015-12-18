package com.bigheart.byrtv.data.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by InG on 15/12/14.
 */
public class AppSettingPreferences {
    private final String PREFERENCE_NAME = "app_setting";
    private final String APP_SETTING_MAIN_PAGE = "app_setting_main_page";
    private final String APP_SETTING_CHANNEL_SORT = "app_setting_channel_sort";
    private final String APP_SETTING_CLOSE_DANMU = "app_setting_close_damnu";
    private SharedPreferences pref;

    public AppSettingPreferences(Context c) {
        pref = c.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public void setMainPage(String mainPage) {
        pref.edit().putString(APP_SETTING_MAIN_PAGE, mainPage).commit();
    }

    public String getMainPage() {
        return pref.getString(APP_SETTING_MAIN_PAGE, null);
    }

    public void setChannelSort(String sortWay) {
        pref.edit().putString(APP_SETTING_CHANNEL_SORT, sortWay).commit();
    }

    public String getChannelSort() {
        return pref.getString(APP_SETTING_CHANNEL_SORT, null);
    }

    public void setCloseDanMu(String closeDanMu) {
        pref.edit().putString(APP_SETTING_CLOSE_DANMU, closeDanMu).commit();
    }

    public String getColseDanMu() {
        return pref.getString(APP_SETTING_CLOSE_DANMU, null);
    }

    public void clear() {
        pref.edit().clear().commit();
    }
}
