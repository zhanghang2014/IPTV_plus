package com.bigheart.byrtv.ui.view.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigheart.byrtv.R;
import com.bigheart.byrtv.ui.presenter.TvLivePresenter;
import com.bigheart.byrtv.ui.view.TvLiveAvtivityView;
import com.bigheart.byrtv.util.ByrTvUtil;
import com.bigheart.byrtv.util.LogUtil;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;


/**
 * Created by BigHeart on 15/12/10.
 */
public class TvLiveActivity extends BaseActivity implements TvLiveAvtivityView, View.OnClickListener {

    public static final String TV_LIVE_URI = "tv_live_uri", TV_LIVE_NAME = "tv_live_name";

    private final int OPTION_VOLUME = 0, OPTION_BRIGHTNESS = 1;
    private final float THRESHOLD = ByrTvUtil.dip2px(5f);
    private float adjustY = 0;
    private int adjustOption = OPTION_VOLUME;
    private String channelUri, channelName;

    private VideoView mVideoView;
    private RelativeLayout rlVvTop, rlVvBottom;
    private Button btLaunchDanmu, btLockScreen, btDanmuSwitch;
    private ImageView ivPlayOrPause;
    private TextView tvChannelName, tvVideoQuality, tvBufferInfo;

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mVideoView.isPlaying()) {
            mVideoView.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
        if (minBroadcast.isOrderedBroadcast())
            getApplicationContext().unregisterReceiver(minBroadcast);
    }

    private void initUI() {
        mVideoView = (VideoView) findViewById(R.id.vv_tv_live);

        findViewById(R.id.rl_vv_control).setOnClickListener(this);
        //top
        rlVvTop = (RelativeLayout) findViewById(R.id.rl_vv_top);
        tvChannelName = (TextView) findViewById(R.id.tv_vv_channel_name);
        findViewById(R.id.iv_vv_back).setOnClickListener(this);
        tvVideoQuality = (TextView) findViewById(R.id.tv_vv_video_quality);
        tvVideoQuality.setOnClickListener(this);
        //center
        tvBufferInfo = (TextView) findViewById(R.id.tv_vv_buffer_info);
        //bottom
        rlVvBottom = (RelativeLayout) findViewById(R.id.rl_vv_bottom);
        btLockScreen = (Button) findViewById(R.id.bt_vv_lock_screen);
        btLockScreen.setOnClickListener(this);
        btDanmuSwitch = (Button) findViewById(R.id.bt_vv_danmu_switch);
        btDanmuSwitch.setOnClickListener(this);
        btLaunchDanmu = (Button) findViewById(R.id.bt_vv_launch_danmu);
        btLaunchDanmu.setOnClickListener(this);
        ivPlayOrPause = (ImageView) findViewById(R.id.iv_vv_play_pause);
        ivPlayOrPause.setOnClickListener(this);
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


        presenter = new TvLivePresenter(this, this);

        tvChannelName.setText(channelName);


        Log.i(TV_LIVE_URI, channelUri + " " + channelUri.length());
        mVideoView.setVideoPath(channelUri);
        this.toast(channelUri);
        mediaController = new MediaController(this);
        mVideoView.setMediaController(mediaController);

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
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        HandlerTouch(ev);
        return super.dispatchTouchEvent(ev);
//        return false;
    }

    private void HandlerTouch(MotionEvent event) {
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
                            presenter.adjustBrightness(TvLiveActivity.this, yy);
                            break;
                        case OPTION_VOLUME:
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

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_vv_video_quality:
                break;
            case R.id.bt_vv_lock_screen:
                break;
            case R.id.bt_vv_danmu_switch:
                break;
            case R.id.bt_vv_launch_danmu:
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
                finish();
                break;
            case R.id.iv_vv_more_setting:
                break;
            case R.id.rl_vv_control:
                onScreenClicked();
                break;
            default:
                break;
        }
    }


    /**
     * 屏幕被单击
     */
    private void onScreenClicked() {
        if (rlVvBottom.getVisibility() == View.VISIBLE) {
            cleanAllMenu();
        } else {
            showTopBottonMenu();
        }
    }

    /**
     * 清空屏幕上的所有菜单
     */
    private void cleanAllMenu() {
        rlVvBottom.setVisibility(View.INVISIBLE);
        rlVvTop.setVisibility(View.INVISIBLE);
    }

    /**
     * 显示上下菜单
     */
    private void showTopBottonMenu() {
        rlVvBottom.setVisibility(View.VISIBLE);
        rlVvTop.setVisibility(View.VISIBLE);
    }
}
