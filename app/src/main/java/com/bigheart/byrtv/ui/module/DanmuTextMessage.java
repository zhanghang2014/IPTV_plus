package com.bigheart.byrtv.ui.module;

import com.avos.avoscloud.im.v2.AVIMMessageField;
import com.avos.avoscloud.im.v2.AVIMMessageType;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.bigheart.byrtv.ui.view.activity.TvLiveActivity;

import java.util.Map;

/**
 * Created by BigHeart on 15/12/18.
 */

@AVIMMessageType(
        type = 123
)
public class DanmuTextMessage extends AVIMTypedMessage {
    @AVIMMessageField(
            name = "danmutext"
    )
    String danmuText;
    @AVIMMessageField(
            name = "attrs"
    )
    Map<String, Object> attrs;
    @AVIMMessageField(
            name = "danmuattrs"
    )
    TvLiveActivity.DanmuAttrs danmuAttrs;

    public String getDanmuText() {
        return this.danmuText;
    }

    public void setDanmuText(String text) {
        this.danmuText = text;
    }


    public Map<String, Object> getAttrs() {
        return this.attrs;
    }

    public void setAttrs(Map<String, Object> attr) {
        this.attrs = attr;
    }

    public TvLiveActivity.DanmuAttrs getDanmuAttrs() {
        return danmuAttrs;
    }

    public void setDanmuAttrs(TvLiveActivity.DanmuAttrs danmuAttrs) {
        this.danmuAttrs = danmuAttrs;
    }

}
