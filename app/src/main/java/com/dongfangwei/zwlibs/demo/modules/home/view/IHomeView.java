package com.dongfangwei.zwlibs.demo.modules.home.view;

import com.dongfangwei.zwlibs.demo.publics.base.BaseView;

import java.util.List;

public interface IHomeView extends BaseView {
    void getDataSuccess(List<String> list);

    void getDataFailure(int code, String msg);
}
