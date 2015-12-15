package com.bigheart.byrtv.ui.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.bigheart.byrtv.R;
import com.bigheart.byrtv.ui.presenter.LoginActivityPresenter;
import com.bigheart.byrtv.ui.view.LoginActivityView;

/**
 * Created by InG on 15/12/13.
 */
public class LoginActivity extends BaseActivity implements LoginActivityView {

    private Toolbar toolbar;
    private EditText etUsername, etPWD;
    private String username,pwd;
    private LoginActivityPresenter presenter;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initUI();
        initData();
    }

    public void initUI() {
        //toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("登陆");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etUsername= (EditText) findViewById(R.id.et_login_username);
        etPWD= (EditText) findViewById(R.id.et_login_pwd);
        btnLogin= (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    public void initData(){
        presenter=new LoginActivityPresenter(this,this);
    }

    @Override
    public void loginSuccess() {
        toast("登陆成功");
        //本地缓存用户
        presenter.saveLocalUser(username,pwd);
        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void loginFail() {
        etPWD.setText("");
        toast("登陆失败，请重试");
    }

    @Override
    public void login() {
        username=etUsername.getText().toString().trim();
        pwd=etPWD.getText().toString().trim();
        if(username !=null || username.equals("") || pwd!=null || pwd.equals("")){
            presenter.mLogin(username, pwd, new LogInCallback() {
                @Override
                public void done(AVUser avUser, AVException e) {
                    if(e==null){
                        loginSuccess();
                    }else{
                        loginFail();
                    }
                }
            });
        }else{
            toast("不能为空");
        }
    }
}
