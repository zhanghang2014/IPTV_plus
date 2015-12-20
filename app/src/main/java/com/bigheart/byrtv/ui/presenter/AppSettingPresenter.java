package com.bigheart.byrtv.ui.presenter;

import android.content.Context;
import android.widget.Toast;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.FunctionCallback;
import com.bigheart.byrtv.data.sharedpreferences.AppSettingPreferences;
import com.bigheart.byrtv.ui.view.activity.AppSettingActivity;
import com.bigheart.byrtv.util.ByrTvUtil;

import java.util.List;


/**
 * Created by InG on 15/12/14.
 */
public class AppSettingPresenter extends Presenter {

    private Context context;
    private AppSettingPreferences sp;
    private AppSettingActivity appSettingActivity;

    public AppSettingPresenter(Context context, AppSettingActivity appSettingActivity) {
        this.context = context;
        this.appSettingActivity = appSettingActivity;
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

    public void checkUpdate() {
        AVCloud.callFunctionInBackground("appUpdate", null, new FunctionCallback() {
            @Override
            public void done(Object o, AVException e) {
                if (e == null) {
                    if (String.valueOf(ByrTvUtil.getVersionCode(context)) != ((List) o).get(0)) {
                        appSettingActivity.showUpdateDialog(((List<String>) o).get(0), ((List<String>) o).get(1), ((List<String>) o).get(2));
                    }else {
                        Toast.makeText(context,"已是最新版",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
