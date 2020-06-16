package com.dongfangwei.zwlibs.demo.modules.home.presenter;

import com.dongfangwei.zwlibs.demo.modules.home.model.HomeModel;
import com.dongfangwei.zwlibs.demo.modules.home.model.IHomeModel;
import com.dongfangwei.zwlibs.demo.modules.home.view.IHomeView;
import com.dongfangwei.zwlibs.demo.modules.main.entity.HttpParameters;

import java.util.List;

public class HomePresenter implements IHomePresenter {
    private IHomeModel model;
    private IHomeView view;

    public HomePresenter(IHomeView view) {
        this.view = view;
        this.model = new HomeModel();
    }

    @Override
    public void getData(int page, int size) {
        if (model != null) {
            HttpParameters parameters = new HttpParameters();
            parameters.put("page", page);
            parameters.put("size", size);
            model.getData(parameters, this);
        }
    }

    @Override
    public void getDataSuccess(List<String> list) {
        if (view != null) {
            view.getDataSuccess(list);
        }
    }

    @Override
    public void getDataError(Throwable e) {

    }

    @Override
    public void onDestroy() {
        if (model != null) {
            model = null;
        }
        if (view != null) {
            view = null;
        }
    }
}
