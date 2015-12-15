package com.bigheart.byrtv.ui.presenter;

import android.content.Context;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVMobilePhoneVerifyCallback;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.RequestEmailVerifyCallback;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SignUpCallback;
import com.avos.avoscloud.UpdatePasswordCallback;
import com.bigheart.byrtv.data.sharedpreferences.AccountPreferences;
import com.bigheart.byrtv.ui.view.activity.EditUserDataActivity;

/**
 * Created by InG on 15/12/10.
 */
public class EditUserDataPresenter extends Presenter {
    private Context context;
    private EditUserDataActivity editUserDataActivity;
    private AVUser currentUser;
    private AccountPreferences sp;

    public EditUserDataPresenter(Context c, EditUserDataActivity view) {
        context = c;
        editUserDataActivity = view;
        currentUser = AVUser.getCurrentUser();
        sp = new AccountPreferences(c);
    }

    public void updatePWD(String oldPWD, String newPWD, UpdatePasswordCallback callback) {
        if (currentUser != null) {
            currentUser.updatePasswordInBackground(oldPWD, newPWD, callback);
        }
    }

    public void saveLocalPWD(String pwd) {
        sp.setUserPsw(pwd);
    }

    public String getLocalPWD(){
        return sp.getUserPsw();
    }

    public void updatePhone(String phone, SaveCallback callback) {
        if (currentUser != null) {
            currentUser.setMobilePhoneNumber(phone);
            currentUser.saveInBackground(callback);
        }
    }

    public void verPhone(String phone, RequestMobileCodeCallback callback) {
        if (currentUser != null) {
            currentUser.requestMobilePhoneVerifyInBackground(phone, callback);
        }
    }

    public void verCode(String code, AVMobilePhoneVerifyCallback callback) {
        if (currentUser != null) {
            currentUser.verifyMobilePhoneInBackground(code, callback);
        }
    }

    public void updateUsername(String username, SaveCallback callback) {
        if (currentUser != null) {
            sp.setUserAccount(username);
            currentUser.setUsername(username);
            currentUser.saveInBackground(callback);
        }
    }

    public void updateNickname(String nickname,SaveCallback callback){
        if(currentUser!=null){
            currentUser.put("nickname",nickname);
            currentUser.saveInBackground(callback);
        }
    }

    public void updateEmail(String email, SaveCallback callback) {
        if (currentUser != null) {
            currentUser.setEmail(email);
            currentUser.saveInBackground(callback);
        }
    }

    public void verEmail(String email, RequestEmailVerifyCallback callback) {
        if (currentUser != null) {
            AVUser.requestEmailVerfiyInBackground(email, callback);
        }
    }
}
