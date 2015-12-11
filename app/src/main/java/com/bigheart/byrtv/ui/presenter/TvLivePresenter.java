package com.bigheart.byrtv.ui.presenter;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.view.WindowManager;

import com.bigheart.byrtv.ui.view.TvLiveAvtivityView;

import io.vov.vitamio.MediaPlayer;

/**
 * Created by BigHeart on 15/12/10.
 */
public class TvLivePresenter extends Presenter {

    private Context context;
    private TvLiveAvtivityView view;


    public TvLivePresenter(Context c, TvLiveAvtivityView tvLiveAvtivityView) {
        context = c;
        view = tvLiveAvtivityView;
    }


    public void tvPlayError(MediaPlayer mp, int what, int extra) {

    }

    public void tvPlayBuffering() {

    }

    public void tvPlayInfo() {

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
}