package com.bigheart.byrtv.ui.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.avos.avoscloud.feedback.FeedbackAgent;
import com.bigheart.byrtv.R;
import com.bigheart.byrtv.data.sharedpreferences.AppSettingOption;
import com.bigheart.byrtv.ui.presenter.AppSettingPresenter;
import com.bigheart.byrtv.ui.view.AppSettingView;

import static com.bigheart.byrtv.R.id.ll_app_setting_feedback;


/**
 * Created by InG on 15/12/8.
 */
public class AppSettingActivity extends BaseActivity implements AppSettingView, View.OnClickListener {

    private LinearLayout llMainPage, llSortWay, llAppUpdate,
            llAbout, llCloseDanMu, llFeedback;
    private AppCompatCheckBox ccbCloseDanMu;
    private TextView mainPage, sortWay;
    private int mainPages, sortWays, closeDanMus;
    private AppSettingPresenter presenter;
    private Toolbar toolbar;
    private int tmp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_setting);
        initUI();
        iniData();
    }

    private void initUI() {
        //toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("软件设置");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        llMainPage = (LinearLayout) findViewById(R.id.ll_app_setting_main_page);
        llSortWay = (LinearLayout) findViewById(R.id.ll_app_setting_channel_sort);
        llAppUpdate = (LinearLayout) findViewById(R.id.ll_app_setting_check_update);
        llAbout = (LinearLayout) findViewById(R.id.ll_app_setting_about);
        ccbCloseDanMu = (AppCompatCheckBox) findViewById(R.id.ccb_app_setting_close_danmu);
        mainPage = (TextView) findViewById(R.id.tv_app_setting_main_page);
        sortWay = (TextView) findViewById(R.id.tv_app_setting_channel_sort);

        llMainPage.setOnClickListener(this);
        llSortWay.setOnClickListener(this);
        llAppUpdate.setOnClickListener(this);
        llAbout.setOnClickListener(this);

//        ccbCloseDanMu.setOnClickListener(this);

        llCloseDanMu = (LinearLayout) findViewById(R.id.ll_app_setting_close_danmu);
        llCloseDanMu.setOnClickListener(this);
        llFeedback = (LinearLayout) findViewById(ll_app_setting_feedback);
        llFeedback.setOnClickListener(this);
    }

    private void iniData() {
        presenter = new AppSettingPresenter(this);
        mainPages = presenter.getMainPage();
        sortWays = presenter.getChannelSort();
        closeDanMus = presenter.getDanMuSetting();

        if (mainPages == AppSettingOption.NOT_SETTING) {
            //默认设置:全部频道
            presenter.saveMainPage(AppSettingOption.ALL_CHANNEL);
            mainPage.setText("全部频道");
        } else if (mainPages == AppSettingOption.ALL_CHANNEL) {
            mainPage.setText("全部频道");
        } else if (mainPages == AppSettingOption.MY_CHANNEL) {
            mainPage.setText("收藏的频道");
        }

        if (sortWays == AppSettingOption.NOT_SETTING) {
            //默认设置：拼音排序
            presenter.saveChannelSort(AppSettingOption.SORT_PINYIN);
            sortWay.setText("拼音首字母");
        } else if (sortWays == AppSettingOption.SORT_PINYIN) {
            sortWay.setText("拼音首字母");
        } else if (sortWays == AppSettingOption.SORT_PEOPLE) {
            sortWay.setText("频道在线人数");
        }

        if (closeDanMus == AppSettingOption.NOT_SETTING) {
            //默认设置：打开弹幕
            presenter.saveDanMuSetting(AppSettingOption.OPEN_DANMU);
            ccbCloseDanMu.setChecked(false);
        } else if (closeDanMus == AppSettingOption.OPEN_DANMU) {
            ccbCloseDanMu.setChecked(false);
        } else if (closeDanMus == AppSettingOption.CLOSE_DANMU) {
            ccbCloseDanMu.setChecked(true);
        }
    }

    @Override
    public void doAppUpdate() {

    }

    @Override
    public void checkAppUpadte() {

    }

    @Override
    public void danmuOption() {

        if (ccbCloseDanMu.isChecked()) {
            ccbCloseDanMu.setChecked(false);
        } else {
            ccbCloseDanMu.setChecked(true);
        }

        if (ccbCloseDanMu.isChecked()) {
            presenter.saveDanMuSetting(AppSettingOption.CLOSE_DANMU);
        } else {
            presenter.saveDanMuSetting(AppSettingOption.OPEN_DANMU);
        }
//        iniData();
    }

    @Override
    public void channelSortWay() {
        tmp=-1;
        final String[] op = new String[]{"拼音首字母", "频道在线人数"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("选择频道排序方式：")
                .setSingleChoiceItems(op, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            tmp = AppSettingOption.SORT_PINYIN;
                        } else {
                            tmp = AppSettingOption.SORT_PEOPLE;
                        }
                    }
                }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                presenter.saveChannelSort(tmp);
                iniData();
            }
        }).create().show();
    }

    @Override
    public void mainPage() {
        tmp=-1;
        final String[] op = new String[]{"全部频道", "收藏的频道"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("选择默认首页：")
                .setSingleChoiceItems(op, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            tmp = AppSettingOption.ALL_CHANNEL;
                        } else {
                            tmp = AppSettingOption.MY_CHANNEL;
                        }
                    }
                }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                presenter.saveMainPage(tmp);
                iniData();
            }
        }).create().show();
    }

    @Override
    public void about() {
        Intent intent = new Intent(AppSettingActivity.this, AppAboutActivity.class);
        startActivity(intent);
    }

    @Override
    public void feedback() {
        FeedbackAgent agent = new FeedbackAgent(this);
        agent.startDefaultThreadActivity();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_app_setting_main_page:
                mainPage();
                break;

            case R.id.ll_app_setting_channel_sort:
                channelSortWay();
                break;

            case R.id.ll_app_setting_close_danmu:
                danmuOption();
                break;

            case R.id.ll_app_setting_check_update:
                doAppUpdate();
                break;

            case R.id.ll_app_setting_about:
                about();
                break;

            case R.id.ll_app_setting_feedback:
                feedback();
                break;
            default:
                break;
        }
    }
}
