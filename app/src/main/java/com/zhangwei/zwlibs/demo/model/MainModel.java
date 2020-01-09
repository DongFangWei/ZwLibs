package com.zhangwei.zwlibs.demo.model;


import com.zhangwei.zwlibs.demo.entity.HttpParameters;
import com.zhangwei.zwlibs.demo.presenter.MainPresenter;

public interface MainModel {
    void getData(HttpParameters parameters, MainPresenter presenter);
}
