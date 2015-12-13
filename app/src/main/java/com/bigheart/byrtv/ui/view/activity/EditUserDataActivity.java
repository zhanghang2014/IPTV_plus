package com.bigheart.byrtv.ui.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.RequestEmailVerifyCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.UpdatePasswordCallback;
import com.bigheart.byrtv.R;
import com.bigheart.byrtv.ui.presenter.EditUserDataPresenter;
import com.bigheart.byrtv.ui.view.EditUserDataView;
import com.bigheart.byrtv.util.LogUtil;

import static com.bigheart.byrtv.R.id.btn_edit_user_data_ver_code;
import static com.bigheart.byrtv.R.id.ll_edit_user_data_ver_email;


/**
 * Created by InG on 15/12/10.
 */
public class EditUserDataActivity extends BaseActivity implements EditUserDataView, View.OnClickListener {

    private Toolbar toolbar;
    private EditUserDataPresenter presenter;
    private EditText etUsername, etOldPWD, etNewPWD, etPhone, etCode,etEmail;
    private Button btnCommitNickname, btnCommitPWD, btnGiveUp,
            btnVerPhone, btnVerCode,btnVerEmail;
    private LinearLayout rootLayout, updateUsernameLayout,
            updatePWDLayout, verPhoneLayout,verEmailLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_data);
        initUI();
        initData();
    }

    public void initUI() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
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
        updateUsernameLayout = (LinearLayout) findViewById(R.id.ll_edit_user_data_username);
        updatePWDLayout = (LinearLayout) findViewById(R.id.ll_edit_user_data_pwd);
        verPhoneLayout = (LinearLayout) findViewById(R.id.ll_edit_user_data_ver_phone);
        verEmailLayout= (LinearLayout) findViewById(ll_edit_user_data_ver_email);

        updateUsernameLayout.setVisibility(View.GONE);
        updatePWDLayout.setVisibility(View.GONE);
        verPhoneLayout.setVisibility(View.GONE);
        verEmailLayout.setVisibility(View.GONE);

        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        if (type.equals("username")) {
            toolbar.setTitle("修改昵称");
            updateUsernameLayout.setVisibility(View.VISIBLE);
        } else if (type.equals("pwd")) {
            toolbar.setTitle("修改密码");
            updatePWDLayout.setVisibility(View.VISIBLE);
        } else if (type.equals("phone")) {
            //暂时由邮箱代替
            toolbar.setTitle("验证邮箱");
            verEmailLayout.setVisibility(View.VISIBLE);
        } else {
            //error
        }

        btnCommitNickname = (Button) findViewById(R.id.btn_edit_user_data_username);
        btnCommitPWD = (Button) findViewById(R.id.btn_edit_user_data_pwd);
        btnVerPhone = (Button) findViewById(R.id.btn_edit_user_data_ver_phone);
        btnGiveUp = (Button) findViewById(R.id.btn_edit_user_data_give_up);
        btnVerCode = (Button) findViewById(btn_edit_user_data_ver_code);
        btnVerEmail= (Button) findViewById(R.id.btn_edit_user_data_ver_email);

        btnCommitNickname.setOnClickListener(this);
        btnCommitPWD.setOnClickListener(this);
        btnVerPhone.setOnClickListener(this);
        btnGiveUp.setOnClickListener(this);
        btnVerCode.setOnClickListener(this);
        btnVerEmail.setOnClickListener(this);

        etUsername = (EditText) findViewById(R.id.et_edit_user_username);
        etOldPWD = (EditText) findViewById(R.id.et_edit_user_old_pwd);
        etNewPWD = (EditText) findViewById(R.id.et_edit_user_new_pwd);
        etPhone = (EditText) findViewById(R.id.et_edit_user_phone);
        etCode = (EditText) findViewById(R.id.et_edit_user_code);
        etEmail= (EditText) findViewById(R.id.et_edit_user_email);
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

            case R.id.btn_edit_user_data_username:
                updateNickname();
                break;

            case R.id.btn_edit_user_data_ver_phone:
//                verPhone();
                break;

            case R.id.btn_edit_user_data_give_up:
                finish();
                break;

            case R.id.btn_edit_user_data_ver_code:
//                verCode();
                break;

            case R.id.btn_edit_user_data_ver_email:
                verEmail();
                break;
            default:
                break;
        }
    }

    @Override
    public void clearEditText() {
        etPhone.setText("");
        etUsername.setText("");
        etOldPWD.setText("");
        etNewPWD.setText("");
        etPhone.setText("");
        etCode.setText("");
    }

    @Override
    public void updatePWD() {
        final String oldPWD = etOldPWD.getText().toString().trim();
        final String newPWD = etNewPWD.getText().toString().trim();

        if (presenter.getLocalPWD().equals("f32@ds*@&dsa")){
            etOldPWD.setText("f32@ds*@&dsa");
        }

        if (oldPWD.equals("") || oldPWD == null || newPWD.equals("") || newPWD == null) {
            Snackbar.make(rootLayout, "原始密码或新密码不能为空", Snackbar.LENGTH_SHORT).show();
            return;
        }
        presenter.updatePWD(oldPWD, newPWD,
                new UpdatePasswordCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            Toast.makeText(EditUserDataActivity.this, "修改密码成功", Toast.LENGTH_SHORT).show();
                            presenter.saveLocalPWD(newPWD);
                        } else {
                            Toast.makeText(EditUserDataActivity.this, "修改密码失败", Toast.LENGTH_SHORT).show();
                            clearEditText();
                        }
                    }
                });
    }

    @Override
    public void verEmail() {
        final String email = etEmail.getText().toString().trim();
        if(email==null || email.equals("")){
            toast("邮箱地址不能为空");
        }else{
            presenter.updateEmail(email, new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if(e==null){
                        presenter.verEmail(email, new RequestEmailVerifyCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e==null){
                                    toast("请前往邮箱查收验证邮件");
                                    presenter.updateUsername(email, new SaveCallback() {
                                        @Override
                                        public void done(AVException e) {
                                            if(e==null){
                                                toast("使用邮箱可以登陆啦");
                                            }else {
                                                toast(e.toString());
                                            }
                                        }
                                    });
                                }else {
                                    toast(e.toString());
                                }
                            }
                        });
                    }else{
                        toast(e.toString());
                    }
                }
            });
        }
    }

//    @Override
//    public void verPhone() {
//        final String phone = etPhone.getText().toString().trim();
//        if (phone == null || phone.equals("")) {
//            Snackbar.make(rootLayout, "手机号不能为空", Snackbar.LENGTH_SHORT).show();
//        } else {
//            presenter.updatePhone(phone, new SaveCallback() {
//                @Override
//                public void done(AVException e) {
//                    if (e == null) {
//                        presenter.verPhone(phone, new RequestMobileCodeCallback() {
//                            @Override
//                            public void done(AVException e) {
//                                if (e == null) {
//                                    Toast.makeText(EditUserDataActivity.this, "验证码已发送", Toast.LENGTH_SHORT).show();
//                                } else {
//                                    Toast.makeText(EditUserDataActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
//                                    LogUtil.d("smsCode", e.toString());
//                                    clearEditText();
//                                }
//                            }
//                        });
//                    } else {
//                        Toast.makeText(EditUserDataActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
//                        LogUtil.d("smsCode", e.toString());
//                        clearEditText();
//                    }
//                }
//            });
//        }
//    }

//    @Override
//    public void verCode() {
//        String code = etCode.getText().toString().trim();
//        presenter.verCode(code, new AVMobilePhoneVerifyCallback() {
//            @Override
//            public void done(AVException e) {
//                if (e == null) {
//                    Toast.makeText(EditUserDataActivity.this, "验证成功", Toast.LENGTH_SHORT).show();
//                    presenter.reLogin(new LogInCallback() {
//                        @Override
//                        public void done(AVUser avUser, AVException e) {
//
//                        }
//                    });
//                } else {
//                    Toast.makeText(EditUserDataActivity.this, "验证失败", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }

    @Override
    public void updateNickname() {
        String username = etUsername.getText().toString();
        if (username == null || username.equals("")) {
            Snackbar.make(rootLayout, "用户名不能为空", Snackbar.LENGTH_SHORT).show();
        } else {
            presenter.updateNickname(username, new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        Toast.makeText(EditUserDataActivity.this, "更改成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(rootLayout, "更改失败，换一个试试？", Snackbar.LENGTH_SHORT).show();
                        LogUtil.d("username", e.toString());
                        clearEditText();
                    }
                }
            });
        }
    }

    //重发验证码功能 暂时不用
//    @Override
//    public void reSendCode() {
//        final String phone = etPhone.getText().toString().trim();
//        presenter.verPhone(phone, new RequestMobileCodeCallback() {
//            @Override
//            public void done(AVException e) {
//                if (e == null) {
//                    Snackbar.make(rootLayout, "验证码已发送", Snackbar.LENGTH_SHORT).show();
//                } else {
//                    Snackbar.make(rootLayout, "验证码发送失败", Snackbar.LENGTH_SHORT).show();
//                    LogUtil.d("smsCode", e.toString());
//                    clearEditText();
//                }
//            }
//        });
//    }

}
