package com.bigheart.byrtv.ui.view.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bigheart.byrtv.R;
import com.bigheart.byrtv.data.sharedpreferences.DanmuPreferences;
import com.bigheart.byrtv.ui.presenter.TvLivePresenter;
import com.bigheart.byrtv.ui.view.TvLiveActivityView;
import com.bigheart.byrtv.util.ByrTvUtil;
import com.bigheart.byrtv.util.LogUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

    public static final String TV_LIVE_URI = "tv_live_uri", TV_LIVE_NAME = "tv_live_name";

    private final int OPTION_VOLUME = 0, OPTION_BRIGHTNESS = 1;
    private final float THRESHOLD = ByrTvUtil.dip2px(5f);
    private float adjustY = 0;
    private int adjustOption = OPTION_VOLUME;
    private String channelUri, channelName;
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
    private ImageView ivCloseWrite;
    private ImageView ibBigText, ibSmallText, ibDanmuInTop, ibDanmuInBottom, ibDanmuFlow, ivColor0, ivColor1, ivColor2, ivColor3;

    //弹幕设置 子菜单
    private ScrollView svDanmuSetting;
    private ImageView ivFilterTopDanmu, ivFilterBottomDanmu, ivFilterFlowDanmu, ivFilterColorDanmu;
    private SeekBar sbTextScale, sbDestiny, sbSpeed, sbAlpha;
    private boolean isFilterColorDanmu = false, isFilterTopDanmu = false, isFilterFlowDanmu = false, isFilterBottomDanmu = false;

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


        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
        exec.scheduleAtFixedRate(new Runnable() {
            public void run() {
                addDanmu("四不四洒！！！", false);
            }
        }, 0, 800, TimeUnit.MILLISECONDS);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (danmakuView != null && danmakuView.isPrepared()) {
            danmakuView.pause();
        }
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
        // TODO: 15/12/13 view change
        danmuEtTextSize = size;
        danmuPreferences.setDanmuTextEtSize(size);
    }

    @Override
    public void setDanmuEtTextColorPos(int colorPos) {
        danmuEtColorPos = colorPos;
        danmuPreferences.setDanmuColorEtPos(danmuEtColorPos);
    }

    @Override
    public void setDanmuEtPos(int pos) {
        danmuEtPos = pos;
        danmuPreferences.setDanmuEtPos(pos);
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
        findViewById(R.id.iv_vv_more_setting).setOnClickListener(mainCtlClickListener);
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
        ivCloseWrite = (ImageView) findViewById(R.id.iv_vv_close_danmu_edit);
        ibLaunchDanmu = (ImageButton) findViewById(R.id.ib_launch_danmu);
        ibBigText = (ImageView) findViewById(R.id.iv_vv_big_text);
        ibSmallText = (ImageView) findViewById(R.id.iv_vv_small_text);
        ibDanmuInTop = (ImageView) findViewById(R.id.iv_vv_text_still_top);
        ibDanmuInBottom = (ImageView) findViewById(R.id.iv_vv_text_still_bottom);
        ibDanmuFlow = (ImageView) findViewById(R.id.iv_vv_text_flow);
        ivColor0 = (ImageView) findViewById(R.id.iv_vv_color_0);
        ivColor1 = (ImageView) findViewById(R.id.iv_vv_color_1);
        ivColor2 = (ImageView) findViewById(R.id.iv_vv_color_2);
        ivColor3 = (ImageView) findViewById(R.id.iv_vv_color_3);

        ivCloseWrite.setOnClickListener(editDanmuClickListen);
        ibLaunchDanmu.setOnClickListener(editDanmuClickListen);
        ibBigText.setOnClickListener(editDanmuClickListen);
        ibSmallText.setOnClickListener(editDanmuClickListen);
        ibDanmuInTop.setOnClickListener(editDanmuClickListen);
        ibDanmuInBottom.setOnClickListener(editDanmuClickListen);
        ibDanmuFlow.setOnClickListener(editDanmuClickListen);
        ivColor0.setOnClickListener(editDanmuClickListen);
        ivColor1.setOnClickListener(editDanmuClickListen);
        ivColor2.setOnClickListener(editDanmuClickListen);
        ivColor3.setOnClickListener(editDanmuClickListen);


        //弹幕过滤


        //弹幕设置
        svDanmuSetting = (ScrollView) findViewById(R.id.sv_iv_danmu_setting);
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
        svDanmuSetting.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }


    private void initData() {

        if (getIntent().hasExtra(TV_LIVE_NAME) && getIntent().hasExtra(TV_LIVE_URI)) {
            channelName = getIntent().getStringExtra(TV_LIVE_NAME);
            channelUri = getIntent().getStringExtra(TV_LIVE_URI);
        } else {
            finish();
        }
        //先设置一次时间
        ((TextView) findViewById(R.id.tv_vv_time)).setText(new SimpleDateFormat("hh:mm").format(System.currentTimeMillis()));
        //绑定 Receiver
        IntentFilter updateIntent = new IntentFilter();
        updateIntent.addAction("android.intent.action.TIME_TICK");
        getApplicationContext().registerReceiver(minBroadcast, updateIntent);

        danmuPreferences = new DanmuPreferences(this);
        presenter = new TvLivePresenter(this, this);
        presenter.init();


        tvChannelName.setText(channelName);


        //视频控件设置
        Log.i(TV_LIVE_URI, channelUri + " " + channelUri.length());
        mVideoView.setVideoPath(channelUri);
        this.toast(channelUri);
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
                ivPlayOrPause.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
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
        danmakuView.showFPS(true);
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
                    llDanmuEdit.setVisibility(View.VISIBLE);
                    break;
                case R.id.iv_vv_play_pause:
                    if (mVideoView.isPlaying()) {
//                    LogUtil.d("TvLiveActivity", "pause");
                        mVideoView.pause();
                        ivPlayOrPause.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));
                    } else {
//                    LogUtil.d("TvLiveActivity", "play");
                        mVideoView.start();
                        ivPlayOrPause.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
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
                case R.id.iv_vv_more_setting:
                    showMoreSettingWin();
                    break;
                case R.id.rl_vv_control:
                    onScreenClicked();
                    break;
                case R.id.iv_vv_unlock_screen:
                    isLockScreen = false;
                    ivUnlockScreenLogo.setVisibility(View.INVISIBLE);
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
                    addDanmu(etWriteDanmu.getText().toString().trim(), true);
                    etWriteDanmu.setText("");
                    llDanmuEdit.setVisibility(View.GONE);
                    break;
                case R.id.iv_vv_close_danmu_edit:
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
     * 弹幕设置 中的点击事件
     */

    private View.OnClickListener danmuSettingClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_vv_filter_top:
                    isFilterTopDanmu = !isFilterTopDanmu;
                    danmakuContext.setFTDanmakuVisibility(isFilterTopDanmu);
                    if (isFilterTopDanmu) {
                        //show
                    } else {
                        //hide
                    }
                    break;
                case R.id.iv_vv_filter_flow:
                    isFilterFlowDanmu = !isFilterFlowDanmu;
                    danmakuContext.setR2LDanmakuVisibility(isFilterFlowDanmu);
                    if (isFilterFlowDanmu) {
                        //show
                    } else {
                        //hide
                    }
                    break;
                case R.id.iv_vv_filter_bottom:
                    isFilterBottomDanmu = !isFilterBottomDanmu;
                    danmakuContext.setFBDanmakuVisibility(isFilterBottomDanmu);
                    if (isFilterBottomDanmu) {
                        //show
                    } else {
                        //hide
                    }
                    break;
                case R.id.iv_vv_filter_color:
                    isFilterColorDanmu = !isFilterColorDanmu;
                    if (isFilterColorDanmu) {
                        //only show white color
                        danmakuContext.setColorValueWhiteList(Color.WHITE);
                    } else {
                        //show all color
//                        danmakuContext.setColorValueWhiteList(0);
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
            if (rlVvBottom.isShown() || llDanmuEdit.isShown() || svDanmuSetting.isShown()) {
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
        svDanmuSetting.setVisibility(View.GONE);
    }

    /**
     * 显示上下菜单
     */
    private void showTopBottomMenu() {
        rlVvBottom.setVisibility(View.VISIBLE);
        rlVvTop.setVisibility(View.VISIBLE);
    }


    /**
     * 显示 更多设置 选择窗口
     */
    private void showMoreSettingWin() {
        if (popupMoreSetting == null) {
            View view = getLayoutInflater().inflate(R.layout.popup_layout, null);
            ListView listView = (ListView) view.findViewById(R.id.lv_pop_up_win);

            ArrayList<HashMap<String, String>> moreSettingList = new ArrayList<>();
            final String tvItemKey = "textItem";
            HashMap<String, String> danmuSettingMap = new HashMap<>();
            HashMap<String, String> danmuFilterMap = new HashMap<>();

            danmuSettingMap.put(tvItemKey, getResources().getString(R.string.danmu_filter));
            moreSettingList.add(danmuSettingMap);
            danmuFilterMap.put(tvItemKey, getResources().getString(R.string.danmu_control));
            moreSettingList.add(danmuFilterMap);

            listView.setAdapter(new SimpleAdapter(this, moreSettingList, R.layout.item_pop, new String[]{tvItemKey}, new int[]{R.id.tv_pop_item}));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    cleanAllMenu();
                    if (popupMoreSetting != null) {
                        popupMoreSetting.dismiss();
                    }
                    if (position == 0) {
                        //弹幕设置
                        svDanmuSetting.setVisibility(View.VISIBLE);
                    } else if (position == 1) {
                        //用户屏蔽
                    }
                }
            });

            popupMoreSetting = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            popupMoreSetting.setOutsideTouchable(true);
            popupMoreSetting.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));

        }
        LogUtil.d("showViewQualityWin", "show");
        popupMoreSetting.showAsDropDown(findViewById(R.id.iv_vv_more_setting));
//        popupMoreSetting.showAtLocation(findViewById(R.id.iv_vv_more_setting), Gravity.BOTTOM, 0, 0);

    }


    private final int danmuStayTime = 1200;//弹幕显示时间
    private final float danmuBigTextSize = 25f, danmuSmallTextSize = 15f;

    /**
     * @param content
     * @param isUser  是否是当前用户发送的
     * @return
     */
    private boolean addDanmu(String content, boolean isUser) {
        if (!TextUtils.isEmpty(content)) {
            BaseDanmaku danmaku;
            if (danmuEtPos == TvLiveActivity.this.DANMU_TEXT_TOP) {
                danmaku = danmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_FIX_TOP);
            } else if (danmuEtPos == TvLiveActivity.this.DANMU_TEXT_BOTTOM) {
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
                if (danmuEtTextSize == TvLiveActivityView.DANMU_ET_BIG_SIZE_TEXT) {
                    danmaku.textSize = danmuBigTextSize * (danmakuParser.getDisplayer().getDensity() - 0.6f);
                } else {
                    danmaku.textSize = danmuSmallTextSize * (danmakuParser.getDisplayer().getDensity() - 0.6f);
                }
                danmaku.textColor = TvLiveActivity.danmuColor[danmuEtColorPos];
                danmaku.textShadowColor = Color.WHITE;
                if (isUser) {
                    danmaku.borderColor = Color.GREEN;
                }
                danmakuView.addDanmaku(danmaku);
//                LogUtil.d("addDanmu", "addDanmu success " + danmuEtColorPos + " " + danmuEtTextSize);
                // TODO: 15/12/13 上传弹幕到 leancloud
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

}
