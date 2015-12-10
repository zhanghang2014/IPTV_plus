package com.bigheart.byrtv.ui.view.activity;

import android.os.Bundle;

import com.bigheart.byrtv.ui.presenter.EditUserDataPresenter;
import com.bigheart.byrtv.ui.view.EditUserDataView;

/**
 * Created by InG on 15/12/10.
 */
public class EditUserDataActivity extends BaseActivity implements EditUserDataView {

    private EditUserDataPresenter presenter;

    @Override
    public void SaveAndBack() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initData();
    }

    public void initUI(){

    }

    public void initData(){
        presenter=new EditUserDataPresenter(this,this);
    }
}
