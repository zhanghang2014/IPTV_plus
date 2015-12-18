package com.bigheart.byrtv.ui.presenter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.bigheart.byrtv.data.sharedpreferences.AppSettingOption;
import com.bigheart.byrtv.data.sqlite.ChannelColumn;
import com.bigheart.byrtv.data.sqlite.SqlChannelManager;
import com.bigheart.byrtv.ui.module.ChannelModule;
import com.bigheart.byrtv.ui.view.AllChannelView;
import com.bigheart.byrtv.ui.view.activity.TvLiveActivity;
import com.bigheart.byrtv.util.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
        intent.putExtra(TvLiveActivity.TV_SERVER_NAME, channel.getServerName());
        context.startActivity(intent);
    }

    public ArrayList<ChannelModule> channelSort(int settingOption,ArrayList<ChannelModule> channels){
        if(settingOption == AppSettingOption.SORT_PINYIN){
            Collections.sort(channels, new SortByPinYin());
            return channels;
        }else{
            Collections.sort(channels, new SortByPeople());
            return channels;
        }
    }

    class SortByPinYin implements Comparator {
        @Override
        public int compare(Object o, Object t1) {
            ChannelModule c1 = (ChannelModule) o;
            ChannelModule c2 = (ChannelModule) t1;
//            String c1Name = PinyinHelper.convertToPinyinString(c1.getChannelName(), "", PinyinFormat.WITHOUT_TONE);
//            String c2Name = PinyinHelper.convertToPinyinString(c2.getChannelName(), "", PinyinFormat.WITHOUT_TONE);
            String c1Name=c1.getServerName();
            String c2Name=c2.getServerName();
            LogUtil.d("channel", c1Name);
            LogUtil.d("chi",c1.getChannelName());


            return c1Name.compareTo(c2Name);
        }
    }

    class SortByPeople implements Comparator {

        @Override
        public int compare(Object o, Object t1) {
            ChannelModule c1 = (ChannelModule) o;
            ChannelModule c2 = (ChannelModule) t1;
            if (c1.getPeopleNum() > c2.getPeopleNum()) {
                return 1;
            } else if (c1.getPeopleNum() == c2.getPeopleNum()) {
                return 0;
            } else {
                return -1;
            }
        }
    }
}
