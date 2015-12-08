package com.bigheart.byrtv.ui.view.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigheart.byrtv.R;
import com.bigheart.byrtv.util.ChannelSortType;

/**
 * Created by BigHeart on 15/12/6.
 */

public class MyCollectionFragment extends Fragment {

    private static final String ITEM_SORT_TYPE = "itemSortType";
    private int sortType = ChannelSortType.SORT_BY_PEOPLE_NUM.ordinal();//默认在线人数排序


    public static MyCollectionFragment newInstance(ChannelSortType itemSortType) {
        MyCollectionFragment fragment = new MyCollectionFragment();
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
        if (getArguments() != null) {
            sortType = getArguments().getInt(ITEM_SORT_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_collecttion, container, false);
    }

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

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }


}
