package com.dongfangwei.zwlibs.demo.modules.home.presenter;

import com.dongfangwei.zwlibs.demo.publics.base.BasePresenter;

import java.util.List;

public interface IHomePresenter extends BasePresenter {
    void getData(int page, int size);

    void getDataSuccess(List<String> list);

    void getDataError(Throwable e);
}
