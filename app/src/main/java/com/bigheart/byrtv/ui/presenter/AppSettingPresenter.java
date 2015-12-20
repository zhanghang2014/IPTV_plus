package com.bigheart.byrtv.ui.presenter;

import android.content.Context;

import com.bigheart.byrtv.data.sharedpreferences.AppSettingPreferences;


/**
 * Created by InG on 15/12/14.
 */
public class AppSettingPresenter extends Presenter {

    private Context context;
    private AppSettingPreferences sp;

    public AppSettingPresenter(Context context) {
        this.context = context;
        sp = new AppSettingPreferences(context);
    }

    public void saveMainPage(int mainPage) {
        sp.setMainPage(mainPage);
    }

    public int getMainPage() {
        return sp.getMainPage();
    }

    public void saveChannelSort(int sortWay) {
        sp.setChannelSort(sortWay);
    }

    public int getChannelSort() {
        return sp.getChannelSort();
    }

    public void saveDanMuSetting(int DanMuSetting) {
        sp.setDanMuSetting(DanMuSetting);
    }

    public int getDanMuSetting() {
        return sp.getDanMuSetting();
    }

//    public void
}
