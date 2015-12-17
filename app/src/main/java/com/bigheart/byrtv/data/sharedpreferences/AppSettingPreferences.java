package com.bigheart.byrtv.data.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.bigheart.byrtv.util.ChannelSortType;


/**
 * Created by InG on 15/12/14.
 * <p/>
 * 更换为int类型
 */
public class AppSettingPreferences {
    private final String PREFERENCE_NAME = "app_setting";
    private final String APP_SETTING_MAIN_PAGE = "app_setting_main_page";
    private final String APP_SETTING_CHANNEL_SORT = "app_setting_channel_sort";
    private final String APP_SETTING_DANMU = "app_setting_damnu";
    private SharedPreferences pref;

    public AppSettingPreferences(Context c) {
        pref = c.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public void setMainPage(int mainPage) {
        pref.edit().putInt(APP_SETTING_MAIN_PAGE, mainPage).commit();
    }

    public int getMainPage() {
        return pref.getInt(APP_SETTING_MAIN_PAGE, -1);
    }

    public void setChannelSort(int sortWay) {
        pref.edit().putInt(APP_SETTING_CHANNEL_SORT, sortWay).commit();
    }

    public int getChannelSort() {
        return pref.getInt(APP_SETTING_CHANNEL_SORT, -1);
    }

    public void setDanMuSetting(int danMuSetting) {
        pref.edit().putInt(APP_SETTING_DANMU, danMuSetting).commit();
    }

    public int getDanMuSetting() {
        return pref.getInt(APP_SETTING_DANMU, -1);
    }

    public void clear() {
        pref.edit().clear().commit();
    }

    public ChannelSortType getAllChannelOrderType() {
        int index = getChannelSort();
        switch (index) {
            case AppSettingOption.SORT_PEOPLE:
                return ChannelSortType.SORT_BY_PEOPLE_NUM;
            case AppSettingOption.SORT_PINYIN:
                return ChannelSortType.SORT_BY_ALPHA;
            default:
                return ChannelSortType.SORT_BY_PEOPLE_NUM;
        }
    }
}
