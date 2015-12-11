package com.bigheart.byrtv.ui.presenter;

import android.content.Context;

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.UpdatePasswordCallback;
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

    public String getNickname() {
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

}
