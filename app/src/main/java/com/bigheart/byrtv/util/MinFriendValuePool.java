package com.bigheart.byrtv.util;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.bigheart.byrtv.ui.view.activity.TvLiveActivity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * Created by BigHeart on 15/12/20.
 */
public class MinFriendValuePool {
    private static MinFriendValuePool pool;
    private final int MAX_COUNT = 2;
    Queue q = new LinkedList();

    public static MinFriendValuePool getInstance() {
        if (pool == null) {
            pool = new MinFriendValuePool();
        }
        return pool;
    }

    public boolean execute(Queue qIds) {
        if (q.size() > 0) {
            return false;
        }
        q = qIds;
        int qSize = q.size();
        for (int count = 0; count < MAX_COUNT && count < qSize; count++) {
            startNewThread(((TvLiveActivity.FilterItem) qIds.poll()).getSenderId());
            LogUtil.d("MinFriendValuePool", count + "th");
        }
        return true;
    }

    private void startNewThread(final String id) {
        if (id != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Map<String, Object> parameters = new HashMap<>();
                    parameters.put("userID", id);
                    AVCloud.setProductionMode(false);
                    try {
                        AVCloud.callFunction("minFriendValue", parameters);
                    } catch (AVException e) {
                        if (e == null) {
                            LogUtil.d("MinFriendValuePool", "success");
                        } else {
                            e.printStackTrace();
                        }
                    } finally {
                        LogUtil.d("MinFriendValuePool", "start new");
                        startNewThread((String) q.poll());
                    }
                }
            }).start();
        }
    }
}
