package com.bigheart.byrtv.util;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.FunctionCallback;
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
    Queue<String> q = new LinkedList();
    private int crtCount = 0;

    public static MinFriendValuePool getInstance() {
        if (pool == null) {
            pool = new MinFriendValuePool();
        }
        return pool;
    }

    public void execute(String id) {
        if (id != null) {
            q.add(id);
            if (crtCount < MAX_COUNT) {
                startNewThread(id);
                q.poll();
            }
        }
    }

    private void startNewThread(final String id) {
        if (id != null) {
            changeCrtCount(1);
            LogUtil.d("MinFriendValuePool new thread", crtCount + "");
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("userID", id);
            AVCloud.callFunctionInBackground("minFriendValue", parameters, new FunctionCallback() {
                @Override
                public void done(Object o, AVException e) {
                    changeCrtCount(-1);
                    startNewThread(q.poll());
                }
            });

//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    Map<String, Object> parameters = new HashMap<>();
//                    parameters.put("userID", id);
//                    AVCloud.setProductionMode(false);
//                    try {
//                        AVCloud.callFunction("minFriendValue", parameters);
//                    } catch (AVException e) {
//                        if (e == null) {
//                            LogUtil.d("MinFriendValuePool", "success");
//                        } else {
//                            e.printStackTrace();
//                        }
//                    } finally {
//                        LogUtil.d("MinFriendValuePool", "start new");
//                        changeCrtCount(-1);
//                        startNewThread(q.poll());
//                    }
//                }
//            }).start();
        }
    }

    private synchronized void changeCrtCount(int change) {
        crtCount += change;
    }
}
