package com.bigheart.byrtv.util;

import com.bigheart.byrtv.ui.module.ChannelModule;

import java.util.Comparator;

/**
 * Created by BigHeart on 15/12/19.
 */

public class SortByPeople implements Comparator {

    @Override
    public int compare(Object o, Object t1) {
        ChannelModule c1 = (ChannelModule) o;
        ChannelModule c2 = (ChannelModule) t1;
        if (c1.getPeopleNum() > c2.getPeopleNum()) {
            return 1;
        } else if (c1.getPeopleNum() == c2.getPeopleNum()) {
            return 0;
        } else {
            return -1;
        }
    }
}
