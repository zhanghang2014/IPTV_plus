package com.bigheart.byrtv.ui.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SaveCallback;
import com.bigheart.byrtv.R;
import com.bigheart.byrtv.ui.presenter.UserSettingPresenter;
import com.bigheart.byrtv.ui.view.UserSettingView;
import com.bigheart.byrtv.util.LogUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.ByteArrayOutputStream;


/**
 * Created by InG on 15/12/8.
 */
public class UserSettingActivity extends BaseActivity implements UserSettingView, View.OnClickListener {

    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int GALLERY_REQUEST_CODE = 2;
    private static final int CROP_REQUEST_CODE = 3;
    private static final int REQUEST_UPDATE_USER_DATA = 4;

    private UserSettingPresenter presenter;
    private SimpleDraweeView icon,bg;
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

        bg= (SimpleDraweeView) findViewById(R.id.sdv_user_setting_background);
    }

    private void initData() {
        presenter = new UserSettingPresenter(this, this);
        username.setText(presenter.getNickname());
        gender.setText(presenter.getGender());
        friend.setText(presenter.getFriend());

        refreshIcon();
    }


    @Override
    public void refreshUserData() {
        presenter.reLogin(new LogInCallback() {
            @Override
            public void done(AVUser avUser, AVException e) {
                if (e == null) {
                    initData();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //手机验证相关
//        if(AVUser.getCurrentUser()!=null && AVUser.getCurrentUser().isMobilePhoneVerified()){
//            verPhone.setVisibility(View.GONE);
//            updatePWD.setVisibility(View.VISIBLE);
//        }else {
//            updatePWD.setVisibility(View.GONE);
//            verPhone.setVisibility(View.VISIBLE);
//        }

        //邮箱验证相关
        if (presenter.isVerEmailed()) {
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
                startActivityForResult(intent, REQUEST_UPDATE_USER_DATA);
                break;

            case R.id.btn_user_setting_update_pwd:
                intent.putExtra("type", "pwd");
                startActivityForResult(intent, REQUEST_UPDATE_USER_DATA);
                break;

            case R.id.tv_user_setting_gender:
                //选择性别
                LogUtil.d("onclick", "user_setting_gender");
                updateGender();
                break;

            case R.id.sdv_user_setting_icon:
                if (presenter.getUser() == null) {
                    toast("请先登陆");
                } else {
                    selectPhoto();
                    toast("更改头像");
                    LogUtil.d("photo", ">>>>>>>>>>>>>updateIcon()");
//                    presenter.updateIcon(new SaveCallback() {
//                        @Override
//                        public void done(AVException e) {
//                            if (e==null){
//                                toast("更换头像成功");
//                            }else{
//                                toast(e.toString());
//                            }
//                        }
//                    });
                }
                break;

            case R.id.btn_user_setting_logout:
                //跳转至用户登陆,实际效果为切换用户
//                presenter.logout();
//                Snackbar.make(mLayout, "已注销登陆", Snackbar.LENGTH_SHORT).show();
                Intent intent1 = new Intent(UserSettingActivity.this, LoginActivity.class);
                startActivity(intent1);
                finish();
                break;

            case R.id.btn_user_setting_ver_phone:
                //暂时验证邮箱
                intent.putExtra("type", "phone");
                startActivityForResult(intent, REQUEST_UPDATE_USER_DATA);
                break;

            default:
                break;
        }
    }

    //头像
    @Override
    public void selectPhoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "选取图片"), GALLERY_REQUEST_CODE);
        LogUtil.d("photo", ">>>>>>>>>>>>>selectPhoto()");
    }

    @Override
    public void refreshIcon() {
        if (presenter.getIconUri() != null) {
            icon.setImageURI(presenter.getIconUri());
            bg.setImageURI(presenter.getIconUri());
        }
    }

    @Override
    public void updateGender() {
        LogUtil.d("onclick", "updateGender");

        final String[] gender = {""};
        String[] strings = new String[]{"男", "女", "保密"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("选择性别：")
                .setSingleChoiceItems(strings, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        LogUtil.d("gender", "gender:  " + i);
                        if (i == 0) {
                            gender[0] = "男";
                        } else if (i == 1) {
                            gender[0] = "女";
                        } else {
                            gender[0] = "保密";
                        }
                    }
                }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                presenter.updateGender(gender[0], new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            toast("修改性别成功");
                            refreshUserData();
                        } else {
                            toast(e.toString());
                        }
                    }
                });
            }
        }).create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case REQUEST_UPDATE_USER_DATA:
                refreshUserData();
                break;

            case GALLERY_REQUEST_CODE:
                if (data == null) {//相册
                    return;
                }
                Uri uri = data.getData();
                startActivityForResult(presenter.startImageZoom(uri), CROP_REQUEST_CODE);
                break;

            case CROP_REQUEST_CODE:
                //剪裁后的图片
                if (data != null && data.getExtras() != null) {
                    Bundle extras = data.getExtras();
                    Bitmap photo = extras.getParcelable("data");
                    //将Bitmap转换为Byte[]
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    //对图片进行压缩，第二个参数为压缩的百分比，100为不压缩
                    photo.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] bitmapByte = baos.toByteArray();
                    LogUtil.d("bytes", bitmapByte.toString());
                    presenter.updateIcon(bitmapByte, new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                toast("Success");
                                refreshUserData();
                            } else {
                                LogUtil.d("update", e.toString());
                            }
                        }
                    });
                }
                break;

            default:
                break;
        }
    }
}
