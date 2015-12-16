package com.bigheart.byrtv;

import android.app.Application;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.bigheart.byrtv.data.sqlite.SqlChannelManager;
import com.bigheart.byrtv.util.ByrTvUtil;

public class ByrTvApplication extends Application {
    private static boolean isLogin = false, isTryPullChannelFromNet = false;
    public static AVIMClient avimClient;

    public enum updateWhat {
        upDateLoginState, updatePullChannelState, updateLoginAndPullChannelState;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AVOSCloud.initialize(this, "inmr1XU9PooPJIdglS83npTj-gzGzoHsz", "fIcsp5SWywQd3h9c3yFeA0q2");
//        AVOSCloud.initialize(this, "2jCxyCwdAGGvTmRxM509vOrk", "RcDFh4h0qVxA1m6y4DUmxKwC");
        SqlChannelManager.initChannelManager(getApplicationContext());
        ByrTvUtil.init(getApplicationContext());
        //lancloud debug log
//        AVOSCloud.setDebugLogEnabled(true);
    }

    /**
     * 更新登录状态、拉取数据状态
     *
     * @param isLogin2
     * @param isTryPullChannelFromNet2
     * @param what
     */
    public static synchronized void updateLoginAndPullDataState(boolean isLogin2, boolean isTryPullChannelFromNet2, updateWhat what) {
        switch (what) {
            case upDateLoginState:
                isLogin = isLogin2;
                break;
            case updatePullChannelState:
                isTryPullChannelFromNet = isTryPullChannelFromNet2;
                break;
            case updateLoginAndPullChannelState:
                isLogin = isLogin2;
                isTryPullChannelFromNet = isTryPullChannelFromNet2;
                break;
            default:
                break;
        }
    }

    public static boolean isLoginAndTryPullChannelFromNet() {
        return isLogin && isTryPullChannelFromNet;
    }
}
