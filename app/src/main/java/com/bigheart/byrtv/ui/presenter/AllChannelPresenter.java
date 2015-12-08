package com.bigheart.byrtv.ui.presenter;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.bigheart.byrtv.ByrTvApplication;
import com.bigheart.byrtv.R;
import com.bigheart.byrtv.domain.interactor.ChannelsRsp;
import com.bigheart.byrtv.domain.interactor.GetChannelList;
import com.bigheart.byrtv.ui.module.ChannelModule;
import com.bigheart.byrtv.ui.view.AllChannelView;

import java.util.ArrayList;

/**
 * Created by BigHeart on 15/12/7.
 */
public class AllChannelPresenter extends Presenter {
    private AllChannelView allChannelView;
    private ArrayList<ChannelModule> channels;
    private Context context;
    private Handler handler = new Handler();

    public AllChannelPresenter(Context c, AllChannelView view) {
        allChannelView = view;
        context = c;
    }

    @Override
    public void init() {
        super.init();
        allChannelView.startRefresh();
        new GetChannelList(new ChannelsRsp() {
            @Override
            public void getFromSqLiteSuccess(ArrayList<ChannelModule> channels) {

            }

            @Override
            public void getFromSqLiteError(Exception e) {

            }

            @Override
            public void getFromNetSuccess(final ArrayList<ChannelModule> channels) {
//                Log.i("AllChannelPresenter", channels.size() + "");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        allChannelView.stopRefresh();
                        allChannelView.updateData(channels);
                    }
                });
            }

            @Override
            public void getFromNetError(Exception e) {
                // TODO: 15/12/8 handler e
                e.printStackTrace();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, R.string.net_wrong, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).getChannels();
    }
}
