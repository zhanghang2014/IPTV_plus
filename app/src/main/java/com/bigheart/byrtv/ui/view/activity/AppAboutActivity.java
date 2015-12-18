package com.bigheart.byrtv.ui.view.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.bigheart.byrtv.R;
import com.bigheart.byrtv.ui.view.AppAboutView;

/**
 * Created by InG on 15/12/16.
 */
public class AppAboutActivity extends BaseActivity implements AppAboutView {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_about);
        initUI();
    }

    private void initUI(){
        //toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("关于");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void contectToBigHerat() {

    }

    @Override
    public void contectToInG() {

    }
}
