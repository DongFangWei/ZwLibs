package com.dongfangwei.zwlibs.demo.modules.home.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dongfangwei.zwlibs.base.recycler.AutoRecyclerView;
import com.dongfangwei.zwlibs.base.recycler.BaseAdapter;
import com.dongfangwei.zwlibs.base.recycler.LinearDivider;
import com.dongfangwei.zwlibs.demo.R;
import com.dongfangwei.zwlibs.demo.modules.home.presenter.HomePresenter;
import com.dongfangwei.zwlibs.demo.modules.home.presenter.IHomePresenter;
import com.dongfangwei.zwlibs.demo.publics.TextAdapter;

import java.util.List;

public class HomeFragment extends Fragment implements IHomeView, AutoRecyclerView.OnLoadListener {
    private AutoRecyclerView mAutoRecyclerView;
    private int page = 1;
    private static final int SIZE = 20;
    private TextAdapter mAdapter;
    private IHomePresenter mPresenter;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new HomePresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAutoRecyclerView = (AutoRecyclerView) inflater.inflate(R.layout.fragment_home, container, false);
        return mAutoRecyclerView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context context = view.getContext();
        mAutoRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAutoRecyclerView.setOnLoadListener(this);
        mAutoRecyclerView.addItemDecoration(new LinearDivider());
        mAdapter = new TextAdapter(context);
        mAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseAdapter adapter, View view, int position) {
                String s = ((TextAdapter) adapter).getItemViewData(position);
                Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
            }
        });
        mAutoRecyclerView.setAdapter(mAdapter);
        mAutoRecyclerView.setRefreshing(true);
        getData();
    }

    private void getData() {
        mPresenter.getData(page, SIZE);
    }

    @Override
    public void getDataSuccess(List<String> list) {
        if (page++ == 1) {
            mAdapter.clearData();
        }
        mAdapter.setData(list);
        mAutoRecyclerView.onLoadComplete(page == 10);
    }

    @Override
    public void getDataFailure(int code, String msg) {
        page++;
        mAutoRecyclerView.onLoadError();
    }

    @Override
    public void onRefresh() {
        page = 1;
        getData();
    }

    @Override
    public void onLoad() {
        getData();
    }
}
