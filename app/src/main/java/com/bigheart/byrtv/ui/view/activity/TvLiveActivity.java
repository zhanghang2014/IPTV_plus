package com.bigheart.byrtv.ui.view.activity;

import android.app.Service;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bigheart.byrtv.R;
import com.bigheart.byrtv.util.ByrTvUtil;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;


/**
 * Created by BigHeart on 15/12/10.
 */
public class TvLiveActivity extends BaseActivity {

    public static final String TV_LIVE_URI = "tv_live_uri", TV_LIVE_NAME = "tv_live_name";

    private final int OPTION_VOLUMN = 0, OPTION_BRIGHTNESS = 1;
    private final float THRESHOLD = ByrTvUtil.dip2px(5f);

    private String channelUri, channelName;
    private VideoView mVideoView;

    private float mY = 0, mRawY = 0;

    private int adjustOption = OPTION_VOLUMN;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    private void initUI() {
        mVideoView = (VideoView) findViewById(R.id.vv_tv_live);
    }

    private void initData() {

        if (getIntent().hasExtra(TV_LIVE_NAME) && getIntent().hasExtra(TV_LIVE_URI)) {
            channelName = getIntent().getStringExtra(TV_LIVE_NAME);
            channelUri = getIntent().getStringExtra(TV_LIVE_URI);
        } else {
            finish();
        }

        Log.i(TV_LIVE_URI, channelUri + " " + channelUri.length());

        mVideoView.setVideoPath(channelUri);
        this.toast(channelUri);
        mVideoView.setMediaController(new MediaController(this));
        mVideoView.requestFocus();
        mVideoView.setBufferSize(5120);

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setPlaybackSpeed(1.0f);
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
            }
        });

        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {

                return false;
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        HandlerTouch(ev);
        return super.dispatchTouchEvent(ev);
    }

    private void HandlerTouch(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mY = event.getY();
                mRawY = mY;
                if (event.getX() < ByrTvUtil.getScreenHeight() / 2.0f) {
                    //左边:亮度
                    adjustOption = OPTION_BRIGHTNESS;
                } else {
                    //右边:声音
                    adjustOption = OPTION_VOLUMN;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float y = event.getY();
                float yy = (mY - y) / THRESHOLD;
                if (Math.abs(yy) >= 1) {
                    mY = y;
                    switch (adjustOption) {
                        case OPTION_BRIGHTNESS:
                            adjustBrightness(yy);
                            break;
                        case OPTION_VOLUMN:
                            adjustVolume(yy);
                            break;
                        default:
                            break;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (Math.abs(mRawY - event.getY()) < 10) {
//                    mMediaController.show(2000);
                }
                break;
        }

    }

    private void adjustVolume(float dis) {
        AudioManager audioMgr = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
        if (dis > 0) {
            audioMgr.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_RAISE, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        } else if (dis < 0) {
            audioMgr.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_LOWER, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        }
        this.toast("声音：" + audioMgr.getStreamVolume(AudioManager.STREAM_MUSIC));
    }

    private void adjustBrightness(float dis) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = lp.screenBrightness + dis / 100.0f;
        if (lp.screenBrightness > 1) {
            lp.screenBrightness = 1;
        } else if (lp.screenBrightness < 0.1) {
            lp.screenBrightness = 0.1f;
        }
        toast(String.format("亮度：%.0f", lp.screenBrightness * 100));
        getWindow().setAttributes(lp);
    }
}
