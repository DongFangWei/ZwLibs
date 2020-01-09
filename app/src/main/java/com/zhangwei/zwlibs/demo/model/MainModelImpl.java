package com.zhangwei.zwlibs.demo.model;

import com.zhangwei.zwlibs.demo.entity.HttpParameters;
import com.zhangwei.zwlibs.demo.presenter.MainPresenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainModelImpl implements MainModel {
    private final static String FORMAT = "Item %d-%d";

    @Override
    public void getData(HttpParameters parameters, MainPresenter presenter) {
        int page = (int) parameters.get("page");
        int size = (int) parameters.get("size");
        List<String> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(String.format(Locale.getDefault(), FORMAT, page, i));
        }
        presenter.getDataSuccess(list);
    }
}
