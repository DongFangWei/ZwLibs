package com.dongfangwei.zwlibs.demo.modules.home.model;

import com.dongfangwei.zwlibs.demo.modules.home.presenter.IHomePresenter;
import com.dongfangwei.zwlibs.demo.modules.main.entity.HttpParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeModel implements IHomeModel {
    @Override
    public void getData(HttpParameters parameters, IHomePresenter presenter) {
        int page = (int) parameters.get("page");
        int size = (int) parameters.get("size");
        List<String> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(String.format(Locale.getDefault(), "Home - %d - %d", page, i));
        }
        presenter.getDataSuccess(list);
    }

    @Override
    public void onDestroy() {

    }
}
