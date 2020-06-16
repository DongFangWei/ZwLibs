package com.dongfangwei.zwlibs.demo.modules.main.model;


import com.dongfangwei.zwlibs.demo.modules.main.entity.HttpParameters;
import com.dongfangwei.zwlibs.demo.modules.main.presenter.MainPresenter;

public interface MainModel {
    void getData(HttpParameters parameters, MainPresenter presenter);
}
