package com.bigheart.byrtv.util;

import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationMemberCountCallback;
import com.bigheart.byrtv.ui.module.ChannelModule;
import com.bigheart.byrtv.ui.presenter.FinishObtainDataRsp;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by BigHeart on 15/12/20.
 */
public class ObtainPeopNumPool {
    private static ObtainPeopNumPool pool;
    Queue<ChannelModule> q = new LinkedList();
    private final int MAX_COUNT = 3;
    private int crtCount = 0;
    private static FinishObtainDataRsp rspo;

    public static ObtainPeopNumPool getInstance(FinishObtainDataRsp rsp) {
        if (pool == null) {
            pool = new ObtainPeopNumPool();
        }
        rspo = rsp;
        return pool;
    }

    public void execute(ArrayList<ChannelModule> list) {
        for (ChannelModule c : list) {
            execute(c);
        }
    }

    public void execute(ChannelModule c) {
        //先到先执行
        if (c != null) {
            q.add(c);
            if (crtCount < MAX_COUNT) {
                startNewThread(q.poll());
            }
        }
    }

    private void startNewThread(final ChannelModule c) {
        if (c != null) {
            if (c.getConversation() != null) {
                changeCrtCount(1);
                c.getConversation().getMemberCount(new AVIMConversationMemberCountCallback() {
                    @Override
                    public void done(Integer count, AVIMException e) {
                        if (e == null) {
                            c.setPeopleNum(count);
                            LogUtil.d(c.getConversation().getName(), "conversation got " + count + " members");
                        } else {
                            e.printStackTrace();
                        }
                        LogUtil.d("ObtainPeopNumPool crtCount", crtCount + "");
                        changeCrtCount(-1);
                        startNewThread(q.poll());
                    }
                });
            }
        } else {
            //执行完毕
            rspo.finishObtainPeopNum(true);
            LogUtil.d("ObtainPeopNumPool", "finish");
//            rspo = null;
        }
    }


    private synchronized void changeCrtCount(int change) {
        crtCount += change;
    }

}
