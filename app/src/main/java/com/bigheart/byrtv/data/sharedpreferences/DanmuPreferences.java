package com.bigheart.byrtv.data.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by BigHeart on 15/12/13.
 */
public class DanmuPreferences {
    private final String PREFERENCE_NAME = "danmu_setting";
    private final String DANMU_COLOR_ET_POS = "danmu_color_et_pos", DANMU_TEXT_ET_SIZE = "danmu_text_et_size", DANMU_ET_POSITION = "danmu_et_position";
    private final String DANMU_TEXT_SIZE = "danmu_text_size";
    private SharedPreferences pref;

    public DanmuPreferences(Context c) {
        pref = c.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    //编辑框的属性

    public int getDanmuEtColorPos() {
        return pref.getInt(DANMU_COLOR_ET_POS, 0);
    }

    public void setDanmuColorEtPos(int danmuColorPos) {
        pref.edit().putInt(DANMU_COLOR_ET_POS, danmuColorPos).commit();
    }

    public int getDanmuEtTextSize() {
        return pref.getInt(DANMU_TEXT_ET_SIZE, 0);
    }

    public void setDanmuTextEtSize(int danmuTextSize) {
        pref.edit().putInt(DANMU_TEXT_ET_SIZE, danmuTextSize).commit();
    }

    public int getDanmuEtPos() {
        return pref.getInt(DANMU_ET_POSITION, 0);
    }

    public void setDanmuEtPos(int danmuPos) {
        pref.edit().putInt(DANMU_ET_POSITION, danmuPos).commit();
    }

    //屏蔽属性


    //用户设置弹幕的属性
    public int getDanmuTextSize() {
        return pref.getInt(DANMU_TEXT_SIZE, 0);
    }

    public void setDanmuTextSize(int danmuTextSize) {
        pref.edit().putInt(DANMU_TEXT_SIZE, danmuTextSize).commit();
    }
}
