package com.bigheart.byrtv.ui.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.bigheart.byrtv.data.sharedpreferences.AppSettingOption;
import com.bigheart.byrtv.ui.module.ChannelModule;
import com.bigheart.byrtv.ui.view.MyCollectionView;
import com.bigheart.byrtv.ui.view.activity.TvLiveActivity;
import com.bigheart.byrtv.util.LogUtil;
import com.bigheart.byrtv.util.ObtainPeopNumPool;
import com.bigheart.byrtv.util.SortByPeople;
import com.bigheart.byrtv.util.SortByPinYin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by BigHeart on 15/12/8.
 */
public class MyCollectionPresenter extends Presenter implements FinishObtainDataRsp {
    private MyCollectionView view;
    private Context context;
    private boolean HadUpdateData;

    private Handler handler;

    public MyCollectionPresenter(Context c, MyCollectionView myCollectionView) {
        context = c;
        view = myCollectionView;
        handler = new Handler();
    }

    @Override
    public void finishObtainPeopNum(boolean isSuccess) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                view.stopRefresh();
                view.updateData();
            }
        });
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


    public ArrayList<ChannelModule> channelSort(int settingOption, ArrayList<ChannelModule> channels) {
        if (settingOption == AppSettingOption.SORT_PINYIN) {
//            if (!HadUpdateData) {
//                //拼音只需更新一次
//                HadUpdateData = true;
            Collections.sort(channels, new SortByPinYin());
//            }
            return channels;
        } else {
            Collections.sort(channels, new SortByPeople());
            return channels;
        }
    }

    public void upDateChannelPeopNum(ArrayList<ChannelModule> listC) {
        ObtainPeopNumPool.getInstance(MyCollectionPresenter.this).execute(listC);
    }
}
