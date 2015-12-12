package com.bigheart.byrtv.ui.presenter;

import android.content.Context;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.RefreshCallback;
import com.avos.avoscloud.UpdatePasswordCallback;
import com.bigheart.byrtv.data.sharedpreferences.AccountPreferences;
import com.bigheart.byrtv.ui.view.UserSettingView;

/**
 * Created by InG on 15/12/8.
 */
public class UserSettingPresenter extends Presenter {

    private Context context;
    private UserSettingView userSettingView;
    private String nickname, gender, friend;
    private AVUser user;

    public UserSettingPresenter(Context c, UserSettingView view) {
        context = c;
        userSettingView = view;
        user = AVUser.getCurrentUser();
    }

    public String getUsername() {
        if (user != null) {
            nickname = user.getUsername();
        }
        return nickname;
    }

    public String getGender() {
        if (user != null) {
            gender = user.getString("gender");
        }
        return gender;
    }

    public String getFriend() {
        if (user != null) {
            friend = "" + user.getInt("friend");
        }
        return friend;
    }

    public void logout() {
        if (user != null) {
            user.logOut();
        }
    }

    public void isVerEmailed() {
//        AccountPreferences sp =new AccountPreferences(context);
//        final Boolean[] tmp = {false};
//        AVUser newUser = new AVUser();
//        newUser.logInInBackground(sp.getUserAccount(), sp.getUserPsw(), new LogInCallback<AVUser>() {
//            @Override
//            public void done(AVUser avUser, AVException e) {
//                if(e==null){
//                    tmp[0] =avUser.getBoolean("emailVerified");
//                }
//            }
//        });
//        return tmp[0];

       //TODO 返回邮箱验证状态
    }
}
