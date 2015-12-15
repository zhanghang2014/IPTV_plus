package com.bigheart.byrtv.ui.presenter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SaveCallback;
import com.bigheart.byrtv.data.sharedpreferences.AccountPreferences;
import com.bigheart.byrtv.ui.view.UserSettingView;

/**
 * Created by InG on 15/12/8.
 */
public class UserSettingPresenter extends Presenter {

    private Context context;
    private UserSettingView userSettingView;
    private String username, gender, friend, nickname;
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

    public void updateGender(String gender,SaveCallback callback) {
        if (user!=null){
            user.put("gender",gender);
            user.saveInBackground(callback);
        }
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
        if (user != null) {
            tmp = user.getBoolean("emailVerified");
        }
        return tmp;
    }

    public AVUser getUser() {
        return user;
    }

    //剪裁图片
    public Intent startImageZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        return intent;
    }

    public void updateIcon(byte[] bytes,SaveCallback callback) {
        AVFile file=new AVFile(user.getUsername()+".png",bytes);
        user.put("avator", file);
        user.saveInBackground(callback);
    }

    public Uri getIconUri(){
        Uri uri=null;
        if (user!=null){
            AVFile file = user.getAVFile("avator");
            if(file!=null){
                uri = Uri.parse(file.getUrl());
            }
        }
        return uri;
    }

    public void reLogin(LogInCallback callback){
        AccountPreferences sp =new AccountPreferences(context);
        AVUser.logInInBackground(sp.getUserAccount(),sp.getUserPsw(),callback);
    }
}
