package com.bigheart.byrtv.ui.view.activity;

import android.app.Application;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.feedback.FeedbackAgent;
import com.bigheart.byrtv.ByrTvApplication;
import com.bigheart.byrtv.R;
import com.bigheart.byrtv.data.sharedpreferences.AppSettingOption;
import com.bigheart.byrtv.data.sharedpreferences.AppSettingPreferences;
import com.bigheart.byrtv.ui.presenter.MainActivityPresenter;
import com.bigheart.byrtv.ui.view.FragContactToAct;
import com.bigheart.byrtv.ui.view.MainActivityView;
import com.bigheart.byrtv.ui.view.fragment.AllChannelFragment;
import com.bigheart.byrtv.ui.view.fragment.MyCollectionFragment;
import com.bigheart.byrtv.util.ChannelSortType;
import com.bigheart.byrtv.util.LogUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;


public class MainActivity extends BaseActivity implements MainActivityView, FragContactToAct, View.OnClickListener {


    private final int FRAGMENT_NUM = 2, POS_MY_COLLECTION = 0, POS_ALL_CHANNEL = 1;


    private ViewPager viewPager;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private PagerAdapter pagerAdapter;
    private AllChannelFragment channelFragment;
    private MyCollectionFragment myCollectionFragment;

    private MainActivityPresenter presenter;

    private static int okFragCount = 0;

    private AppSettingPreferences sp;


    //drawer
    private DrawerLayout drawer;
    private SimpleDraweeView icon, bg;
    private TextView nickname;

    private LinearLayout collectionC, allC, profile, setting, navHeadMain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fresco.initialize(this);
        setContentView(R.layout.activity_main);
        sp = new AppSettingPreferences(this);
        initUI();
        initData();
        LogUtil.d("MainActivity", "onCreate " + okFragCount);


        /**
         * 查询Feedback回复的信息
         *  当用户收到开发者的新回复时，就会产生一个新的消息通知。如果你需要改变通知的图标，
         *  请替换 res 下的 avoscloud_feedback_notification.png 文件即可。
         * */
        FeedbackAgent agent = new FeedbackAgent(this);
        agent.sync();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        okFragCount = 0;
        LogUtil.d("MainActivity", "onDestroy " + okFragCount);
    }

    private void initUI() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.my_collections);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);

//        navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        toggle.syncState();
        drawer.setDrawerListener(toggle);


        viewPager = (ViewPager) findViewById(R.id.vp_main);
        pagerAdapter = new PagerAdapter(getFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case POS_MY_COLLECTION:
                        toolbar.setTitle(R.string.my_collections);
                        break;
                    case POS_ALL_CHANNEL:
                        toolbar.setTitle(R.string.all_channel);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //首页默认显示
        if (sp.getMainPage() == AppSettingOption.MY_CHANNEL) {
            viewPager.setCurrentItem(POS_MY_COLLECTION);
        } else if (sp.getMainPage() == AppSettingOption.ALL_CHANNEL) {
            viewPager.setCurrentItem(POS_ALL_CHANNEL);
        }

        //首页排序方式放在了AppSettingPreferences
        channelFragment = AllChannelFragment.newInstance(new AppSettingPreferences(this).getAllChannelOrderType(), this);

        myCollectionFragment = MyCollectionFragment.newInstance(ChannelSortType.SORT_BY_PEOPLE_NUM, this);

        icon = (SimpleDraweeView) findViewById(R.id.sdv_user_icon);
        bg = (SimpleDraweeView) findViewById(R.id.sdv_nav_head_background);
        nickname = (TextView) findViewById(R.id.tv_nickname);
        collectionC = (LinearLayout) findViewById(R.id.collection_of_channels);
        allC = (LinearLayout) findViewById(R.id.all_of_channels);
        profile = (LinearLayout) findViewById(R.id.user_setting);
        setting = (LinearLayout) findViewById(R.id.app_setting);
        navHeadMain = (LinearLayout) findViewById(R.id.nav_head_main);
        initDrawerUI();
    }


    private void initDrawerUI() {
        allC.setOnClickListener(this);
        collectionC.setOnClickListener(this);
        profile.setOnClickListener(this);
        setting.setOnClickListener(this);
        navHeadMain.setOnClickListener(this);
//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(MenuItem menuItem) {
//                int id = menuItem.getItemId();
//                switch (id) {
//                    case R.id.collection_of_channels:
//                        viewPager.setCurrentItem(POS_MY_COLLECTION);
//                        break;
//                    case R.id.all_of_channels:
//                        viewPager.setCurrentItem(POS_ALL_CHANNEL);
//                        break;
//                    case R.id.user_setting:
//                        Intent intent = new Intent(MainActivity.this, UserSettingActivity.class);
//                        startActivity(intent);
//                        break;
//                    case R.id.app_setting:
//                        Intent intent1 = new Intent(MainActivity.this, AppSettingActivity.class);
//                        startActivity(intent1);
//                        break;
//                    default:
//                        break;
//                }
//                drawer.closeDrawer(GravityCompat.START);
//                return true;
//            }
//        });

    }

    private void initData() {
        if (presenter == null)
            presenter = new MainActivityPresenter(this, this, myCollectionFragment, channelFragment);

        presenter.checkUpdate();
        presenter.login();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //防止点击下方页面
            case R.id.nav_head_main:
                break;
            case R.id.collection_of_channels:
                viewPager.setCurrentItem(POS_MY_COLLECTION);
                break;
            case R.id.all_of_channels:
                viewPager.setCurrentItem(POS_ALL_CHANNEL);
                break;
            case R.id.user_setting:
                Intent intent = new Intent(MainActivity.this, UserSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.app_setting:
                Intent intent1 = new Intent(MainActivity.this, AppSettingActivity.class);
                startActivity(intent1);
                break;
            default:
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
    }

    private class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case POS_MY_COLLECTION:
                    return myCollectionFragment;
                case POS_ALL_CHANNEL:
                    return channelFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return FRAGMENT_NUM;
        }
    }

    @Override
    public void login(Exception e) {
        if (e == null) {
//            this.toast("login success!" + AVUser.getCurrentUser().getUsername());
            ByrTvApplication.updateReadGetPeopleNumState(true, true, true, ByrTvApplication.updateWhat.upDateLoginState);
            presenter.instanceAVIMClient();

        } else {
//            this.toast("login fail");
            e.printStackTrace();
        }
    }

    public void refreshIcon() {
        if (presenter.getIconUri() != null) {
            icon.setImageURI(presenter.getIconUri());
            bg.setImageURI(presenter.getIconUri());
        }
        nickname.setText(presenter.getNickname());
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void fragmentInitOk() {
        okFragCount++;
        LogUtil.i("MainActivity", "get ok msg " + okFragCount);
        if (okFragCount == 2) {
            if (presenter == null)
                presenter = new MainActivityPresenter(this, this, myCollectionFragment, channelFragment);
            presenter.pullData();
//            Log.i("MainActivity", "pullData");
        }
    }

    @Override
    public void notifyMyCollectionFrg() {
        presenter.upDateMyCollectionFrg();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshIcon();
    }

    @Override
    public void showUpdateDialog(String newVersionName, String updateInfo, final String downloadUrl) {
        if (!MainActivity.this.isFinishing()) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(getResources().getString(R.string.app_name) + " " + newVersionName)
                    .setMessage(Html.fromHtml(updateInfo))
                    .setPositiveButton("现在更新", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Intent intent = new Intent();
                            intent.setAction("android.intent.action.VIEW");
                            intent.setData(Uri.parse("http://" + downloadUrl));
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    }).show();
        }
    }
}