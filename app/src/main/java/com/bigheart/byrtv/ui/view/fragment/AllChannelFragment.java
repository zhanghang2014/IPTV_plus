package com.bigheart.byrtv.ui.view.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigheart.byrtv.R;
import com.bigheart.byrtv.ui.module.ChannelModule;
import com.bigheart.byrtv.ui.presenter.AllChannelPresenter;
import com.bigheart.byrtv.ui.view.AllChannelView;
import com.bigheart.byrtv.util.ChannelSortType;

import java.util.ArrayList;

/**
 * Created by BigHeart on 15/12/6.
 */

public class AllChannelFragment extends Fragment implements AllChannelView {

    private static final String ITEM_SORT_TYPE = "itemSortType";
    private final int ItemTypeCount = 2;
    private final int MSG_START_FRESH = 0, MSG_STOP_FRESH = 1, MSG_DATA_REFRESH = 2, MSG_TOAST = 3;


    private ListView lvAllChannel;
    private SwipeRefreshLayout refreshLayout;

    private int sortType = ChannelSortType.SORT_BY_PEOPLE_NUM.ordinal();//列表排序类型，默认在线人数排序
    private ChannelAdapter channelAdapter;
    private ArrayList<ChannelModule> channels;
    private AllChannelPresenter presenter;


    public static AllChannelFragment newInstance(ChannelSortType itemSortType) {
        AllChannelFragment fragment = new AllChannelFragment();
        Bundle args = new Bundle();
        args.putInt(ITEM_SORT_TYPE, itemSortType.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

    public AllChannelFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new AllChannelPresenter(getActivity(), this);

        if (getArguments() != null) {
            sortType = getArguments().getInt(ITEM_SORT_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.fragment_all_channel, container, false);

        channelAdapter = new ChannelAdapter(getActivity());
        lvAllChannel = (ListView) layoutView.findViewById(R.id.lv_all_channel);
        channels = new ArrayList<>();
        lvAllChannel.setAdapter(channelAdapter);

        refreshLayout = (SwipeRefreshLayout) layoutView.findViewById(R.id.srl_all_channel);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //只刷新在线人数，不刷新频道列表
            }
        });

        presenter.init();

        return layoutView;

    }

    /**
     * This method was deprecated in API level 23. Use onAttach(Context) instead.
     *
     * @param activity
     */

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    private class ChannelAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        ChannelAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getViewTypeCount() {
            return ItemTypeCount;
        }

        @Override
        public int getCount() {
            return channels.size();
        }

        @Override
        public Object getItem(int position) {
            return channels.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_channel, null);
                holder = new ViewHolder((TextView) convertView.findViewById(R.id.tv_channel_name),
                        (TextView) convertView.findViewById(R.id.tv_people_num),
                        (ImageView) convertView.findViewById(R.id.iv_chanel),
                        (ImageView) convertView.findViewById(R.id.iv_collection));
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ChannelModule tmpChannel = channels.get(position);
            holder.tvChannelName.setText(tmpChannel.getChannelName());

            return convertView;
        }

        class ViewHolder {
            TextView tvPeopleNum, tvChannelName;
            ImageView ivCollection, ivChannelPic;

            ViewHolder(TextView channelName, TextView peopleNum, ImageView channelPic, ImageView collection) {
                tvChannelName = channelName;
                tvPeopleNum = peopleNum;
                ivChannelPic = channelPic;
                ivCollection = collection;
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    @Override
    public void startRefresh() {
        refreshLayout.setRefreshing(true);
    }

    @Override
    public void stopRefresh() {
        refreshLayout.setRefreshing(false);

    }

    @Override
    public void updateData(ArrayList<ChannelModule> channels) {
        this.channels = channels;
        channelAdapter.notifyDataSetChanged();
    }
}
