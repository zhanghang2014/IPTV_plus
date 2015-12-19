package com.bigheart.byrtv.ui.view.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageHandler;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.bigheart.byrtv.ByrTvApplication;
import com.bigheart.byrtv.R;
import com.bigheart.byrtv.data.sharedpreferences.DanmuPreferences;
import com.bigheart.byrtv.ui.module.ChannelModule;
import com.bigheart.byrtv.ui.presenter.MainActivityPresenter;
import com.bigheart.byrtv.ui.presenter.TvLivePresenter;
import com.bigheart.byrtv.ui.view.TvLiveActivityView;
import com.bigheart.byrtv.util.ByrTvUtil;
import com.bigheart.byrtv.util.LogUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;
import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;


/**
 * Created by BigHeart on 15/12/10.
 */
public class TvLiveActivity extends BaseActivity implements TvLiveActivityView {

    public static final String TV_SERVER_NAME = "tv_server_name";

    private final int OPTION_VOLUME = 0, OPTION_BRIGHTNESS = 1;
    private final float THRESHOLD = ByrTvUtil.dip2px(5f);
    private float adjustY = 0;
    private int adjustOption = OPTION_VOLUME;
    private ChannelModule channel;
    private boolean isLockScreen = false;

    //弹幕偏好
    private int danmuEtTextSize = 0, danmuEtColorPos = 0, danmuEtPos = 0;


    private VideoView mVideoView;
    private RelativeLayout rlVvTop, rlVvBottom;
    private Button btLaunchDanmu, btLockScreen, btDanmuSwitch;
    private ImageView ivPlayOrPause, ivUnlockScreenLogo;
    private TextView tvChannelName, tvBufferInfo;

    private IDanmakuView danmakuView;
    private DanmakuContext danmakuContext;
    private BaseDanmakuParser danmakuParser;

    private DanmuPreferences danmuPreferences;


    private PopupWindow popupMoreSetting;

    //子菜单控件
    //发射弹幕控件
    private LinearLayout llDanmuEdit;
    private EditText etWriteDanmu;
    private ImageButton ibLaunchDanmu;
    private ImageView ivBigText, ivSmallText, ivDanmuInTop, ivDanmuInBottom, ivDanmuFlow, ivColor0, ivColor1, ivColor2, ivColor3;

    //弹幕设置 子菜单
    private LinearLayout llDanmuSetting;
    private ImageView ivFilterTopDanmu, ivFilterBottomDanmu, ivFilterFlowDanmu, ivFilterColorDanmu;
    private SeekBar sbTextScale, sbDestiny, sbSpeed, sbAlpha;
    private boolean isFilterColorDanmu = false, isFilterTopDanmu = false, isFilterFlowDanmu = false, isFilterBottomDanmu = false;

    //弹幕屏蔽
    private LinearLayout llFilterUser;
    private ListView lvFilter;
    private FilterAdapter filterAdapter;
    private Button btFilter;
    Queue<FilterItem> qFilter;

    private class FilterItem {
        FilterItem(String content, String senderId, boolean isCheck) {
            danmuContent = content;
            this.isCheck = isCheck;
            this.senderId = senderId;
        }

        String danmuContent, senderId;
        boolean isCheck;
    }

    private TvLivePresenter presenter;
    private MediaController mediaController;


    /**
     * 监听分钟量级的变化，更新时间
     */
    private BroadcastReceiver minBroadcast = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //update time every minute
            ((TextView) findViewById(R.id.tv_vv_time)).setText(new SimpleDateFormat("hh:mm").format(System.currentTimeMillis()));
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏状态栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.layout_tv_live);
        Vitamio.isInitialized(this);
        initUI();
        initData();


//        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
//        exec.scheduleAtFixedRate(new Runnable() {
//            public void run() {
//                addDanmu("四不四洒！！！", false);
//            }
//        }, 0, 800, TimeUnit.MILLISECONDS);

    }


    @Override
    protected void onPause() {
        super.onPause();
        if (danmakuView != null && danmakuView.isPrepared()) {
            danmakuView.pause();
        }
//        AVIMMessageManager.unregisterMessageHandler(DanmuTextMessage.class, messageHandler);
        AVIMMessageManager.unregisterMessageHandler(AVIMTextMessage.class, messageHandler);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mVideoView.isPlaying()) {
            mVideoView.start();
        }
        if (danmakuView != null && danmakuView.isPrepared() && danmakuView.isPaused()) {
            danmakuView.resume();
        }
        AVIMMessageManager.registerMessageHandler(AVIMTextMessage.class, messageHandler);
//        AVIMMessageManager.registerDefaultMessageHandler(messageHandler);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
        if (minBroadcast.isOrderedBroadcast()) {
            getApplicationContext().unregisterReceiver(minBroadcast);
        }
        if (danmakuView != null) {
            danmakuView.release();
            danmakuView = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (danmakuView != null) {
            danmakuView.release();
            danmakuView = null;
        }
    }

    @Override
    public void setDanmuEtTextSize(int size) {
        danmuEtTextSize = size;
        danmuPreferences.setDanmuTextEtSize(size);
        ivBigText.setImageDrawable(null);
        ivSmallText.setImageDrawable(null);
        if (size == TvLiveActivity.this.DANMU_ET_BIG_SIZE_TEXT) {
            setBeSelected(ivBigText);
        } else {
            setBeSelected(ivSmallText);
        }
    }

    @Override
    public void setDanmuEtTextColorPos(int colorPos) {
        danmuEtColorPos = colorPos;
        ivColor0.setImageDrawable(null);
        ivColor1.setImageDrawable(null);
        ivColor2.setImageDrawable(null);
        ivColor3.setImageDrawable(null);
        switch (colorPos) {
            case 0:
                setBeSelected(ivColor0);
                break;
            case 1:
                setBeSelected(ivColor1);
                break;
            case 2:
                setBeSelected(ivColor2);
                break;
            case 3:
                setBeSelected(ivColor3);
                break;
        }
        danmuPreferences.setDanmuColorEtPos(danmuEtColorPos);
    }

    @Override
    public void setDanmuEtPos(int pos) {
        danmuEtPos = pos;
        ivDanmuInTop.setImageDrawable(null);
        ivDanmuInBottom.setImageDrawable(null);
        ivDanmuFlow.setImageDrawable(null);
        danmuPreferences.setDanmuEtPos(pos);
        if (pos == TvLiveActivity.this.DANMU_TEXT_TOP) {
            setBeSelected(ivDanmuInTop);
        } else if (pos == TvLiveActivity.this.DANMU_TEXT_BOTTOM) {
            setBeSelected(ivDanmuInBottom);
        } else {
            //flow
            setBeSelected(ivDanmuFlow);
        }
    }

    private void setBeSelected(ImageView iv) {
        iv.setImageDrawable(getResources().getDrawable(R.drawable.state_selected_outline));
    }

    private void initUI() {
        mVideoView = (VideoView) findViewById(R.id.vv_tv_live);
        danmakuView = (IDanmakuView) findViewById(R.id.dmk_view_live);


        findViewById(R.id.rl_vv_control).setOnClickListener(mainCtlClickListener);

        //主菜单控件
        //top
        rlVvTop = (RelativeLayout) findViewById(R.id.rl_vv_top);
        tvChannelName = (TextView) findViewById(R.id.tv_vv_channel_name);
        findViewById(R.id.iv_vv_back).setOnClickListener(mainCtlClickListener);
        findViewById(R.id.iv_vv_danmu_setting).setOnClickListener(mainCtlClickListener);
        findViewById(R.id.iv_vv_filter_user).setOnClickListener(mainCtlClickListener);
        //center
        tvBufferInfo = (TextView) findViewById(R.id.tv_vv_buffer_info);
        ivUnlockScreenLogo = (ImageView) findViewById(R.id.iv_vv_unlock_screen);
        ivUnlockScreenLogo.setOnClickListener(mainCtlClickListener);
        //bottom
        rlVvBottom = (RelativeLayout) findViewById(R.id.rl_vv_bottom);
        btLockScreen = (Button) findViewById(R.id.bt_vv_lock_screen);
        btLockScreen.setOnClickListener(mainCtlClickListener);
        btDanmuSwitch = (Button) findViewById(R.id.bt_vv_danmu_switch);
        btDanmuSwitch.setOnClickListener(mainCtlClickListener);
        btLaunchDanmu = (Button) findViewById(R.id.bt_vv_launch_danmu);
        btLaunchDanmu.setOnClickListener(mainCtlClickListener);
        ivPlayOrPause = (ImageView) findViewById(R.id.iv_vv_play_pause);
        ivPlayOrPause.setOnClickListener(mainCtlClickListener);

        //子菜单
        // 发射弹幕菜单控件
        llDanmuEdit = (LinearLayout) findViewById(R.id.ll_danmu_edit);
        etWriteDanmu = (EditText) findViewById(R.id.et_write_danmu);
        ibLaunchDanmu = (ImageButton) findViewById(R.id.ib_launch_danmu);
        ivBigText = (ImageView) findViewById(R.id.iv_vv_big_text);
        ivSmallText = (ImageView) findViewById(R.id.iv_vv_small_text);
        ivDanmuInTop = (ImageView) findViewById(R.id.iv_vv_text_still_top);
        ivDanmuInBottom = (ImageView) findViewById(R.id.iv_vv_text_still_bottom);
        ivDanmuFlow = (ImageView) findViewById(R.id.iv_vv_text_flow);
        ivColor0 = (ImageView) findViewById(R.id.iv_vv_color_0);
        ivColor1 = (ImageView) findViewById(R.id.iv_vv_color_1);
        ivColor2 = (ImageView) findViewById(R.id.iv_vv_color_2);
        ivColor3 = (ImageView) findViewById(R.id.iv_vv_color_3);

        ibLaunchDanmu.setOnClickListener(editDanmuClickListen);
        ivBigText.setOnClickListener(editDanmuClickListen);
        ivSmallText.setOnClickListener(editDanmuClickListen);
        ivDanmuInTop.setOnClickListener(editDanmuClickListen);
        ivDanmuInBottom.setOnClickListener(editDanmuClickListen);
        ivDanmuFlow.setOnClickListener(editDanmuClickListen);
        ivColor0.setOnClickListener(editDanmuClickListen);
        ivColor1.setOnClickListener(editDanmuClickListen);
        ivColor2.setOnClickListener(editDanmuClickListen);
        ivColor3.setOnClickListener(editDanmuClickListen);


        //弹幕设置
        llDanmuSetting = (LinearLayout) findViewById(R.id.ll_iv_danmu_setting);
        ivFilterTopDanmu = (ImageView) findViewById(R.id.iv_vv_filter_top);
        ivFilterBottomDanmu = (ImageView) findViewById(R.id.iv_vv_filter_bottom);
        ivFilterFlowDanmu = (ImageView) findViewById(R.id.iv_vv_filter_flow);
        ivFilterColorDanmu = (ImageView) findViewById(R.id.iv_vv_filter_color);
        sbTextScale = (SeekBar) findViewById(R.id.sb_text_scale);
        sbAlpha = (SeekBar) findViewById(R.id.sb_danmu_aphla);
        sbDestiny = (SeekBar) findViewById(R.id.sb_danmu_destiny);
        sbSpeed = (SeekBar) findViewById(R.id.sb_danmu_speed);

        ivFilterTopDanmu.setOnClickListener(danmuSettingClickListener);
        ivFilterBottomDanmu.setOnClickListener(danmuSettingClickListener);
        ivFilterFlowDanmu.setOnClickListener(danmuSettingClickListener);
        ivFilterColorDanmu.setOnClickListener(danmuSettingClickListener);
        sbAlpha.setOnSeekBarChangeListener(userSettingSbChangeListener);
        sbDestiny.setOnSeekBarChangeListener(userSettingSbChangeListener);
        sbSpeed.setOnSeekBarChangeListener(userSettingSbChangeListener);
        sbTextScale.setOnSeekBarChangeListener(userSettingSbChangeListener);
        llDanmuSetting.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        //弹幕过滤
        llFilterUser = (LinearLayout) findViewById(R.id.ll_vv_filter_user);
        btFilter = (Button) findViewById(R.id.bt_vv_fliter_users);
        lvFilter = (ListView) findViewById(R.id.lv_vv_danmu_users);

        btFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 15/12/18 可以优化,减少循环

                Iterator<FilterItem> iterator = qFilter.iterator();
                boolean hasRemove = false;
                ArrayList<FilterItem> listFilter = new ArrayList<>();
                while (iterator.hasNext()) {
                    FilterItem f = iterator.next();
                    if (f.isCheck) {
                        listFilter.add(f);
                        hasRemove = true;
                    }
                }
                if (hasRemove) {
                    Iterator<FilterItem> listIterator = listFilter.iterator();
                    while (listIterator.hasNext()) {
                        FilterItem f2 = listIterator.next();
                        qFilter.remove(f2);
                        danmakuContext.addUserHashBlackList(f2.senderId);
                    }
                    filterAdapter.notifyDataSetChanged();
                }
            }
        });
    }


    // TODO: 15/12/18 每次发送都要判断是否登录
    private boolean isJoinThisRoom = false;

    private void initConv() {
        AVIMConversation cv = channel.getConversation();
        if (cv == null) {
            if (!ByrTvApplication.isGetAVIMClient()) {
                if (AVUser.getCurrentUser() != null) {//只处理有 登录记录 的情况
                    ByrTvApplication.avimClient = AVIMClient.getInstance(AVUser.getCurrentUser().getObjectId());
                    ByrTvApplication.avimClient.open
                            (new AVIMClientCallback() {
                                 @Override
                                 public void done(AVIMClient client, AVIMException e) {
                                     if (e == null) {
                                         LogUtil.d("TvLiveActivity", "get client");
                                         ByrTvApplication.avimClient = client;

                                         //可复用
                                         AVIMConversationQuery query = ByrTvApplication.avimClient.getQuery();
                                         query.whereEqualTo("name", channel.getServerName());
                                         query.setLimit(1);
                                         query.findInBackground(new AVIMConversationQueryCallback() {
                                             @Override
                                             public void done(List<AVIMConversation> convs, AVIMException e) {
                                                 if (e == null) {
                                                     if (convs != null && !convs.isEmpty()) {
                                                         convs.get(0).join(new AVIMConversationCallback() {
                                                             @Override
                                                             public void done(AVIMException e) {
                                                                 if (e == null) {
                                                                     LogUtil.d("TvLiveActivity", "join " + channel.getServerName() + " cv");
                                                                     isJoinThisRoom = true;
//                                                                     isInList();
                                                                 } else {
                                                                     e.printStackTrace();
                                                                 }
                                                             }
                                                         });
                                                         channel.setConversation(convs.get(0));
                                                         LogUtil.d("TvLiveActivity", "get " + channel.getServerName() + " cv");
                                                     }
                                                 }
                                             }
                                         });

                                     } else {
                                         e.printStackTrace();
                                         toast(getResources().getString(R.string.net_wrong));
                                     }
                                 }
                             }

                            );
                }
            } else {
                AVIMConversationQuery query = ByrTvApplication.avimClient.getQuery();
                query.whereEqualTo("name", channel.getServerName());
                query.setLimit(1);
                query.findInBackground(new AVIMConversationQueryCallback() {
                    @Override
                    public void done(List<AVIMConversation> convs, AVIMException e) {
                        if (e == null) {
                            if (convs != null && !convs.isEmpty()) {
                                convs.get(0).join(new AVIMConversationCallback() {
                                    @Override
                                    public void done(AVIMException e) {
                                        if (e == null) {
                                            LogUtil.d("TvLiveActivity", "join " + channel.getServerName() + " cv");
                                            isJoinThisRoom = true;
                                        } else {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                channel.setConversation(convs.get(0));
                                LogUtil.d("TvLiveActivity", "get " + channel.getServerName() + " cv");
                            }
                        }
                    }
                });
            }
        } else {
            cv.join(new AVIMConversationCallback() {
                @Override
                public void done(AVIMException e) {
                    if (e == null) {
                        LogUtil.d("TvLiveActivity", "join " + channel.getServerName() + " cv");
                        isJoinThisRoom = true;
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void initData() {

        if (getIntent().hasExtra(TV_SERVER_NAME)) {
            channel = MainActivityPresenter.getAllChannelByName(getIntent().getStringExtra(TV_SERVER_NAME));
        } else {
            finish();
        }

        initConv();


        //先设置一次时间
        ((TextView) findViewById(R.id.tv_vv_time)).setText(new SimpleDateFormat("hh:mm").format(System.currentTimeMillis()));
        //绑定 Receiver
        IntentFilter updateIntent = new IntentFilter();
        updateIntent.addAction("android.intent.action.TIME_TICK");
        getApplicationContext().registerReceiver(minBroadcast, updateIntent);

        danmuPreferences = new DanmuPreferences(this);
        presenter = new TvLivePresenter(this, this);
        presenter.init();


        tvChannelName.setText(channel.getChannelName());


        //视频控件设置
//        Log.i(TV_LIVE_URI, channelUri + " " + channelUri.length());
        mVideoView.setVideoPath(channel.getUri());
//        this.toast(channelUri);
        mediaController = new MediaController(this);
        mVideoView.setMediaController(mediaController);
        mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);
//        mVideoView.requestFocus();
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setPlaybackSpeed(1.0f);

                //调整宽高
                int videoH = mediaPlayer.getVideoHeight();
                int videoW = mediaPlayer.getVideoWidth();
                Log.i("TvLiveActivity video", videoH + " " + videoW);
                if (videoH > ByrTvUtil.getScreenWidth() || videoW > ByrTvUtil.getScreenHeight()) {
                    float wRatio = (float) videoW / (float) ByrTvUtil.getScreenWidth();
                    float hRatio = (float) videoH / (float) ByrTvUtil.getScreenHeight();
                    float ratio = Math.max(wRatio, hRatio);
                    //获取缩放比例
                    Log.i("TvLiveActivity ratio", ratio + "");
                    videoW = (int) Math.ceil((float) videoW / ratio);
                    videoH = (int) Math.ceil((float) videoH / ratio);
                    mVideoView.setLayoutParams(new RelativeLayout.LayoutParams(videoW, videoH));
                    Log.i("TvLiveActivity device", ByrTvUtil.getScreenHeight() + " " + ByrTvUtil.getScreenWidth());
                    Log.i("TvLiveActivity change", videoH + " " + videoW);
                }
                mVideoView.setBufferSize(512 * 1024);
                ivPlayOrPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_fill_white_24dp));
            }
        });

        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                // extra : https://github.com/yixia/VitamioBundle/wiki/ErrorCode
                // TODO: 15/12/11 加弹窗
                LogUtil.e("TvLiveActivity", "setOnErrorListener");
                toast("视频播放错误！");
                return false;
            }
        });
        mVideoView.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                LogUtil.d("TvLiveActivity", percent + "%");
                if (percent < 100) {
                    if (tvBufferInfo.getVisibility() != View.VISIBLE) {
                        tvBufferInfo.setVisibility(View.VISIBLE);
                    }
                    tvBufferInfo.setText("缓冲" + percent + "%");
                } else {
                    tvBufferInfo.setText("");
                    tvBufferInfo.setVisibility(View.INVISIBLE);
                }
            }
        });

        mVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
//                LogUtil.d("TvLiveActivity info", what + " " + extra);
                return false;
            }
        });


        //弹幕参数设置
        danmakuParser = new BaseDanmakuParser() {
            @Override
            protected Danmakus parse() {
                return new Danmakus();
            }
        };
        danmakuContext = DanmakuContext.create();
        HashMap<Integer, Integer> maxLineLimit = new HashMap<>();
        maxLineLimit.put(BaseDanmaku.TYPE_SCROLL_RL, 5);
        danmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3)
                .setDuplicateMergingEnabled(false)
                .setScrollSpeedFactor(1.2f)
                .setScaleTextSize(1.2f)
                .setMaximumLines(maxLineLimit);
        danmakuView.setCallback(new DrawHandler.Callback() {
            @Override
            public void prepared() {
                danmakuView.start();
            }

            @Override
            public void updateTimer(DanmakuTimer timer) {

            }

            @Override
            public void drawingFinished() {

            }
        });
        danmakuView.prepare(danmakuParser, danmakuContext);
//        danmakuView.showFPS(true);


        //用户屏蔽
        qFilter = new LinkedList<>();
        filterAdapter = new FilterAdapter(this);
        lvFilter.setAdapter(filterAdapter);
    }

    private class FilterAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        FilterAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return qFilter.size();
        }

        @Override
        public Object getItem(int position) {
            return ((LinkedList) qFilter).get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_user_filter, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.cbDanmuFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    ((FilterItem) ((LinkedList) qFilter).get(position)).isCheck = isChecked;
                }
            });
            holder.tvDanmuFilter.setText(((FilterItem) ((LinkedList) qFilter).get(position)).danmuContent);

            return convertView;
        }

        class ViewHolder {
            CheckBox cbDanmuFilter;
            TextView tvDanmuFilter;

            ViewHolder(View view) {
                cbDanmuFilter = (CheckBox) view.findViewById(R.id.cb_vv_user_filter);
                tvDanmuFilter = (TextView) view.findViewById(R.id.tv_vv_danmu_filter);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                adjustY = event.getY();
                if (event.getX() < ByrTvUtil.getScreenHeight() / 2.0f) {
                    //左边:亮度
                    adjustOption = OPTION_BRIGHTNESS;
                } else {
                    //右边:声音
                    adjustOption = OPTION_VOLUME;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float y = event.getY();
                float yy = (adjustY - y) / THRESHOLD;
                if (Math.abs(yy) >= 1) {
                    adjustY = y;
                    switch (adjustOption) {
                        case OPTION_BRIGHTNESS:
                            if (!isLockScreen)
                                presenter.adjustBrightness(TvLiveActivity.this, yy);
                            break;
                        case OPTION_VOLUME:
                            if (!isLockScreen)
                                presenter.adjustVolume(yy);
                            break;
                        default:
                            break;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }

        return super.dispatchTouchEvent(event);
    }

    /**
     * 上下控制菜单、屏幕的点击事件
     */
    private View.OnClickListener mainCtlClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_vv_lock_screen:
                    isLockScreen = true;
                    cleanAllMenu();
                    ivUnlockScreenLogo.setVisibility(View.VISIBLE);
                    break;
                case R.id.bt_vv_danmu_switch:
                    if (danmakuView.isShown()) {
                        //关闭弹幕
                        btDanmuSwitch.setCompoundDrawables(null, getResources().getDrawable(android.R.drawable.star_big_off), null, null);
                        danmakuView.hide();
                    } else {
                        //打开弹幕
                        btDanmuSwitch.setCompoundDrawables(null, getResources().getDrawable(android.R.drawable.star_big_on), null, null);
                        danmakuView.show();
                    }
                    break;
                case R.id.bt_vv_launch_danmu:
                    cleanAllMenu();
                    etWriteDanmu.requestFocus();
                    llDanmuEdit.setVisibility(View.VISIBLE);
                    InputMethodManager inputManager = (InputMethodManager) etWriteDanmu.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.showSoftInput(etWriteDanmu, InputMethodManager.SHOW_FORCED);
                    break;
                case R.id.iv_vv_play_pause:
                    if (mVideoView.isPlaying()) {
//                    LogUtil.d("TvLiveActivity", "pause");
                        mVideoView.pause();
                        ivPlayOrPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_fill_white_24dp));
                    } else {
//                    LogUtil.d("TvLiveActivity", "play");
                        mVideoView.start();
                        ivPlayOrPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_fill_white_24dp));
                    }
                    break;
                case R.id.iv_vv_back:
                    if (mVideoView != null) {
                        mVideoView.stopPlayback();
                    }
                    if (danmakuView != null) {
                        danmakuView.release();
                        danmakuView = null;
                    }
                    finish();
                    break;
                case R.id.rl_vv_control:
                    onScreenClicked();
                    break;
                case R.id.iv_vv_unlock_screen:
                    isLockScreen = false;
                    ivUnlockScreenLogo.setVisibility(View.INVISIBLE);
                    break;

                case R.id.iv_vv_filter_user:
                    cleanAllMenu();
                    llFilterUser.setVisibility(View.VISIBLE);
                    filterAdapter.notifyDataSetChanged();
                    LogUtil.d("qFilter.size()", qFilter.size() + "");
                    break;

                case R.id.iv_vv_danmu_setting:
                    cleanAllMenu();
                    llDanmuSetting.setVisibility(View.VISIBLE);
                    break;
                default:
                    LogUtil.d("TvLiveActivity mainCtlClickListener", "未处理监听事件");
                    break;
            }
        }
    };

    /**
     * 弹幕编辑框 点击事件
     */
    private View.OnClickListener editDanmuClickListen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ib_launch_danmu:
                    // TODO: 15/12/17 判断 join 是否成功，作相应处理
                    String content = etWriteDanmu.getText().toString().trim();
                    addDanmuToServer(content);
                    etWriteDanmu.setText("");
                    llDanmuEdit.setVisibility(View.GONE);
                    break;
                case R.id.iv_vv_big_text:
                    TvLiveActivity.this.setDanmuEtTextSize(TvLiveActivity.this.DANMU_ET_BIG_SIZE_TEXT);
                    break;
                case R.id.iv_vv_small_text:
                    TvLiveActivity.this.setDanmuEtTextSize(TvLiveActivity.this.DANMU_ET_SMALL_SIZE_TEXT);
                    break;
                case R.id.iv_vv_text_still_top:
                    TvLiveActivity.this.setDanmuEtPos(TvLiveActivity.this.DANMU_TEXT_TOP);
                    break;
                case R.id.iv_vv_text_flow:
                    TvLiveActivity.this.setDanmuEtPos(TvLiveActivity.this.DANMU_TEXT_FLOW);
                    break;
                case R.id.iv_vv_text_still_bottom:
                    TvLiveActivity.this.setDanmuEtPos(TvLiveActivity.this.DANMU_TEXT_BOTTOM);
                    break;
                case R.id.iv_vv_color_0:
                    TvLiveActivity.this.setDanmuEtTextColorPos(0);
                    break;
                case R.id.iv_vv_color_1:
                    TvLiveActivity.this.setDanmuEtTextColorPos(1);
                    break;
                case R.id.iv_vv_color_2:
                    TvLiveActivity.this.setDanmuEtTextColorPos(2);
                    break;
                case R.id.iv_vv_color_3:
                    TvLiveActivity.this.setDanmuEtTextColorPos(3);
                    break;
                default:
                    LogUtil.d("TvLiveActivity editDanmuClickListen", "未处理监听事件");
                    break;
            }
        }
    };


    /**
     * 弹幕过滤 中的点击事件
     */

    private View.OnClickListener danmuSettingClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_vv_filter_top:
                    isFilterTopDanmu = !isFilterTopDanmu;
                    danmakuContext.setFTDanmakuVisibility(!isFilterTopDanmu);
                    if (isFilterTopDanmu) {
                        //show
                        setBeSelected(ivFilterTopDanmu);
                    } else {
                        //hide
                        ivFilterTopDanmu.setImageDrawable(null);
                    }
                    break;
                case R.id.iv_vv_filter_flow:
                    isFilterFlowDanmu = !isFilterFlowDanmu;
                    danmakuContext.setR2LDanmakuVisibility(!isFilterFlowDanmu);
                    if (isFilterFlowDanmu) {
                        //show
                        setBeSelected(ivFilterFlowDanmu);
                    } else {
                        //hide
                        ivFilterFlowDanmu.setImageDrawable(null);
                    }
                    break;
                case R.id.iv_vv_filter_bottom:
                    isFilterBottomDanmu = !isFilterBottomDanmu;
                    danmakuContext.setFBDanmakuVisibility(!isFilterBottomDanmu);
                    if (isFilterBottomDanmu) {
                        //show
                        setBeSelected(ivFilterBottomDanmu);
                    } else {
                        //hide
                        ivFilterBottomDanmu.setImageDrawable(null);

                    }
                    break;
                case R.id.iv_vv_filter_color:
                    isFilterColorDanmu = !isFilterColorDanmu;
                    if (isFilterColorDanmu) {
                        //only show white color
                        setBeSelected(ivFilterColorDanmu);
                        danmakuContext.setColorValueWhiteList(Color.WHITE);
                    } else {
                        //show all color
//                        danmakuContext.setColorValueWhiteList(0);
                        ivFilterColorDanmu.setImageDrawable(null);
                        danmakuContext.setColorValueWhiteList(TvLiveActivity.this.danmuColor[0], TvLiveActivity.this.danmuColor[1], TvLiveActivity.this.danmuColor[2], TvLiveActivity.this.danmuColor[3]);
                    }
                    break;
                default:
                    break;
            }
        }
    };


    private SeekBar.OnSeekBarChangeListener userSettingSbChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            switch (seekBar.getId()) {
                case R.id.sb_danmu_aphla:
                    //5~255
                    danmakuContext.setDanmakuTransparency(progress * 2.5f + 5);
                    LogUtil.d("setDanmakuTransparency ", progress * 2.5f + 5 + "");
                    break;
                case R.id.sb_danmu_destiny:
                    //3~53
                    danmakuContext.setMaximumVisibleSizeInScreen(progress / 2 + 3);
                    LogUtil.d("setMaximumVisibleSizeInScreen ", progress / 2f + 3 + "");
                    break;
                case R.id.sb_danmu_speed:
                    //0.2~5
                    danmakuContext.setScrollSpeedFactor(progress / 23f + 0.2f);
                    LogUtil.d("setScrollSpeedFactor ", progress / 23f + 0.2f + "");
                    break;
                case R.id.sb_text_scale:
                    //0.5~4
                    danmakuContext.setScaleTextSize(progress / 28.6f + 0.5f);
                    LogUtil.d("setScaleTextSize ", progress / 28.6f + 0.5f + "");
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };


    /**
     * 屏幕被单击
     */
    private void onScreenClicked() {
        if (isLockScreen) {
            if (ivUnlockScreenLogo.isShown()) {
                ivUnlockScreenLogo.setVisibility(View.INVISIBLE);
            } else {
                ivUnlockScreenLogo.setVisibility(View.VISIBLE);
            }
        } else {
            if (rlVvBottom.isShown() || llDanmuEdit.isShown() || llDanmuSetting.isShown() || llFilterUser.isShown()) {
                cleanAllMenu();
            } else {
                showTopBottomMenu();
            }
        }
    }

    /**
     * 清空屏幕上的所有菜单
     */
    private void cleanAllMenu() {
        rlVvBottom.setVisibility(View.GONE);
        rlVvTop.setVisibility(View.GONE);
        llDanmuEdit.setVisibility(View.GONE);
        llDanmuSetting.setVisibility(View.GONE);
        llFilterUser.setVisibility(View.GONE);
    }

    /**
     * 显示上下菜单
     */
    private void showTopBottomMenu() {
        rlVvBottom.setVisibility(View.VISIBLE);
        rlVvTop.setVisibility(View.VISIBLE);
    }


    private final int danmuStayTime = 1200;//弹幕显示时间
    private final float danmuBigTextSize = 25f, danmuSmallTextSize = 15f;

    /**
     * @param content
     * @param attrs
     * @return
     */
    private boolean addDanmu(String content, DanmuAttrs attrs, boolean isFromUser) {
        if (!TextUtils.isEmpty(content) || attrs != null) {
            BaseDanmaku danmaku;
            if (attrs.getShowPos() == TvLiveActivity.this.DANMU_TEXT_TOP) {
                danmaku = danmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_FIX_TOP);
            } else if (attrs.getShowPos() == TvLiveActivity.this.DANMU_TEXT_BOTTOM) {
                danmaku = danmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_FIX_BOTTOM);
            } else {
                danmaku = danmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
            }

            if (danmaku != null && danmakuView != null) {
                danmaku.text = content;
                danmaku.padding = 5;
                danmaku.priority = 0;  // 可能会被各种过滤器过滤并隐藏显示
                danmaku.isLive = true;

                danmaku.time = danmakuView.getCurrentTime() + danmuStayTime;
                if (attrs.getTextSize() == TvLiveActivityView.DANMU_ET_BIG_SIZE_TEXT) {
                    danmaku.textSize = danmuBigTextSize * (danmakuParser.getDisplayer().getDensity() - 0.6f);
                } else {
                    danmaku.textSize = danmuSmallTextSize * (danmakuParser.getDisplayer().getDensity() - 0.6f);
                }
                danmaku.textColor = TvLiveActivity.danmuColor[attrs.getColor()];
                danmaku.textShadowColor = Color.WHITE;
                if (isFromUser) {
                    danmaku.borderColor = Color.GREEN;
                }

                danmakuView.addDanmaku(danmaku);
                danmaku.userHash = attrs.userId;
//                LogUtil.d("addDanmu", "addDanmu success " + danmuEtColorPos + " " + danmuEtTextSize);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public class DanmuAttrs {
        public static final String DANMU_SHOW_POS = "show_pos", DANMU_COLOR = "color", DANMU_TEXT_SIZE = "text_size", DANMU_SENDER_ID = "sender_id";
        private int showPos, color, textSize;
        private String userId;

        public void setShowPos(int showPos) {
            this.showPos = showPos;
        }

        public int getShowPos() {
            return showPos;
        }

        public int getColor() {
            return color;
        }

        public int getTextSize() {
            return textSize;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public void setTextSize(int textSize) {
            this.textSize = textSize;
        }

        DanmuAttrs(int showPos, int color, int textSize, String userId) {
            this.showPos = showPos;
            this.color = color;
            this.textSize = textSize;
            this.userId = userId;
        }
    }

    private boolean addDanmuToServer(String content) {

        addDanmu(content, new DanmuAttrs(danmuEtPos, danmuEtColorPos, danmuEtTextSize, ByrTvApplication.avimClient.getClientId()), true);

//        AVIMTextMessage msg = new AVIMTextMessage();
//        msg.setDanmuText(content);
//        msg.setDanmuAttrs(new DanmuAttrs(danmuEtPos, danmuEtColorPos, danmuEtTextSize, ByrTvApplication.avimClient.getClientId()));
        AVIMTextMessage msg = new AVIMTextMessage();
        Map<String, Object> mapAttrs = new HashMap<>();
        mapAttrs.put(DanmuAttrs.DANMU_TEXT_SIZE, danmuEtTextSize);
        mapAttrs.put(DanmuAttrs.DANMU_COLOR, danmuEtColorPos);
        mapAttrs.put(DanmuAttrs.DANMU_SHOW_POS, danmuEtPos);
        mapAttrs.put(DanmuAttrs.DANMU_SENDER_ID, ByrTvApplication.avimClient.getClientId());
        msg.setAttrs(mapAttrs);
        msg.setText(content);

        // 发送消息
        if (channel.getConversation() != null) {
            channel.getConversation().sendMessage(msg, AVIMConversation.TRANSIENT_MESSAGE_FLAG, new AVIMConversationCallback() {
                @Override
                public void done(AVIMException e) {
                    if (e == null) {
                        LogUtil.d("TvLiveActivity", "addDanmuToServer ！");
                    } else {
                        e.printStackTrace();
                        toast("发送失败");
                    }
                }
            });
        } else {
            LogUtil.e("TvLiveActivity", "addDanmuToServer fail ！getConversation == null");
        }
        return true;
    }

    private void addDanmuToQFilter(FilterItem item) {
        if (qFilter.size() >= 10) {
            qFilter.poll();
        }
        qFilter.add(item);
    }

    private AVIMMessageHandler messageHandler = new AVIMMessageHandler() {
        @Override
        public void onMessage(AVIMMessage message, AVIMConversation conversation, AVIMClient client) {
            super.onMessage(message, conversation, client);
            if (message instanceof AVIMTextMessage) {
                Map<String, Object> mapAttrs = ((AVIMTextMessage) message).getAttrs();
                addDanmu(((AVIMTextMessage) message).getText(),
                        new DanmuAttrs(((Integer) mapAttrs.get(DanmuAttrs.DANMU_SHOW_POS)).intValue(),
                                ((Integer) mapAttrs.get(DanmuAttrs.DANMU_COLOR)).intValue(),
                                ((Integer) mapAttrs.get(DanmuAttrs.DANMU_TEXT_SIZE)).intValue(),
                                ((String) mapAttrs.get(DanmuAttrs.DANMU_SENDER_ID))), false);
                Toast.makeText(TvLiveActivity.this, ((AVIMTextMessage) message).getText(), Toast.LENGTH_SHORT).show();
                addDanmuToQFilter(new FilterItem(((AVIMTextMessage) message).getText(), ((String) mapAttrs.get(DanmuAttrs.DANMU_SENDER_ID)), false));
            } else {
                LogUtil.d("CustomMessageHandler", "收到未声明的消息" + message.getClass());
            }
        }

    };


}
