package com.zhangwei.zwlibs.demo.view;

import java.util.List;

public interface MainView {
    void getDataSuccess(List<String> list);

    void getDataFailure(int code, String msg);
}
