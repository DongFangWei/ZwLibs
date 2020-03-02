package com.dongfangwei.zwlibs.demo.presenter;

import com.dongfangwei.zwlibs.demo.entity.HttpParameters;
import com.dongfangwei.zwlibs.demo.model.MainModel;
import com.dongfangwei.zwlibs.demo.model.MainModelImpl;
import com.dongfangwei.zwlibs.demo.view.MainView;

import java.util.List;

public class MainPresenterImpl implements MainPresenter {
    private MainModel mModel;
    private MainView mView;

    public MainPresenterImpl(MainView view) {
        this.mView = view;
        this.mModel = new MainModelImpl();
    }

    @Override
    public void getData(int page, int size) {
        HttpParameters parameters = new HttpParameters();
        parameters.put("page", page);
        parameters.put("size", size);
        mModel.getData(parameters, this);
    }

    @Override
    public void getDataSuccess(List<String> list) {
        mView.getDataSuccess(list);
    }

    @Override
    public void getDataFailure(Throwable e) {
        mView.getDataFailure(2333, e.getMessage());
    }

    @Override
    public void onDestroy() {
        if (mView != null) {
            mView = null;
        }
        if (mModel != null) {
            mModel = null;
        }
    }
}
