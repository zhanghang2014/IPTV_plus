package com.bigheart.byrtv.ui.view.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.UpdatePasswordCallback;
import com.bigheart.byrtv.R;
import com.bigheart.byrtv.ui.presenter.UserSettingPresenter;
import com.bigheart.byrtv.ui.view.UserSettingView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by InG on 15/12/8.
 */
public class UserSettingActivity extends BaseActivity implements UserSettingView, View.OnClickListener {

    private UserSettingPresenter presenter;
    private SimpleDraweeView icon;
    private TextView nickname, gender, friend;
    private Button logout, updatePWD, verEmail;
    private FrameLayout mLayout;
    private Toolbar toolbar;


    //handler
    private static final int UPDARE_DATA = 0x01;
    private static final int UPDATE_PWD = 0x02;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case UPDARE_DATA:
                    initData();
                    Snackbar.make(mLayout, "已注销登陆", Snackbar.LENGTH_SHORT).show();
                    break;
                case UPDATE_PWD:
                    Snackbar.make(mLayout, "已更新密码", Snackbar.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_user_setting);
        initUI();
        initData();
    }

    private void initUI() {
        //toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("用户设置");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mLayout = (FrameLayout) findViewById(R.id.layout_user_setting);
        icon = (SimpleDraweeView) findViewById(R.id.sdv_user_setting_icon);
        nickname = (TextView) findViewById(R.id.tv_user_setting_nickname);
        gender = (TextView) findViewById(R.id.tv_user_setting_gender);
        friend = (TextView) findViewById(R.id.tv_user_setting_friend);
        logout = (Button) findViewById(R.id.btn_user_setting_logout);
        updatePWD = (Button) findViewById(R.id.btn_user_update_pwd);
        verEmail = (Button) findViewById(R.id.btn_user_ver_email);

        updatePWD.setOnClickListener(this);
        verEmail.setOnClickListener(this);
        logout.setOnClickListener(this);
        icon.setOnClickListener(this);
        nickname.setOnClickListener(this);
        gender.setOnClickListener(this);
        friend.setOnClickListener(this);
    }

    private void initData() {
        presenter = new UserSettingPresenter(this, this);
        nickname.setText(presenter.getNickname());
        gender.setText(presenter.getGender());
        friend.setText(presenter.getFriend());
    }


    @Override
    public void updateUserData() {
        initData();
    }

    @Override
    public void updatePWD() {
        //TODO goto new activity
        presenter.updatePasswrd("test", "test", new UpdatePasswordCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    handler.sendEmptyMessage(UPDATE_PWD);
                } else {
                    Snackbar.make(mLayout, "更改密码失败", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_user_setting_friend:
                break;

            case R.id.tv_user_setting_gender:
                break;

            case R.id.tv_user_setting_nickname:
                break;

            case R.id.sdv_user_setting_icon:
                break;

            case R.id.btn_user_setting_logout:
                presenter.logout();
                handler.sendEmptyMessage(UPDARE_DATA);
                break;

            case R.id.btn_user_update_pwd:
                updatePWD();
                break;

            case R.id.btn_user_ver_email:
                Snackbar.make(mLayout, "验证邮箱", Snackbar.LENGTH_SHORT).show();
                break;

            default:
                break;
        }
    }
}
