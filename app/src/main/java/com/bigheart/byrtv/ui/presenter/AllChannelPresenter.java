package com.bigheart.byrtv.ui.presenter;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.bigheart.byrtv.ByrTvApplication;
import com.bigheart.byrtv.R;
import com.bigheart.byrtv.data.sqlite.ChannelColumn;
import com.bigheart.byrtv.data.sqlite.SqlChannelManager;
import com.bigheart.byrtv.domain.interactor.ChannelsRsp;
import com.bigheart.byrtv.domain.interactor.GetChannelList;
import com.bigheart.byrtv.ui.module.ChannelModule;
import com.bigheart.byrtv.ui.view.AllChannelView;
import com.bigheart.byrtv.ui.view.activity.TvLiveActivity;

import java.util.ArrayList;

/**
 * Created by BigHeart on 15/12/7.
 */
public class AllChannelPresenter extends Presenter {

    private AllChannelView allChannelView;
    private Context context;
    private Handler handler = new Handler();
    private boolean hadSetDataFromNet = false;


    public AllChannelPresenter(Context c, AllChannelView view) {
        allChannelView = view;
        context = c;
    }

    /**
     * 收藏频道
     *
     * @param sqlId 所频道在的数据库中的 id
     * @param state 所频道在的数据库中的 id
     * @return 收藏失败返回 －1
     */
    public int updateChannelCollectedState(long sqlId, boolean state) {
        if (sqlId != -1) {
            ContentValues cv = new ContentValues();
            cv.put(ChannelColumn.IS_COLLECTION, state);
            int rst = SqlChannelManager.getInstance().upDateChannel(cv, ChannelColumn.CHANNEL_ID + " = ?", new String[]{String.valueOf(sqlId)});
            Log.i("AllChannelPresenter ", sqlId + "");
            return rst;
        }
        return -1;
    }

    /**
     * 处理item点击事件
     *
     * @param channel 被选中的 item
     */
    public void onItemClick(ChannelModule channel) {
        Intent intent = new Intent(context, TvLiveActivity.class);
        intent.putExtra(TvLiveActivity.TV_LIVE_NAME, channel.getChannelName());
        intent.putExtra(TvLiveActivity.TV_LIVE_URI, channel.getUri());
        context.startActivity(intent);
    }
}
