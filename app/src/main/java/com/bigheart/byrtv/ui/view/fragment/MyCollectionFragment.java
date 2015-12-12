package com.bigheart.byrtv.ui.view.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bigheart.byrtv.R;
import com.bigheart.byrtv.ui.module.ChannelModule;
import com.bigheart.byrtv.ui.presenter.MyCollectionPresenter;
import com.bigheart.byrtv.ui.view.FragContactToAct;
import com.bigheart.byrtv.ui.view.MyCollectionView;
import com.bigheart.byrtv.util.ChannelSortType;
import com.bigheart.byrtv.util.LogUtil;

import java.util.ArrayList;

/**
 * Created by BigHeart on 15/12/6.
 */

public class MyCollectionFragment extends Fragment implements MyCollectionView {

    private static final String ITEM_SORT_TYPE = "itemSortType";
    private int sortType = ChannelSortType.SORT_BY_ALPHA.ordinal();//默认在线人数排序

    private ListView lvCollection;
    private SwipeRefreshLayout refreshLayout;

    private ArrayList<ChannelModule> collectionChannels = new ArrayList<>();
    private CollectionAdapter collectionAdapter;
    private MyCollectionPresenter presenter;
    private static FragContactToAct collectionFragContactToAct;

    public static MyCollectionFragment newInstance(ChannelSortType itemSortType, FragContactToAct contactToAct) {
        MyCollectionFragment fragment = new MyCollectionFragment();
        collectionFragContactToAct = contactToAct;
        Bundle args = new Bundle();
        args.putInt(ITEM_SORT_TYPE, itemSortType.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

    public MyCollectionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new MyCollectionPresenter(getActivity(), this);
        if (getArguments() != null) {
            sortType = getArguments().getInt(ITEM_SORT_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.fragment_channel, container, false);
        collectionAdapter = new CollectionAdapter(getActivity());
        lvCollection = (ListView) layoutView.findViewById(R.id.lv_all_channel);
        collectionChannels = new ArrayList<>();
        lvCollection.setAdapter(collectionAdapter);

        refreshLayout = (SwipeRefreshLayout) layoutView.findViewById(R.id.srl_all_channel);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //只刷新在线人数，不刷新频道列表
            }
        });

        lvCollection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                presenter.onItemClick(collectionChannels.get(position));
            }
        });

//        if (refreshLayout != null) {
        collectionFragContactToAct.fragmentInitOk();
//            LogUtil.d("refreshLayout", "ok");
//        }

        return layoutView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            collectionFragContactToAct = (FragContactToAct) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement FragContactToAct");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        collectionFragContactToAct = null;
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
        this.collectionChannels = channels;
        collectionAdapter.notifyDataSetChanged();
    }

    private class CollectionAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        CollectionAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return collectionChannels.size();
        }

        @Override
        public Object getItem(int position) {
            return collectionChannels.get(position);
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
            ChannelModule tmpChannel = collectionChannels.get(position);
            holder.tvChannelName.setText(tmpChannel.getChannelName());

            return convertView;
        }

        class ViewHolder {
            TextView tvPeopleNum, tvChannelName;
            ImageView ivChannelPic;

            ViewHolder(TextView channelName, TextView peopleNum, ImageView channelPic, ImageView collection) {
                tvChannelName = channelName;
                tvPeopleNum = peopleNum;
                ivChannelPic = channelPic;
                collection.setVisibility(View.GONE);
            }
        }
    }


}
