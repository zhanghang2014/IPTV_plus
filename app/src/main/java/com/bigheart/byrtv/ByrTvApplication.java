package com.bigheart.byrtv;

import android.app.Application;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.avos.avoscloud.AVOSCloud;
import com.bigheart.byrtv.data.sqlite.SqlChannelManager;
import com.bigheart.byrtv.util.ByrTvUtil;

public class ByrTvApplication extends Application {
    public static boolean isLogin = false;

    @Override
    public void onCreate() {
        super.onCreate();
//        AVOSCloud.initialize(this, "inmr1XU9PooPJIdglS83npTj-gzGzoHsz", "fIcsp5SWywQd3h9c3yFeA0q2");
        AVOSCloud.initialize(this, "2jCxyCwdAGGvTmRxM509vOrk", "RcDFh4h0qVxA1m6y4DUmxKwC");
        SqlChannelManager.initChannelManager(getApplicationContext());
        ByrTvUtil.init(getApplicationContext());


    }
}
