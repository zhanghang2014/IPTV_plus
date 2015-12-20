package com.bigheart.byrtv.ui.presenter;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FunctionCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SignUpCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.bigheart.byrtv.ByrTvApplication;
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
import com.bigheart.byrtv.util.ByrTvUtil;
import com.bigheart.byrtv.util.LogUtil;
import com.bigheart.byrtv.util.SortByPinYin;
import com.bigheart.byrtv.util.SqlUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


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
    private static HashMap<String, ChannelModule> mapChannels;

    private boolean hadSetDataFromNet = false;
    private Handler handler;

    public MainActivityPresenter(Context c, MainActivityView view, MyCollectionView myCollectionView, AllChannelView allChannelView) {
        context = c;
        mainActivityView = view;
        collectionView = myCollectionView;
        channelView = allChannelView;

        ByrTvApplication.resetGetPeopleNumState();

        handler = new Handler();
        allChannels = new ArrayList<>();
        mapChannels = new HashMap<>();
    }

    public void checkUpdate() {
        AVCloud.callFunctionInBackground("appUpdate", null, new FunctionCallback() {
            @Override
            public void done(Object o, AVException e) {
                if (e == null) {
                    if (String.valueOf(ByrTvUtil.getVersionCode(context)) != ((List) o).get(0)) {
                        mainActivityView.showUpdateDialog(((List<String>) o).get(0), ((List<String>) o).get(1), ((List<String>) o).get(2));
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void pullData() {
        collectionView.startRefresh();
        channelView.startRefresh();


        new GetChannelList(new ChannelsRsp() {
            @Override
            public void getFromSqLiteSuccess(final ArrayList<ChannelModule> channels) {
                if (channels.size() > 0) {
                    allChannels = channels;
                    shoveChannelsToMap(mapChannels, allChannels);

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
                shoveChannelsToMap(mapChannels, allChannels);

                ByrTvApplication.updateReadGetPeopleNumState(true, true, true, ByrTvApplication.updateWhat.updatePullChannelState);
                LogUtil.d("getFromNetSuccess", "getFromNetSuccess");
                upDateChatRoomNum(true);


                LogUtil.i("All Channel net", channels.size() + " group");
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
                LogUtil.d("getFromNetError", "getFromNetError");
                upDateChatRoomNum(true);


                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        channelView.stopRefresh();
                        collectionView.stopRefresh();

                        ByrTvApplication.updateReadGetPeopleNumState(true, true, true, ByrTvApplication.updateWhat.updatePullChannelState);

                        Toast.makeText(context, R.string.net_wrong, Toast.LENGTH_SHORT).show();
                        Log.i("MainActivityPresenter", "can not get channel from net");
                    }
                });
            }
        }).getChannels();
    }

    public static ChannelModule getAllChannelByName(String channelName) {
        return mapChannels.get(channelName);
    }

    /**
     * 更新MyCollectionFrg中的数据
     */

    public void upDateMyCollectionFrg() {
        final ArrayList<ChannelModule> newCollectionChannels = filterCollectionChannel(allChannels);
        Collections.sort(newCollectionChannels, new SortByPinYin());
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

    private int serverRoomCount, hadServerRoomUpdateCount;


    /**
     * 实例化 AVIMClient
     */
    public void instanceAVIMClient() {
        ByrTvApplication.avimClient = AVIMClient.getInstance(AVUser.getCurrentUser().getObjectId());
        ByrTvApplication.avimClient.open(new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                if (e == null) {
                    ByrTvApplication.avimClient = avimClient;
                    ByrTvApplication.updateReadGetPeopleNumState(true, true, true, ByrTvApplication.updateWhat.updateAVIMClientState);
                    LogUtil.d("instanceAVIMClient", "instanceAVIMClient");
                    upDateChatRoomNum(true);

                }
            }
        });
    }

    /**
     * 更新聊天室人数
     *
     * @param isNeedToCreateRoom 是否需要建立 chat room
     */
    private synchronized void upDateChatRoomNum(final boolean isNeedToCreateRoom) {

        if (ByrTvApplication.isReadGetPeopleNum()) {
            //仅当 登录 且 尝试请求频道数据 后才执行
            AVIMConversationQuery query = ByrTvApplication.avimClient.getQuery();
            query.whereGreaterThan("name", "");//查询全部
            query.setQueryPolicy(AVQuery.CachePolicy.NETWORK_ONLY);
            query.setLimit(100000000);
            query.findInBackground(new AVIMConversationQueryCallback() {
                @Override
                public void done(List<AVIMConversation> convs, AVIMException e) {
                    if (e == null) {
                        if (convs != null) {
                            LogUtil.d("upDateChatRoomNum", convs.size() + "");
                            serverRoomCount = convs.size();
                            hadServerRoomUpdateCount = 0;
                            if (serverRoomCount > 0) {
                                for (int roomIndex = 0; roomIndex < convs.size(); roomIndex++) {
                                    final AVIMConversation cv = convs.get(roomIndex);
//                                    LogUtil.d(cv.getName() + " Members().size()", cv.getMembers().size() + "");
                                    //update channel
                                    ChannelModule cm = mapChannels.get(cv.getName());
                                    if (cm != null) {
                                        cm.setIsExistInServer(true);
                                        cm.setConversation(cv);
                                    } else {
                                        LogUtil.d("setPeopleNumToMap", "惊现服务器存在，本地不存在的聊天室");
                                    }

                                    if (isNeedToCreateRoom) {
                                        createChatRoom();
                                    }
                                    // TODO: 15/12/16 一次把全部更新，开的线程过多
//                                    cv.getMemberCount(new AVIMConversationMemberCountCallback() {
//                                        @Override
//                                        public void done(Integer count, AVIMException e) {
//                                            if (e == null) {
//                                                setPeopleNumToMap(cv.getName(), count);
//                                                LogUtil.d(cv.getName(), "conversation got " + count + " members");
//                                            } else {
//                                                e.printStackTrace();
//                                            }
//                                        }
//                                    });
                                }
                            } else {
                                LogUtil.d("upDateChatRoomNum", convs.size() + "");
                                createChatRoom();
                            }
                        }
                    }
                }
            });
        }
    }


    private void createChatRoom() {
        hadServerRoomUpdateCount++;
//        LogUtil.d("hadServerRoomUpdateCount", hadServerRoomUpdateCount + "");
        if (hadServerRoomUpdateCount >= serverRoomCount) {//已有的聊天室的人数均已加载完毕
            Iterator createI = mapChannels.entrySet().iterator();
            int i = 0;
            while (createI.hasNext()) {
                ChannelModule c = (ChannelModule) ((Map.Entry) createI.next()).getValue();
                if (c != null && !c.isExistInServer()) {
                    i++;
//                    LogUtil.d("ExistInServer", c.getChannelName());
                    ByrTvApplication.avimClient.createConversation(new ArrayList<String>(), c.getServerName(), null, true, true,
                            new AVIMConversationCreatedCallback() {
                                @Override
                                public void done(AVIMConversation conv, AVIMException e) {
                                    if (e == null) {
                                        LogUtil.d("createChatRoom", conv.getName());
                                    } else {
                                        e.printStackTrace();
                                    }
                                }
                            });
//                    if (i > 20) {
//                        break;
//                    }
                }
            }
            LogUtil.d("createChatRoomNum", i + "");
        }

    }


    /**
     * 从全部频道中获取收藏频道
     *
     * @param channels
     * @return
     */
    private ArrayList<ChannelModule> filterCollectionChannel
    (ArrayList<ChannelModule> channels) {
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

    /**
     * 将 频道 填充为 Map
     *
     * @param map
     * @param channels
     */
    private void shoveChannelsToMap(HashMap<String, ChannelModule> map, ArrayList<ChannelModule> channels) {
        map.clear();
        for (ChannelModule c : channels) {
            String uri = c.getUri();
            c.setServerName(uri.substring(uri.lastIndexOf('/') + 1, uri.length() - 5));
            map.put(c.getServerName(), c);
        }
    }

    private synchronized void setPeopleNumToMap(String roomName, int num) {
        ChannelModule cm = mapChannels.get(roomName);
        if (cm != null) {
            cm.setPeopleNum(num);
        } else {
            LogUtil.d("setPeopleNumToMap", "服务器存在，本地不存在的聊天室");
        }
    }

    public Uri getIconUri() {
        Uri uri = null;
        AVUser user = AVUser.getCurrentUser();
        if (user != null) {
            AVFile file = user.getAVFile("avator");
            if (file != null) {
                uri = Uri.parse(file.getUrl());
            }
        }
        return uri;
    }

    public String getNickname() {
        AVUser user = AVUser.getCurrentUser();
        if (user != null) {
            return user.getString("nickname");
        } else {
            return null;
        }
    }

    public void reLogin(LogInCallback callback) {
        AccountPreferences sp = new AccountPreferences(context);
        AVUser.logInInBackground(sp.getUserAccount(), sp.getUserPsw(), callback);
    }

}
