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

    public void saveMainPage(String mainPage) {
        sp.setMainPage(mainPage);
    }

    public String getMainPage() {
        return sp.getMainPage();
    }

    public void saveChannelSort(String sortWay) {
        sp.setChannelSort(sortWay);
    }

    public String getChannelSort() {
        return sp.getChannelSort();
    }

    public void saveCloseDanMu(String closeDanMu) {
        sp.setCloseDanMu(closeDanMu);
    }

    public String getCloseDanMu() {
        return sp.getColseDanMu();
    }
}
