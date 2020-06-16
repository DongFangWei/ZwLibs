package com.dongfangwei.zwlibs.demo.modules.main.view;

import java.util.List;

public interface MainView {
    void getDataSuccess(List<String> list);

    void getDataFailure(int code, String msg);
}
