package com.bigheart.byrtv.ui.module;

/**
 * 频道
 * <p/>
 * Created by BigHeart on 15/12/6.
 */
public class ChannelModule {
    private String channelName;
    private boolean isCollected;
    private String Uri;
    private int peopleNum;
    private long sqlId;

    public ChannelModule() {

    }

    public ChannelModule(String channelName, boolean isCollected, String uri, long id) {
        setChannelName(channelName);
        setIsCollected(isCollected);
        setUri(uri);
        setSqlId(id);
    }

    public long getSqlId() {
        return sqlId;
    }

    public void setSqlId(long sqlId) {
        this.sqlId = sqlId;
    }

    public int getPeopleNum() {
        return peopleNum;
    }

    public void setPeopleNum(int peopleNum) {
        this.peopleNum = peopleNum;
    }


    public String getUri() {
        return Uri;
    }

    public void setUri(String uri) {
        Uri = uri;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void setIsCollected(boolean isCollected) {
        this.isCollected = isCollected;
    }
}
