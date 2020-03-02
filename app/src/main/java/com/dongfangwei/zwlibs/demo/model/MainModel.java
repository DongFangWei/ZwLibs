package com.dongfangwei.zwlibs.demo.model;


import com.dongfangwei.zwlibs.demo.entity.HttpParameters;
import com.dongfangwei.zwlibs.demo.presenter.MainPresenter;

public interface MainModel {
    void getData(HttpParameters parameters, MainPresenter presenter);
}
