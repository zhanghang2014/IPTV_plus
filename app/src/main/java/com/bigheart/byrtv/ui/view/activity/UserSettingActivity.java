package com.bigheart.byrtv.ui.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
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
    private TextView username, gender, friend;
    private Button logout, updatePWD, verPhone;
    private FrameLayout mLayout;
    private Toolbar toolbar;


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
        username = (TextView) findViewById(R.id.tv_user_setting_username);
        gender = (TextView) findViewById(R.id.tv_user_setting_gender);
        friend = (TextView) findViewById(R.id.tv_user_setting_friend);
        logout = (Button) findViewById(R.id.btn_user_setting_logout);
        verPhone = (Button) findViewById(R.id.btn_user_setting_ver_phone);
        updatePWD = (Button) findViewById(R.id.btn_user_setting_update_pwd);
        updatePWD.setOnClickListener(this);

        verPhone.setOnClickListener(this);
        logout.setOnClickListener(this);
        icon.setOnClickListener(this);
        username.setOnClickListener(this);
        friend.setOnClickListener(this);
        gender.setOnClickListener(this);
    }

    private void initData() {
        presenter = new UserSettingPresenter(this, this);
        username.setText(presenter.getNickname());
        gender.setText(presenter.getGender());
        friend.setText(presenter.getFriend());
    }


    @Override
    public void updateUserData() {
        initData();
    }

    @Override
    public void setEditEnable() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
        //手机验证相关
//        if(AVUser.getCurrentUser()!=null && AVUser.getCurrentUser().isMobilePhoneVerified()){
//            verPhone.setVisibility(View.GONE);
//            updatePWD.setVisibility(View.VISIBLE);
//        }else {
//            updatePWD.setVisibility(View.GONE);
//            verPhone.setVisibility(View.VISIBLE);
//        }

        //邮箱验证相关
        UserSettingPresenter tmp = new UserSettingPresenter(this,this);
        if (tmp.isVerEmailed()) {
            verPhone.setVisibility(View.GONE);
            updatePWD.setVisibility(View.VISIBLE);
        } else {
            updatePWD.setVisibility(View.GONE);
            verPhone.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(UserSettingActivity.this, EditUserDataActivity.class);
        switch (view.getId()) {
            case R.id.tv_user_setting_friend:
                Snackbar.make(mLayout, "友善度过低将限制部分功能", Snackbar.LENGTH_LONG).show();
                break;

            case R.id.tv_user_setting_username:
                intent.putExtra("type", "username");
                startActivity(intent);
                break;

            case R.id.btn_user_setting_update_pwd:
                intent.putExtra("type", "pwd");
                startActivity(intent);
                break;

            case R.id.tv_user_setting_gender:
                //TODO 更改性别
                break;

            case R.id.sdv_user_setting_icon:
                //TODO 用户头像上传
                break;

            case R.id.btn_user_setting_logout:
                //跳转至用户登陆,实际效果为切换用户
                presenter.logout();
                Snackbar.make(mLayout, "已注销登陆", Snackbar.LENGTH_SHORT).show();
                Intent intent1 = new Intent(UserSettingActivity.this,LoginActivity.class);
                startActivity(intent1);
                finish();
                break;

            case R.id.btn_user_setting_ver_phone:
                //暂时验证邮箱
                intent.putExtra("type", "phone");
                startActivity(intent);
                break;

            default:
                break;
        }
    }

}
