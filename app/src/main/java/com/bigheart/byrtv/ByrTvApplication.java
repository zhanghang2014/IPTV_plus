package com.bigheart.byrtv;

import android.app.Application;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.bigheart.byrtv.data.sqlite.SqlChannelManager;
import com.bigheart.byrtv.util.ByrTvUtil;
import com.bigheart.byrtv.util.LogUtil;

public class ByrTvApplication extends Application {
    private static boolean isLogin = false, isTryPullChannelFromNet = false, isGetAVIMClient = false;
    public static AVIMClient avimClient;

    public enum updateWhat {
        upDateLoginState, updatePullChannelState, updateAVIMClientState;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AVOSCloud.initialize(this, "inmr1XU9PooPJIdglS83npTj-gzGzoHsz", "fIcsp5SWywQd3h9c3yFeA0q2");
//        AVOSCloud.initialize(this, "2jCxyCwdAGGvTmRxM509vOrk", "RcDFh4h0qVxA1m6y4DUmxKwC");
        SqlChannelManager.initChannelManager(getApplicationContext());
        ByrTvUtil.init(getApplicationContext());
        LogUtil.d("ByrTvApplication", "onCreate");
        //lancloud debug log
//        AVOSCloud.setDebugLogEnabled(true);
        AVAnalytics.enableCrashReport(this, true);
        //注册 弹幕 消息
//        AVIMMessageManager.registerAVIMMessageType(DanmuTextMessage.class);
    }


    public static boolean isLogin() {
        return isLogin;
    }

    public static boolean isTryPullChannelFromNet() {
        return isTryPullChannelFromNet;
    }

    public static boolean isGetAVIMClient() {
        return isGetAVIMClient;

    }

    public static void resetGetPeopleNumState() {

        isLogin = isTryPullChannelFromNet = isGetAVIMClient = false;
    }

    /**
     * 更新登录状态、拉取数据状态、聊天室 Client 状态
     *
     * @param isLogin2
     * @param isTryPullChannelFromNet2
     * @param isGetAVIMClient2
     * @param what
     */
    public static synchronized void updateReadGetPeopleNumState(boolean isLogin2, boolean isTryPullChannelFromNet2, boolean isGetAVIMClient2, updateWhat what) {
        switch (what) {
            case upDateLoginState:
                isLogin = isLogin2;
                break;
            case updatePullChannelState:
                isTryPullChannelFromNet = isTryPullChannelFromNet2;
                break;
            case updateAVIMClientState:
                isGetAVIMClient = isGetAVIMClient2;
                break;
            default:
                break;
        }
    }

    public static boolean isReadGetPeopleNum() {
        return isGetAVIMClient && isGetAVIMClient && isTryPullChannelFromNet;
    }

}
