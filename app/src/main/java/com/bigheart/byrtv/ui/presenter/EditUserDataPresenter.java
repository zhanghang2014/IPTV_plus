package com.bigheart.byrtv.ui.presenter;

import android.content.Context;

import com.bigheart.byrtv.ui.view.activity.EditUserDataActivity;

/**
 * Created by InG on 15/12/10.
 */
public class EditUserDataPresenter extends Presenter {
    private Context context;
    private EditUserDataActivity editUserDataActivity;

    public EditUserDataPresenter(Context c,EditUserDataActivity view){
        context=c;
        editUserDataActivity=view;
    }

}
