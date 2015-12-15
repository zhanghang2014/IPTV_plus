package com.bigheart.byrtv.ui.presenter;

import android.content.Context;

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.bigheart.byrtv.data.sharedpreferences.AccountPreferences;
import com.bigheart.byrtv.ui.view.activity.LoginActivity;

/**
 * Created by InG on 15/12/13.
 */
public class LoginActivityPresenter extends Presenter {

    private Context context;
    private LoginActivity loginActivity;

    public LoginActivityPresenter(Context c, LoginActivity activity) {
        context = c;
        loginActivity = activity;
    }

    public void mLogin(String username, String pwd, LogInCallback callback) {
        AVUser.logInInBackground(username, pwd, callback);
    }

    public void saveLocalUser(String username, String pwd) {
        AccountPreferences sp = new AccountPreferences(context);
        sp.setUserAccount(username);
        sp.setUserPsw(pwd);
    }
}
