package com.bigheart.byrtv.ui.presenter;

import android.content.Context;
import android.content.Intent;

import com.bigheart.byrtv.ui.module.ChannelModule;
import com.bigheart.byrtv.ui.view.MyCollectionView;
import com.bigheart.byrtv.ui.view.activity.TvLiveActivity;

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

    /**
     * 处理item点击事件
     *
     * @param channel 被选中的 item
     */
    public void onItemClick(ChannelModule channel) {
        Intent intent = new Intent(context, TvLiveActivity.class);
        intent.putExtra(TvLiveActivity.TV_SERVER_NAME, channel.getServerName());
        context.startActivity(intent);
    }

}
