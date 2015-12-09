package com.bigheart.byrtv.ui.presenter;

import android.content.Context;

import com.bigheart.byrtv.ui.view.MyCollectionView;

/**
 * Created by BigHeart on 15/12/8.
 */
public class MyCollectionPresenter extends Presenter {
    private MyCollectionView view;
    private Context context;

    public MyCollectionPresenter(Context c, MyCollectionView myCollectionView) {
        context = c;
        view = myCollectionView;
    }
}
