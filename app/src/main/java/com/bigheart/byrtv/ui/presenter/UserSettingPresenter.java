package com.bigheart.byrtv.ui.presenter;

import android.content.Context;

import com.avos.avoscloud.AVUser;
import com.bigheart.byrtv.ui.view.UserSettingView;

/**
 * Created by InG on 15/12/8.
 */
public class UserSettingPresenter extends Presenter {

    private Context context;
    private UserSettingView userSettingView;
    private String username, gender, friend,nickname;
    private AVUser user;

    public UserSettingPresenter(Context c, UserSettingView view) {
        context = c;
        userSettingView = view;
        user = AVUser.getCurrentUser();
    }

    public String getUsername() {
        if (user != null) {
            username = user.getUsername();
        }
        return username;
    }

    public String getNickname() {
        if (user != null) {
            username = user.getString("nickname");
        }
        return username;
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

    public boolean isVerEmailed() {
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
        boolean tmp = false;
        if(user!=null){
            tmp = user.getBoolean("emailVerified");
        }
        return tmp;
    }

    public AVUser getUser() {
        return user;
    }
}
