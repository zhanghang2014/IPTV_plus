package com.bigheart.byrtv.ui.view.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigheart.byrtv.R;
import com.bigheart.byrtv.data.sqlite.ChannelColumn;
import com.bigheart.byrtv.data.sqlite.SqlChannelManager;
import com.bigheart.byrtv.ui.module.ChannelModule;
import com.bigheart.byrtv.ui.presenter.AllChannelPresenter;
import com.bigheart.byrtv.ui.view.AllChannelView;
import com.bigheart.byrtv.ui.view.FragContactToAct;
import com.bigheart.byrtv.ui.view.activity.MainActivity;
import com.bigheart.byrtv.util.ChannelSortType;

import java.util.ArrayList;

/**
 * Created by BigHeart on 15/12/6.
 */

public class AllChannelFragment extends Fragment implements AllChannelView {

    private static final String ITEM_SORT_TYPE = "itemSortType";
    private final int ItemTypeCount = 2;


    private ListView lvAllChannel;
    private SwipeRefreshLayout refreshLayout;

    private int sortType = ChannelSortType.SORT_BY_PEOPLE_NUM.ordinal();//列表排序类型，默认在线人数排序
    private ChannelAdapter channelAdapter;
    private ArrayList<ChannelModule> channels;

    private static FragContactToAct allChannelFragContactToAct;
    private static AllChannelPresenter presenter;


    public static AllChannelFragment newInstance(ChannelSortType itemSortType, FragContactToAct contactToAct) {
        AllChannelFragment fragment = new AllChannelFragment();
        allChannelFragContactToAct = contactToAct;
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

//        Log.i("AllChannelFragment","onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.fragment_channel, container, false);

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

        lvAllChannel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                presenter.onItemClick(channels.get(position));
            }
        });

        allChannelFragContactToAct.fragmentInitOk();
//        Log.i("AllChannelFragment","onCreateView");

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
        try {
            allChannelFragContactToAct = (FragContactToAct) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement FragContactToAct");
        }
//        Log.i("AllChannelFragment","onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
//        Log.i("AllChannelFragment","onDetach");
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
//            Log.i("AllChannelFragment channel size", channels.size() + "");
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
            final ChannelModule tmpChannel = channels.get(position);
            holder.tvChannelName.setText(tmpChannel.getChannelName());


            if (tmpChannel.isCollected() == true) {
                setCollectionStartState(holder.ivCollection, false);
            } else {
                setCollectionStartState(holder.ivCollection, true);
            }

            holder.ivCollection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tmpChannel.isCollected()) {
                        //cancel collection
                        if (presenter.updateChannelCollectedState(tmpChannel.getSqlId(), false) != -1) {
                            tmpChannel.setIsCollected(false);
                            setCollectionStartState((ImageView) v, true);
                        } else {
                            Toast.makeText(getActivity(), "取消收藏 失败 T～T", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        //add collection
                        if (presenter.updateChannelCollectedState(tmpChannel.getSqlId(), true) != -1) {
                            tmpChannel.setIsCollected(true);
                            setCollectionStartState((ImageView) v, false);
                        } else {
                            Toast.makeText(getActivity(), "收藏 失败 T～T", Toast.LENGTH_SHORT).show();
                        }
                    }
                    allChannelFragContactToAct.notifyMyCollectionFrg();
                }
            });

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

    private void setCollectionStartState(ImageView imageView, boolean isCollection) {
        if (isCollection) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imageView.setImageDrawable(getResources().getDrawable(android.R.drawable.btn_star_big_off, getActivity().getTheme()));
            } else {
                imageView.setImageDrawable(getResources().getDrawable(android.R.drawable.btn_star_big_off));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imageView.setImageDrawable(getResources().getDrawable(android.R.drawable.btn_star_big_on, getActivity().getTheme()));
            } else {
                imageView.setImageDrawable(getResources().getDrawable(android.R.drawable.btn_star_big_on));
            }
        }
    }


}
