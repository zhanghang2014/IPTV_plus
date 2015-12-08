package com.bigheart.byrtv.ui.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SignUpCallback;
import com.bigheart.byrtv.data.sharedpreferences.AccountPreferences;
import com.bigheart.byrtv.ui.view.MainActivityView;

import java.util.Calendar;

/**
 * Created by BigHeart on 15/12/8.
 */
public class MainActivityPresenter extends Presenter {
    private Context context;
    private MainActivityView mainActivityView;

    public MainActivityPresenter(Context c, MainActivityView view) {
        context = c;
        mainActivityView = view;
    }

    public void login() {
        final AccountPreferences accountSp = new AccountPreferences(context);
        if (!TextUtils.isEmpty(accountSp.getUserAccount()) && !TextUtils.isEmpty(accountSp.getUserPsw())) {
            AVUser.logInInBackground(accountSp.getUserAccount(), accountSp.getUserPsw(), new LogInCallback() {
                public void done(AVUser user, AVException e) {
                    mainActivityView.login(e);
                }
            });
        } else {        //账号未缓存，重新为其创建一个
            final AVUser user = new AVUser();
            final String strPsw = "f32@ds*@&dsa";
            user.setUsername("乔布斯" + Calendar.getInstance().getTimeInMillis());
            user.setPassword(strPsw);

//        Log.i("MainActivityPresenter", (AVUser.getCurrentUser() == null) + "");

            user.signUpInBackground(new SignUpCallback() {
                public void done(AVException e) {
                    mainActivityView.login(e);
                    if (e == null) {
                        accountSp.setUserAccount(AVUser.getCurrentUser().getUsername());
                        accountSp.setUserPsw(strPsw);
                    }
                }
            });
        }
    }

}
