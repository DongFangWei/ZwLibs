package com.zhangwei.zwlibs.demo.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.zhangwei.zwlibs.baselib.recycler.AutoRecyclerView;
import com.zhangwei.zwlibs.baselib.recycler.LinearDivider;
import com.zhangwei.zwlibs.demo.R;
import com.zhangwei.zwlibs.demo.presenter.MainPresenter;
import com.zhangwei.zwlibs.demo.presenter.MainPresenterImpl;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MainView, AutoRecyclerView.OnLoadListener {
    private AutoRecyclerView mAutoRecyclerView;
    private MainPresenter mPresenter;
    private int page = 1;
    private static final int SIZE = 20;
    private MainAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPresenter = new MainPresenterImpl(this);
        initView();
        getData();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.main_header_tb);
        setSupportActionBar(toolbar);
        mAutoRecyclerView = findViewById(R.id.main_list_arv);
        mAutoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAutoRecyclerView.setOnLoadListener(this);
        mAutoRecyclerView.addItemDecoration(new LinearDivider(LinearDivider.VERTICAL));
        mAdapter = new MainAdapter(this);
        mAutoRecyclerView.setAdapter(mAdapter);
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
        mAutoRecyclerView.onLoadComplete(true);
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