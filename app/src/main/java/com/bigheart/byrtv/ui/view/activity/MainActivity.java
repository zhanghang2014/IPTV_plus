package com.bigheart.byrtv.ui.view.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;

import com.avos.avoscloud.AVUser;
import com.bigheart.byrtv.R;
import com.bigheart.byrtv.ui.presenter.MainActivityPresenter;
import com.bigheart.byrtv.ui.view.MainActivityView;
import com.bigheart.byrtv.ui.view.fragment.AllChannelFragment;
import com.bigheart.byrtv.ui.view.fragment.MyCollectionFragment;
import com.bigheart.byrtv.util.ChannelSortType;

public class MainActivity extends BaseActivity implements MainActivityView {


    private final int FRAGMENT_NUM = 2, POS_MY_COLLECTION = 0, POS_ALL_CHANNEL = 1;


    private ViewPager viewPager;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private PagerAdapter pagerAdapter;
    private AllChannelFragment channelFragment;
    private MyCollectionFragment myCollectionFragment;

    private MainActivityPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        initData();
    }

    private void initUI() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.my_collections);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);

        toggle.syncState();
        drawerLayout.setDrawerListener(toggle);


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

        channelFragment = AllChannelFragment.newInstance(ChannelSortType.SORT_BY_PEOPLE_NUM);
        myCollectionFragment = MyCollectionFragment.newInstance(ChannelSortType.SORT_BY_PEOPLE_NUM);

        initDrawerUI();
    }

    // TODO: 15/12/8 绑定侧滑菜单的控件、添加监听
    private void initDrawerUI() {

    }

    private void initData() {
        presenter = new MainActivityPresenter(this, this);
        presenter.login();
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
            this.toast("login success!" + AVUser.getCurrentUser().getUsername());
        } else {
            this.toast("login fail");
            e.printStackTrace();
        }
    }
}