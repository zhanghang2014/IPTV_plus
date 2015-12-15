package com.bigheart.byrtv.ui.presenter;

import android.content.ContentValues;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SignUpCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.bigheart.byrtv.R;
import com.bigheart.byrtv.data.sharedpreferences.AccountPreferences;
import com.bigheart.byrtv.data.sqlite.ChannelColumn;
import com.bigheart.byrtv.data.sqlite.SqlChannelManager;
import com.bigheart.byrtv.domain.interactor.ChannelsRsp;
import com.bigheart.byrtv.domain.interactor.GetChannelList;
import com.bigheart.byrtv.ui.module.ChannelModule;
import com.bigheart.byrtv.ui.view.AllChannelView;
import com.bigheart.byrtv.ui.view.MainActivityView;
import com.bigheart.byrtv.ui.view.MyCollectionView;
import com.bigheart.byrtv.util.LogUtil;
import com.bigheart.byrtv.util.SqlUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import javax.xml.validation.Validator;

/**
 * * 控制AllChannelFragment,MyCollectionFragment
 * <p/>
 * <p/>
 * Created by BigHeart on 15/12/8.
 */
public class MainActivityPresenter extends Presenter {
    private Context context;
    private MainActivityView mainActivityView;
    private MyCollectionView collectionView;
    private AllChannelView channelView;
    private volatile ArrayList<ChannelModule> allChannels;

    private boolean hadSetDataFromNet = false;
    private Handler handler;

    public MainActivityPresenter(Context c, MainActivityView view, MyCollectionView myCollectionView, AllChannelView allChannelView) {
        context = c;
        mainActivityView = view;
        collectionView = myCollectionView;
        channelView = allChannelView;

        handler = new Handler();
        allChannels = new ArrayList<>();
    }

    public void pullData() {
        collectionView.startRefresh();
        channelView.startRefresh();


        new GetChannelList(new ChannelsRsp() {
            @Override
            public void getFromSqLiteSuccess(final ArrayList<ChannelModule> channels) {
                if (channels.size() > 0) {
                    allChannels = channels;
                    if (!hadSetDataFromNet) {
                        final ArrayList<ChannelModule> collectionChannels = filterCollectionChannel(channels);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("all Channels sql", channels.size() + " group");
                                Log.i("collection Channels sql", collectionChannels.size() + " group");
                                channelView.updateData(channels);
                                collectionView.updateData(collectionChannels);
                            }
                        });
                    }
                }
            }

            @Override
            public void getFromSqLiteError(Exception e) {

            }

            @Override
            public void getFromNetSuccess(final ArrayList<ChannelModule> channels) {
                hadSetDataFromNet = true;
                //只需更新全部列表
                allChannels = channels;
                updateSqlChannel(channels);

                Log.i("All Channel net", channels.size() + " group");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        channelView.updateData(channels);
                        channelView.stopRefresh();
                        collectionView.stopRefresh();

                    }
                });
            }

            @Override
            public void getFromNetError(Exception e) {
                e.printStackTrace();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        channelView.stopRefresh();
                        collectionView.stopRefresh();
                        Toast.makeText(context, R.string.net_wrong, Toast.LENGTH_SHORT).show();
                        Log.i("MainActivityPresenter", "can not get channel from net");
                    }
                });
            }
        }).getChannels();
    }

    /**
     * 更新MyCollectionFrg中的数据
     */
    public void upDateMyCollectionFrg() {
        final ArrayList<ChannelModule> newCollectionChannels = filterCollectionChannel(allChannels);
        handler.post(new Runnable() {
            @Override
            public void run() {
                collectionView.updateData(newCollectionChannels);
                Log.i("MainActivityPresenter update", newCollectionChannels.size() + " group");
            }
        });
    }


    public void login() {
        final AccountPreferences accountSp = new AccountPreferences(context);
        if (!TextUtils.isEmpty(accountSp.getUserAccount()) && !TextUtils.isEmpty(accountSp.getUserPsw())) {
            AVUser.logInInBackground(accountSp.getUserAccount(), accountSp.getUserPsw(), new LogInCallback() {
                public void done(AVUser user, AVException e) {
                    mainActivityView.login(e);
                }
            });
        } else {        //账号未缓存，重新为其创建一个
            final AVUser user = new AVUser();
            final String strPsw = "f32@ds*@&dsa";
            user.setUsername("乔布斯" + Calendar.getInstance().getTimeInMillis());
            user.setPassword(strPsw);

//        Log.i("MainActivityPresenter", (AVUser.getCurrentUser() == null) + "");

            user.signUpInBackground(new SignUpCallback() {
                public void done(AVException e) {
                    mainActivityView.login(e);
                    if (e == null) {
                        accountSp.setUserAccount(AVUser.getCurrentUser().getUsername());
                        accountSp.setUserPsw(strPsw);
                    }
                }
            });
        }
    }

    /**
     * 从全部频道中获取收藏频道
     *
     * @param channels
     * @return
     */
    private ArrayList<ChannelModule> filterCollectionChannel(ArrayList<ChannelModule> channels) {
        ArrayList<ChannelModule> collectionChannels = new ArrayList<>();
        for (ChannelModule c : channels) {
            if (c.isCollected()) {
                collectionChannels.add(c);
            }
        }
        return collectionChannels;
    }

    private void updateSqlChannel(ArrayList<ChannelModule> channels) {
        ArrayList<ChannelModule> tmpChannels;
        for (ChannelModule c : channels) {
            long tmpId = SqlUtil.getUniqueIdByChannelUri(c.getUri());
            tmpChannels = SqlChannelManager.getInstance().queryChannel(null, ChannelColumn.CHANNEL_ID + "=" + tmpId, null, null, null, null);
            if (tmpChannels != null && tmpChannels.size() > 0) {
                c.setIsCollected(tmpChannels.get(0).isCollected());
                //数据库中已存有，则更新除 收藏 外的其他属性
                ContentValues values = new ContentValues();
                values.put(ChannelColumn.CHANNEL_NAME, c.getChannelName());
                values.put(ChannelColumn.IMG_URI, c.getUri());
                values.put(ChannelColumn.IS_COLLECTION, tmpChannels.get(0).isCollected());
                SqlChannelManager.getInstance().upDateChannel(values, ChannelColumn.CHANNEL_ID + " = " + tmpId, null);

                c.setSqlId(tmpId);
            } else {
                //还未存在，新建
                SqlChannelManager.getInstance().addChannel(c);
            }
        }
    }

    public void createChatroom(final String chatroomName, final AVIMConversationCreatedCallback conversationCreatedCallback) {
        //由local用户创建聊天室
        //TODO: BUG 无法创建聊天室
        final AVIMClient local = AVIMClient.getInstance(AVUser.getCurrentUser().getObjectId());
        local.open(new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                if (e == null) {
                    local.createConversation(Collections.<String>emptyList(), chatroomName,
                            null, true, true, conversationCreatedCallback);
                }
            }
        });
//获取聊天室名称:
//            ChannelModule item = allChannels.get(i);
//            name=item.getUri().substring(item.getUri().lastIndexOf('/') + 1, item.getUri().length() - 5);
//            LogUtil.d("chatRoom",name);
//
    }
}
