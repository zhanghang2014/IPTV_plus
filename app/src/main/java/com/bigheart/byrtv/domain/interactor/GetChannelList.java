package com.bigheart.byrtv.domain.interactor;

import android.content.Context;

import com.bigheart.byrtv.data.net.GetChannelTask;
import com.bigheart.byrtv.data.net.NetWorkRsp;
import com.bigheart.byrtv.data.sqlite.SqlChannelManager;
import com.bigheart.byrtv.ui.module.ChannelModule;

import java.util.ArrayList;

/**
 * Created by BigHeart on 15/12/8.
 */
public class GetChannelList {
    private ChannelsRsp rsp;


    public GetChannelList(ChannelsRsp channelsRsp) {
        rsp = channelsRsp;
    }

    public void getChannels() {

        rsp.getFromSqLiteSuccess(SqlChannelManager.getInstance().queryChannel(null, null, null, null, null, null));
        //
        new GetChannelTask(new NetWorkRsp<ArrayList<ChannelModule>, Exception>() {
            @Override
            public void onSuccess(ArrayList<ChannelModule> channels) {
                rsp.getFromNetSuccess(channels);
            }

            @Override
            public void onError(Exception e) {
                rsp.getFromNetError(e);
            }
        }).start();
    }
}
