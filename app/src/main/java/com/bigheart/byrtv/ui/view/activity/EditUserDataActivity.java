package com.bigheart.byrtv.ui.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.UpdatePasswordCallback;
import com.bigheart.byrtv.R;
import com.bigheart.byrtv.data.sharedpreferences.AccountPreferences;
import com.bigheart.byrtv.ui.presenter.EditUserDataPresenter;
import com.bigheart.byrtv.ui.view.EditUserDataView;

import static com.bigheart.byrtv.R.id.til_edit_user_new_pwd;

/**
 * Created by InG on 15/12/10.
 */
public class EditUserDataActivity extends BaseActivity implements EditUserDataView, View.OnClickListener {

    private Toolbar toolbar;
    private EditUserDataPresenter presenter;
    private TextInputLayout tilNickname, tilOldPWD, tilNewPWD;
    private EditText etNickname, etOldPWD, etNewPWD;
    private Button btnCommit, btnGiveUp,btnUpdateData;
    private LinearLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_data);
        initUI();
        initData();

    }

    public void initUI() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("资料修改");
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rootLayout = (LinearLayout) findViewById(R.id.root_layout_activity_edit_user_data);
        tilNickname = (TextInputLayout) findViewById(R.id.til_edit_user_nickname);
        tilOldPWD = (TextInputLayout) findViewById(R.id.til_edit_user_old_pwd);
        tilNewPWD = (TextInputLayout) findViewById(til_edit_user_new_pwd);

        btnCommit = (Button) findViewById(R.id.btn_edit_user_data_pwd);
        btnGiveUp = (Button) findViewById(R.id.btn_edit_user_data_give_up);

        btnCommit.setOnClickListener(this);
        btnGiveUp.setOnClickListener(this);

        etNickname = (EditText) findViewById(R.id.et_edit_user_nickname);
        etOldPWD = (EditText) findViewById(R.id.et_edit_user_old_pwd);
        etNewPWD = (EditText) findViewById(R.id.et_edit_user_new_pwd);
    }

    public void initData() {
        presenter = new EditUserDataPresenter(this, this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_edit_user_data_pwd:
                updatePWD();
                break;

            default:
                break;
        }
    }

    @Override
    public void clearPWD() {
        etOldPWD.setText("");
        etNewPWD.setText("");
    }

    @Override
    public void updatePWD() {
        final String oldPWD = etOldPWD.getText().toString().trim();
        final String newPWD = etNewPWD.getText().toString().trim();

        if (oldPWD.equals("") || oldPWD == null || newPWD.equals("") || newPWD == null) {
            Snackbar.make(rootLayout, "原始密码或新密码不能为空", Snackbar.LENGTH_SHORT).show();
            return;
        }
        presenter.updatePWD(oldPWD, newPWD,
                new UpdatePasswordCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            Snackbar.make(rootLayout, "修改密码成功", Snackbar.LENGTH_SHORT).show();
                            presenter.saveLocalPWD(newPWD, EditUserDataActivity.this);
                        } else {
                            Snackbar.make(rootLayout, "修改密码失败", Snackbar.LENGTH_SHORT).show();
                            clearPWD();
                        }
                    }
                });
    }

    @Override
    public void clearNickname() {
        etNickname.setText("");
    }
}
