package com.bigheart.byrtv.ui.presenter;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.view.WindowManager;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.bigheart.byrtv.ByrTvApplication;
import com.bigheart.byrtv.R;
import com.bigheart.byrtv.data.sharedpreferences.DanmuPreferences;
import com.bigheart.byrtv.ui.module.ChannelModule;
import com.bigheart.byrtv.ui.view.TvLiveActivityView;
import com.bigheart.byrtv.util.LogUtil;

import java.util.List;

import io.vov.vitamio.MediaPlayer;

/**
 * Created by BigHeart on 15/12/10.
 */
public class TvLivePresenter extends Presenter {

    private Context context;
    private TvLiveActivityView view;
    private boolean hasJoinRoom = false;


    public TvLivePresenter(Context c, TvLiveActivityView tvLiveActivityView) {
        context = c;
        view = tvLiveActivityView;
    }

    @Override
    public void init() {
        super.init();
        DanmuPreferences pref = new DanmuPreferences(context);
        view.setDanmuEtPos(pref.getDanmuEtPos());
        view.setDanmuEtTextColorPos(pref.getDanmuEtColorPos());
        view.setDanmuEtTextSize(pref.getDanmuEtTextSize());
        view.setDanmuSBProgress(pref.getDanmuTextScale(), pref.getDanmuSpeed(), pref.getDanmuAlpha(), pref.getDanmuDestiny());
    }

    public boolean isHasJoinRoom() {
        return hasJoinRoom;
    }


    public void tvPlayError(MediaPlayer mp, int what, int extra) {

    }

    public void tvPlayBuffering() {

    }

    public void tvPlayInfo() {

    }

    public void getChannelConversation() {

    }

    /**
     * 调节音量
     *
     * @param dis 可正可负，表示调节差值
     * @return 调节后的音量
     */
    public int adjustVolume(float dis) {
        AudioManager audioMgr = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
        if (dis > 0) {
            audioMgr.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_RAISE, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        } else if (dis < 0) {
            audioMgr.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_LOWER, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        }
        return audioMgr.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     * 调节屏幕亮度
     *
     * @param activity
     * @param dis      可正可负，表示调节屏幕亮度 差值
     * @return 调节后的屏幕亮度
     */
    public float adjustBrightness(Activity activity, float dis) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.screenBrightness = lp.screenBrightness + dis / 100.0f;
        if (lp.screenBrightness > 1) {
            lp.screenBrightness = 1;
        } else if (lp.screenBrightness < 0.01) {
            lp.screenBrightness = 0.01f;
        }
        activity.getWindow().setAttributes(lp);
//        toast(String.format("亮度：%.0f", lp.screenBrightness * 100));

        return lp.screenBrightness * 100;
    }


    /**
     * 加入聊天室
     *
     * @param channel
     */
    public void joinChatRoom(ChannelModule channel) {
        final AVIMConversation cv = channel.getConversation();
        if (cv == null) {
            if (!ByrTvApplication.isGetAVIMClient()) {
                if (AVUser.getCurrentUser() != null) {//只处理有 登录记录 的情况
                    joinRoomWithUserId(AVUser.getCurrentUser().getObjectId(), channel);
                }
            } else {
                queryCvAndJoin(channel);
            }
        } else {
            joinAVIMConversation(cv);
        }
    }

    private void joinRoomWithUserId(String id, final ChannelModule channel) {
        ByrTvApplication.avimClient = AVIMClient.getInstance(id);
        ByrTvApplication.avimClient.open
                (new AVIMClientCallback() {
                     @Override
                     public void done(AVIMClient client, AVIMException e) {
                         if (e == null) {
                             LogUtil.d("TvLiveActivity", "get client");
                             ByrTvApplication.avimClient = client;
                             queryCvAndJoin(channel);
                         } else {
                             e.printStackTrace();
                             Toast.makeText(context, context.getResources().getString(R.string.net_wrong), Toast.LENGTH_SHORT).show();
                         }
                     }
                 }

                );
    }

    private void queryCvAndJoin(final ChannelModule c) {
        AVIMConversationQuery query = ByrTvApplication.avimClient.getQuery();
        query.whereEqualTo("name", c.getServerName());
        query.setLimit(1);
        query.findInBackground(new AVIMConversationQueryCallback() {
            @Override
            public void done(List<AVIMConversation> convs, AVIMException e) {
                if (e == null) {
                    if (convs != null && !convs.isEmpty()) {
                        joinAVIMConversation(convs.get(0));
                        c.setConversation(convs.get(0));
                        LogUtil.d("TvLiveActivity", "get " + c.getServerName() + " cv");
                    }
                }
            }
        });
    }

    private void joinAVIMConversation(final AVIMConversation cv) {
        cv.join(new AVIMConversationCallback() {
            @Override
            public void done(AVIMException e) {
                if (e == null) {
                    LogUtil.d("TvLiveActivity", "join " + cv.getName() + " cv");
                    LogUtil.d(cv.getName() + " Members().size()", cv.getMembers().size() + "");
                    hasJoinRoom = true;
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
