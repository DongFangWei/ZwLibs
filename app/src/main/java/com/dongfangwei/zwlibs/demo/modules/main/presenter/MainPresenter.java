package com.dongfangwei.zwlibs.demo.modules.main.presenter;

import java.util.List;

public interface MainPresenter {
    void getData(int page, int size);

    void getDataSuccess(List<String> list);

    void getDataFailure(Throwable e);

    void onDestroy();
}
