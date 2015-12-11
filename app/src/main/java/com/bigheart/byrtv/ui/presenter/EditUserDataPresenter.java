package com.bigheart.byrtv.ui.presenter;

import android.content.Context;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.UpdatePasswordCallback;
import com.bigheart.byrtv.data.sharedpreferences.AccountPreferences;
import com.bigheart.byrtv.ui.view.activity.EditUserDataActivity;

/**
 * Created by InG on 15/12/10.
 */
public class EditUserDataPresenter extends Presenter {
    private Context context;
    private EditUserDataActivity editUserDataActivity;

    public EditUserDataPresenter(Context c, EditUserDataActivity view) {
        context = c;
        editUserDataActivity = view;
    }

    public void updatePWD(String oldPWD, String newPWD, UpdatePasswordCallback callback) {
        AVUser user = AVUser.getCurrentUser();
        user.updatePasswordInBackground(oldPWD, newPWD, callback);
    }
    public void saveLocalPWD(String pwd,Context c){
        AccountPreferences sp = new AccountPreferences(c);
        sp.setUserPsw(pwd);
    }
}
